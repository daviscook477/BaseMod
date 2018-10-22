package basemod.abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public abstract class CustomOrb extends AbstractOrb {

    private String passiveDescription;
    private String evokeDescription;

    public CustomOrb(String ID, String NAME, int basePassiveAmount, int baseEvokeAmount, String passiveDescription,
                     String evokeDescription ,String imgPath){

        this.ID = ID;
        this.name = NAME;
        this.basePassiveAmount = basePassiveAmount;
        this.passiveAmount = this.basePassiveAmount;
        this.baseEvokeAmount = baseEvokeAmount;
        this.evokeAmount = this.baseEvokeAmount;
        this.passiveDescription = passiveDescription;
        this.evokeDescription = evokeDescription;
        this.img = ImageMaster.loadImage(imgPath);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.applyFocus();
        this.description = "#yPassive: " + passiveDescription + " NL " + "#yEvoke: " + evokeDescription;
    }


    @Override
    //Taken from frost orb and modified a bit. Works to draw the basic orb image.
    public void render(SpriteBatch sb) {
        sb.setColor(this.c);
        sb.draw(img, this.cX - 48.0F + this.bobEffect.y / 4.0F, this.cY - 48.0F + this.bobEffect.y / 4.0F, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, 0.0F, 0, 0, 96, 96, false, false);
        this.renderText(sb);
        this.hb.render(sb);
    }
}

