package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

public class ModLabel implements IUIElement{
    private Consumer<ModLabel> update;
    
    public ModPanel parent;
    public String text;
    public float x;
    public float y;
    public Color color;
    public BitmapFont font;
    
    public ModLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
    	this(labelText, xPos, yPos, Color.WHITE, FontHelper.buttonLabelFont, p, updateFunc);
    }
    
    public ModLabel(String labelText, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
    	this(labelText, xPos, yPos, color, FontHelper.buttonLabelFont, p, updateFunc);
    }
    
    public ModLabel(String labelText, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
    	this(labelText, xPos, yPos, Color.WHITE, font, p, updateFunc);
    }
    
    public ModLabel(String labelText, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        text = labelText;
        x = xPos*Settings.scale;
        y = yPos*Settings.scale;
        this.color = color;
        this.font = font;
        
        parent = p;
        update = updateFunc;
    }
    
    public void render(SpriteBatch sb) {
        FontHelper.renderFontLeftDownAligned(sb, this.font, text, x, y, this.color);
    }
    
    public void update() {
        update.accept(this);
    }
    
	@Override
	public int renderLayer() {
		return ModPanel.TEXT_LAYER;
	}

	@Override
	public int updateOrder() {
		return ModPanel.PRIORITY_UPDATE;
	}
}