package basemod;

import basemod.interfaces.PreUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.EarlyAccessPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModBadge implements RenderSubscriber, PreUpdateSubscriber {
	public static final Logger logger = LogManager.getLogger(ModBadge.class.getName());
	
    private Texture texture;
    private String modName;
    private String tip;
    
    private float x;
    private float y;
    private float w;
    private float h;
    
    private Hitbox hb;
    private ModPanel modPanel;
    
    public ModBadge(Texture t, float xPos, float yPos, String name, String auth, String desc, ModPanel modSettings) {
        modName = name;
        tip = auth + " NL " + desc;
        
        texture = t;
        x = xPos;
        y = yPos;
        w = t.getWidth();
        h = t.getHeight();
        
        hb = new Hitbox(x, y, w*Settings.scale, h*Settings.scale);
        modPanel = modSettings;
        
        logger.info("initialized mod badge for: " + modName);
        
        BaseMod.subscribe(this);
        
        logger.info("setup hooks for " + modName + " mod badge");
    }
    
    public void receiveRender(SpriteBatch sb) {
        if (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.MAIN_MENU && !EarlyAccessPopup.isUp && !BaseMod.modSettingsUp) { 
            sb.setColor(Color.WHITE); 
            sb.draw(texture, x, y, w*Settings.scale, h*Settings.scale);
            hb.render(sb);
        } else if (modPanel != null && modPanel.isUp) {
            modPanel.render(sb);
        }
    }
    
    public void receivePreUpdate() {
        if (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.MAIN_MENU && !EarlyAccessPopup.isUp && !BaseMod.modSettingsUp) {  
            hb.update();
            
            if (hb.justHovered) {
            	logger.info(modName + " badge hovered");
                CardCrawlGame.sound.playV("UI_HOVER", 0.75f);
            }
            
            if (hb.hovered) {
                TipHelper.renderGenericTip(x+(2*w), y+h, modName, tip);
                
                if (InputHelper.justClickedLeft) {
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.1f);
                    hb.clickStarted = true;
                }
            }
            
            if (hb.clicked) {
                hb.clicked = false;
                onClick();
            }
        } else if (modPanel != null && modPanel.isUp) {
            modPanel.update();
        }        
    }
    
    private void onClick() {
    	logger.info(modName + " badge clicked");
    	
        if (modPanel != null) {
            modPanel.oldInputProcessor = Gdx.input.getInputProcessor();
            BaseMod.modSettingsUp = true;
            modPanel.isUp = true;
            
            modPanel.onCreate();
            
            CardCrawlGame.mainMenuScreen.darken();
            CardCrawlGame.mainMenuScreen.hideMenuButtons();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.SETTINGS;
            CardCrawlGame.cancelButton.show("Close");
        }
    }
}