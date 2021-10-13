package basemod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModPanel {
    
	public static final int BACKGROUND_LAYER = 0;
	public static final int MIDDLE_LAYER = 1;
	public static final int TEXT_LAYER = 2;
	
	public static final int PRIORITY_UPDATE = 0;
	public static final int DEFAULT_UPDATE = 1;
	public static final int LATE_UPDATE = 2;
	
    private static Texture background;
    
    /*
     * we use ArrayList with Collections.sort on every insert instead of a TreeSet
     * for maintaining an ordered list because TreeSet has the unfortunate set property
     * of ignoring duplicates which it defines by things that are equal using its comparator
     * 
     * we use 2 ArrayLists so we can have separate render orderings and update orderings
     * for the IUIElement(s)
     */
    private static Comparator<IUIElement> renderComparator = new Comparator<IUIElement>() {

		@Override
		public int compare(IUIElement obj0, IUIElement obj1) {
			return (obj0.renderLayer() - obj1.renderLayer());
		}
    	
    };
    private static Comparator<IUIElement> updateComparator = new Comparator<IUIElement>() {

		@Override
		public int compare(IUIElement obj0, IUIElement obj1) {
			return (obj0.updateOrder() - obj1.updateOrder());
		}
    	
    };
    private ArrayList<IUIElement> uiElementsRender;
    private ArrayList<IUIElement> uiElementsUpdate;
    
    private Consumer<ModPanel> createFunc;
    
    public InputProcessor oldInputProcessor = null;
    public boolean isUp = false;
    
    public HashMap<String, Integer> state;
    
    // DEPRECATED
    public boolean waitingOnEvent = false;
     
    public ModPanel() {
    	this((me) -> {});
    }
    
    public ModPanel(Consumer<ModPanel> createFunc) {
        background = ImageMaster.loadImage("img/ModPanelBg.png");
        
        uiElementsRender = new ArrayList<>();
        uiElementsUpdate = new ArrayList<>();
        
        state = new HashMap<>();
        
        this.createFunc = createFunc;
    }
    
    private boolean created = false;
    
    // lets mods do things the first time the panel is pulled up
    public void onCreate() {
    	if (!created) {
        	this.createFunc.accept(this);
        	created = true;
    	}
    }
    
    public void addUIElement(IUIElement element) {
    	uiElementsRender.add(element);
    	Collections.sort(uiElementsRender, renderComparator);
    	uiElementsUpdate.add(element);
    	Collections.sort(uiElementsUpdate, updateComparator);
    }
    
    public void render(SpriteBatch sb) {
        // Background pane
        renderBg(sb);
        
        // Render UI elements
        // TreeSet maintains an ordering on inserted elements at insertion time
        // and IUIElement specifies a layer so iterating through and rendering each
        // element in turn will render the smallest layers first and then the higher layers
        for (IUIElement elem : uiElementsRender) {
        	elem.render(sb);
        }
    }
    
    public void renderBg(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(background, 278f * Settings.scale, Settings.OPTION_Y - 376.0f, 0f, 376.0f, 1364.0f, 752.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 1364, 752, false, false);
    }
    
    public void update() {
        // Update UI elements
        // TreeSet maintains an ordering on inserted elements at insertion time
        // and IUIElement specifies a layer so iterating through and updating each
        // element in turn will update the smallest layers first and then the higher layers
        for (IUIElement elem : uiElementsUpdate) {
        	elem.update();
        }
        
        if (InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            BaseMod.modSettingsUp = false;
        }
        
        if (!BaseMod.modSettingsUp) {
            waitingOnEvent = false;
            Gdx.input.setInputProcessor(oldInputProcessor);
            CardCrawlGame.mainMenuScreen.lighten();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.cancelButton.hideInstantly();
            isUp = false;
        }
    }

    public ArrayList<IUIElement> getUIElements() {
        ArrayList<IUIElement> retVal = new ArrayList<>();
        retVal.addAll(uiElementsRender);
        retVal.addAll(uiElementsUpdate);

        return retVal.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }
}