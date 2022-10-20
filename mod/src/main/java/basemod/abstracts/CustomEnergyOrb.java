package basemod.abstracts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;

public class CustomEnergyOrb implements EnergyOrbInterface
{
	private static final int ORB_W = 128;
	private static final float ORB_IMG_SCALE = 1.15f * Settings.scale;

	protected Texture baseLayer;
	protected Texture[] energyLayers;
	protected Texture[] noEnergyLayers;
	protected Texture orbVfx;
	protected float[] layerSpeeds;
	protected float[] angles;

	public CustomEnergyOrb(String[] orbTexturePaths, String orbVfxPath, float[] layerSpeeds)
	{
		if (orbTexturePaths == null || orbVfxPath == null) {
			// Default orb
			energyLayers = new Texture[5];
			noEnergyLayers = new Texture[5];

			baseLayer = ImageMaster.ENERGY_RED_LAYER6;

			energyLayers[0] = ImageMaster.ENERGY_RED_LAYER1;
			energyLayers[1] = ImageMaster.ENERGY_RED_LAYER2;
			energyLayers[2] = ImageMaster.ENERGY_RED_LAYER3;
			energyLayers[3] = ImageMaster.ENERGY_RED_LAYER4;
			energyLayers[4] = ImageMaster.ENERGY_RED_LAYER5;

			noEnergyLayers[0] = ImageMaster.ENERGY_RED_LAYER1D;
			noEnergyLayers[1] = ImageMaster.ENERGY_RED_LAYER2D;
			noEnergyLayers[2] = ImageMaster.ENERGY_RED_LAYER3D;
			noEnergyLayers[3] = ImageMaster.ENERGY_RED_LAYER4D;
			noEnergyLayers[4] = ImageMaster.ENERGY_RED_LAYER5D;

			orbVfx = ImageMaster.RED_ORB_FLASH_VFX;
		} else {
			assert orbTexturePaths.length >= 3;
			// Must be odd number of textures
			assert orbTexturePaths.length % 2 == 1;

			int middleIdx = orbTexturePaths.length / 2;

			energyLayers = new Texture[middleIdx];
			noEnergyLayers = new Texture[middleIdx];

			baseLayer = ImageMaster.loadImage(orbTexturePaths[middleIdx]);

			for (int i=0; i<middleIdx; ++i) {
				energyLayers[i] = ImageMaster.loadImage(orbTexturePaths[i]);
				noEnergyLayers[i] = ImageMaster.loadImage(orbTexturePaths[i + middleIdx + 1]);
			}

			orbVfx = ImageMaster.loadImage(orbVfxPath);
		}
		if (layerSpeeds == null) {
			layerSpeeds = new float[] {
					-20.0f,
					20.0f,
					-40.0f,
					40.0f,
					360.0f
			};
		}
		this.layerSpeeds = layerSpeeds;
		angles = new float[this.layerSpeeds.length];

		assert energyLayers.length == this.layerSpeeds.length;
	}

	public Texture getEnergyImage()
	{
		return orbVfx;
	}

	@Override
	public void updateOrb(int energyCount)
	{
		if (energyCount == 0) {
			angles[4] += Gdx.graphics.getDeltaTime() * layerSpeeds[0] / 4.0f;
			angles[3] += Gdx.graphics.getDeltaTime() * layerSpeeds[1] / 4.0f;
			angles[2] += Gdx.graphics.getDeltaTime() * layerSpeeds[2] / 4.0f;
			angles[1] += Gdx.graphics.getDeltaTime() * layerSpeeds[3] / 4.0f;
			angles[0] += Gdx.graphics.getDeltaTime() * layerSpeeds[4] / 4.0f;
		} else {
			angles[4] += Gdx.graphics.getDeltaTime() * layerSpeeds[0];
			angles[3] += Gdx.graphics.getDeltaTime() * layerSpeeds[1];
			angles[2] += Gdx.graphics.getDeltaTime() * layerSpeeds[2];
			angles[1] += Gdx.graphics.getDeltaTime() * layerSpeeds[3];
			angles[0] += Gdx.graphics.getDeltaTime() * layerSpeeds[4];
		}
	}

	@Override
	public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y)
	{
		sb.setColor(Color.WHITE);
		if (enabled) {
			for (int i=0; i<energyLayers.length; ++i) {
				sb.draw(energyLayers[i],
						current_x - 64.0f, current_y - 64.0f,
						64.0f, 64.0f,
						128.0f, 128.0f,
						ORB_IMG_SCALE, ORB_IMG_SCALE,
						angles[i],
						0, 0,
						ORB_W, ORB_W,
						false, false);
			}
		} else {
			for (int i=0; i<noEnergyLayers.length; ++i) {
				sb.draw(noEnergyLayers[i],
						current_x - 64.0f, current_y - 64.0f,
						64.0f, 64.0f,
						128.0f, 128.0f,
						ORB_IMG_SCALE, ORB_IMG_SCALE,
						angles[i],
						0, 0,
						ORB_W, ORB_W,
						false, false);
			}
		}
		sb.draw(baseLayer,
				current_x - 64.0f, current_y - 64.0f,
				64.0f, 64.0f,
				128.0f, 128.0f,
				ORB_IMG_SCALE, ORB_IMG_SCALE,
				0.0f,
				0, 0,
				ORB_W, ORB_W,
				false, false);
	}
}
