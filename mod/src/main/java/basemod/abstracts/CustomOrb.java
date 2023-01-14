package basemod.abstracts;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomOrb extends AbstractOrb {
    private static final Map<String, Texture> orbTextures = new HashMap<>();

    protected String passiveDescription;
    protected String evokeDescription;

    public CustomOrb(String ID, String NAME, int basePassiveAmount, int baseEvokeAmount, String passiveDescription,
                     String evokeDescription, String imgPath) {
        this.ID = ID;
        this.name = NAME;
        this.basePassiveAmount = basePassiveAmount;
        this.passiveAmount = this.basePassiveAmount;
        this.baseEvokeAmount = baseEvokeAmount;
        this.evokeAmount = this.baseEvokeAmount;
        this.passiveDescription = passiveDescription;
        this.evokeDescription = evokeDescription;
        if (imgPath != null) {
            this.img = orbTextures.get(imgPath);
            if (this.img == null) {
                this.img = ImageMaster.loadImage(imgPath);
                orbTextures.put(imgPath, img);
            }
        }
        updateDescription();
    }

    public CustomOrb(String ID, String NAME, int basePassiveAmount, int baseEvokeAmount, String passiveDescription,
                     String evokeDescription) {
        this(ID, NAME, basePassiveAmount, baseEvokeAmount, passiveDescription, evokeDescription, null);
    }

    @Override
    public void updateDescription() {
        this.applyFocus();
        this.description = "#yPassive: " + passiveDescription + " NL " + "#yEvoke: " + evokeDescription;
    }


    @Override
    //Taken from frost orb and modified a bit. Works to draw the basic orb image.
    public void render(SpriteBatch sb) {
        if (img != null) {
            sb.setColor(this.c);
            sb.draw(img, this.cX - img.getWidth() / 2f + this.bobEffect.y / 4.0F, this.cY - img.getHeight() / 2f + this.bobEffect.y / 4.0F, img.getWidth() / 2f, img.getHeight() / 2f, img.getWidth(), img.getHeight(), this.scale, this.scale, 0.0F, 0, 0, img.getWidth(), img.getHeight(), false, false);
        }
        this.renderText(sb);
        this.hb.render(sb);
    }
}

