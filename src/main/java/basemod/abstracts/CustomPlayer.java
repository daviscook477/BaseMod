package basemod.abstracts;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import basemod.BaseMod;
import basemod.interfaces.ModelRenderSubscriber;

public abstract class CustomPlayer extends AbstractPlayer implements ModelRenderSubscriber {
	
	public Model myModel;
	public ModelInstance myInstance = null;
	public AnimationController controller = null;

	private String modelString = null;
	private String animationString = null;
	private boolean rescaled = false;
	public Texture deckTexture = null;
	
	private ArrayList<Texture> energyLayers = new ArrayList<Texture>();
	private Texture orbVfx;

	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath,
			String model, String animation) {
		super(name, playerClass);
		
		if (orbTextures == null || orbVfxPath == null) {
			buildDefaultOrb();
		} else {
			buildCustomOrb(orbTextures, orbVfxPath);
		}
		
		this.dialogX = (this.drawX + 0.0F * Settings.scale);
		this.dialogY = (this.drawY + 220.0F * Settings.scale);

		this.modelString = model;
		this.animationString = animation;
		
		if (modelString != null) {
			this.atlas = new TextureAtlas();
			
			BaseMod.subscribeToModelRender(this);
		}
	}
	
	public void create() {
		if (modelString == null) {
			return;
		}
		
		try {
			// Model loader needs a binary json reader to decode
			JsonReader jsonReader = new JsonReader();

			// Create a model loader passing in our json reader
			G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);

			// Now load the model by name
			myModel = modelLoader.loadModel(Gdx.files.internal(modelString));

			// Necessary to get transparent textures working - I don't know why
			for (Material mat : myModel.materials) {
				mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
			}

			// Now create an instance. Instance holds the positioning data, etc
			// of an instance of your model
			myInstance = new ModelInstance(myModel, 0, 0, 10.0f);

			// fbx-conv is supposed to perform this rotation for you... it
			// doesnt seem to
			myInstance.transform.rotate(1, 0, 0, -90);

			// only apply animations if provided a non-null animation to run
			if (animationString != null) {
				// You use an AnimationController to um, control animations. Each
				// control is tied to the model instance
				controller = new AnimationController(myInstance);
				// Pick the current animation by name
				controller.setAnimation(animationString, 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						// this will be called when the current animation is done.
						// Passing a negative to loop count loops forever. 1f for
						// speed is normal speed.
						controller.queue(animationString, -1, 1f, null, 0f);
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						// TODO Auto-generated method stub

					}

				});	
			}
		// catch block to prevent it failing silently
		} catch (Exception e) {
			e.printStackTrace();
			
			// not being able to load character image is a fatal error
			Gdx.app.exit();
		}
	}

	@Override
	public void receiveModelRender(ModelBatch batch, Environment env) {
		// do not render a model if provided a null model
		if (modelString == null) {
			return;
		}
		
		// do not render the model if it is no longer in play
		if (this != AbstractDungeon.player) {
			/*
			 *  calling unsubscribeFromModelRender inside the callback
			 *  for receiveModelRender means that when we're calling it
			 *  there is currently an iterator going over the list
			 *  of subscribers and calling receiveModelRender on each of
			 *  them therefore if we immediately try to remove the this
			 *  callback from the post battle subscriber list it will
			 *  throw a concurrent modification exception in the iterator
			 *  
			 *  for now we just add a delay - yes this is an atrocious solution
			 *  PLEASE someone with a better idea replace it
			 */
			Thread delayed = new Thread(() -> {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					System.out.println("could not delay unsubscribe to avoid ConcurrentModificationException");
					e.printStackTrace();
				}
				BaseMod.unsubscribeFromModelRender(this);
			});
			delayed.start();
		}
		
		// ensure loading is done before rendering
		if (myInstance == null) {
			create();
		}
		
		// update animations if animation is enabled
		if (animationString != null) {
			controller.update(Gdx.graphics.getDeltaTime());
		}
		
		// move player model to correct location on screen
		Vector3 loc = myInstance.transform.getTranslation(new Vector3());
		AbstractPlayer player = AbstractDungeon.player;
		if (player != null) {
			loc.x = player.drawX + player.animX - Gdx.graphics.getWidth() / 2;
			loc.y = player.drawY + player.animY + AbstractDungeon.sceneOffsetY - Gdx.graphics.getHeight() / 2;
			myInstance.transform.setTranslation(loc);
			// only scale *once*
			if (!rescaled) {
				// do the z scale instead of y scale - not entirely sure why but it probably has to do with
				// how blender and libgdx differ in their idea of what direction is "up"
				myInstance.transform.scale(Settings.scale, 1.0f, Settings.scale);
				rescaled = true;
			}
			batch.render(myInstance, env);
		}
	}

	
	@Override
	public void renderPlayerImage(SpriteBatch sb) {
		// do not render a model if provided a null model
		if (modelString == null) {
			super.renderPlayerImage(sb);
			return;
		}
		// when the game would attempt to render the player image
		// instead of doing that, go ahead and render the model instead
		BaseMod.publishAnimationRender(sb);
	}
	
	public Texture getOrbVfxTexture() {
		return orbVfx;
	}
	
	public static final String DEFAULT_ORB_VFX = "images/ui/topPanel/energyRedVFX.png";
	
	private void buildDefaultOrb() {
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER1);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER2);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER3);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER4);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER5);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER6);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER1D);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER2D);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER3D);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER4D);
		energyLayers.add(ImageMaster.ENERGY_RED_LAYER5D);

		orbVfx = ImageMaster.loadImage(DEFAULT_ORB_VFX);
	}
	
	private void buildCustomOrb(String[] orbTextures, String orbVfxPath) {
		for(String texPath : orbTextures) {
			energyLayers.add(new Texture(Gdx.files.internal(texPath)));
		}
		
		orbVfx = new Texture(Gdx.files.internal(orbVfxPath));
	}
	
	public void renderOrb(EnergyPanel panel, SpriteBatch sb) {
		sb.setColor(Color.WHITE);
		
		Field orb_scale;
		Field orb_angle_1;
		Field orb_angle_2;
		Field orb_angle_3;
		Field orb_angle_4;
		Field orb_angle_5;
		
		try {
			orb_scale = panel.getClass().getDeclaredField("ORB_IMG_SCALE");
			orb_scale.setAccessible(true);
			
			float ORB_IMG_SCALE = orb_scale.getFloat(panel);
			
			orb_angle_1 = panel.getClass().getDeclaredField("angle1");
			orb_angle_1.setAccessible(true);
			
			float angle1 = orb_angle_1.getFloat(panel);
			
			orb_angle_2 = panel.getClass().getDeclaredField("angle2");
			orb_angle_2.setAccessible(true);
			
			float angle2 = orb_angle_2.getFloat(panel);
			
			orb_angle_3 = panel.getClass().getDeclaredField("angle3");
			orb_angle_3.setAccessible(true);
			
			float angle3 = orb_angle_3.getFloat(panel);
			
			orb_angle_4 = panel.getClass().getDeclaredField("angle4");
			orb_angle_4.setAccessible(true);
			
			float angle4 = orb_angle_4.getFloat(panel);
			
			orb_angle_5 = panel.getClass().getDeclaredField("angle5");
			orb_angle_5.setAccessible(true);
			
			float angle5 = orb_angle_5.getFloat(panel);
			
			sb.setColor(Color.WHITE);
			
		    sb.draw(energyLayers.get(0), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle1, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(1), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle2, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(2), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle3, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(3), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle4, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(4), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle5, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(5), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, 0.0F, 0, 0, 128, 128, false, false);
		    
		    
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	public void renderDisabledOrb(EnergyPanel panel, SpriteBatch sb) {
		Field orb_scale, orb_angle_1, orb_angle_2, orb_angle_3,
				orb_angle_4, orb_angle_5;
		
		try {
			// access private fields off the EnergyPanel
			orb_scale = panel.getClass().getDeclaredField("ORB_IMG_SCALE");
			orb_scale.setAccessible(true);
			float ORB_IMG_SCALE = orb_scale.getFloat(panel);
			
			orb_angle_1 = panel.getClass().getDeclaredField("angle1");
			orb_angle_1.setAccessible(true);
			
			float angle1 = orb_angle_1.getFloat(panel);
			
			orb_angle_2 = panel.getClass().getDeclaredField("angle2");
			orb_angle_2.setAccessible(true);
			
			float angle2 = orb_angle_2.getFloat(panel);
			
			orb_angle_3 = panel.getClass().getDeclaredField("angle3");
			orb_angle_3.setAccessible(true);
			
			float angle3 = orb_angle_3.getFloat(panel);
			
			orb_angle_4 = panel.getClass().getDeclaredField("angle4");
			orb_angle_4.setAccessible(true);
			
			float angle4 = orb_angle_4.getFloat(panel);
			
			orb_angle_5 = panel.getClass().getDeclaredField("angle5");
			orb_angle_5.setAccessible(true);
			
			float angle5 = orb_angle_5.getFloat(panel);
			
			// actual rendering code
			sb.setColor(Color.WHITE);
		    sb.draw(energyLayers.get(6), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle1, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(7), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle2, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(8), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle3, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(9), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle4, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(10), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, angle5, 0, 0, 128, 128, false, false);
		    
		    sb.draw(energyLayers.get(5), panel.current_x - 64.0F, panel.current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, 0.0F, 0, 0, 128, 128, false, false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
}
