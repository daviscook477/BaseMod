package basemod.abstracts;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public abstract class CustomRelic extends AbstractRelic
{
	public CustomRelic(String id, Texture texture, RelicTier tier, LandingSound sfx)
	{
		super(id, "", tier, sfx);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		setTexture(texture);
	}

	public CustomRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx)
	{
		super(id, "", tier, sfx);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		outline.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		setTextureOutline(texture, outline);
	}

	public CustomRelic(String id, String imgName, RelicTier tier, LandingSound sfx)
	{
		super(id, imgName, tier, sfx);
	}

	public void setTexture(Texture t)
	{
		img = t;
		outlineImg = t;
	}

	public void setTextureOutline(Texture t, Texture o)
	{
		img = t;
		outlineImg = o;
	}

	@Override
	public AbstractRelic makeCopy() {
		try{
			return this.getClass().newInstance();
		}catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("BaseMod failed to auto-generate makeCopy for relic: " + relicId);
		}
	}
}