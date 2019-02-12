---
id: custom-character-animations
title: Custom Character Animations
---

## Spriter Animations

Get **Spriter** from https://brashmonkey.com/ and use it to create your animation. This will require having your character model split up into separate textures so you can move them about individually and some knowledge of skeletal animation. Try taking a look on youtube for "spriter tutorial". This is the official tutorial playlist from BrashMonkey https://www.youtube.com/playlist?list=PL8Vej0NhCcI5sxOT64-Z31LW59VUyAVfr. Once you've made your spriter file export it and copy it to the resources directory for your mod. Then when defining your class that **extends** `CustomPlayer` when you make your call to `super` you will want to use this constructor:

```Java
	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, float[] layerSpeeds, AbstractAnimation animation)
```

For the `animation` parameter you will want to pass an instance of `basemod.animations.SpriterAnimation` which has a very simple constructor: `public SpriterAnimation(String filepath)`. Simply pass the path to your spriter file to the `SpriterAnimation` constructor and you should be all set to be using animations made in **Spriter**.


## G3DJ Animations (Old, Difficult to use, and has a Performance Hit)

To make custom character animations you need Blender and fbx-conv:
* Blender (https://www.blender.org/)
* fbx-conv (https://github.com/libgdx/fbx-conv)

In Blender you should just create a bunch of textured quads with your character art on them. (You need to be able to import images as planes). Look up some tutorial for 2d animation to see how to do this and make animations. You need these animations: (TODO list required animations for StS).

You have to make your character quads in the xy-plane with the textures facing in the negative Z direction. Turn on `Backface Culling` in Blender so you can tell if your texture has its normals facing properly.

When exporting to `fbx` from Blender, you **should** be able to leave things at default but here are the requirements:
* scale should be `1.0` to start out, up the scale to what looks right as you test
* `Y-up`
* `forward: -Z`

Then when using fbx-conv to convert the fbx into either a g3db or g3dj file you need to use the `-f` flag for flipping textures because otherwise everything will be upside down.

There are two file formats that libgdx supports: `g3db` and `g3dj`. `g3db` is a binary format whereas `g3dj` is a json format. Right now we only support loading from the json format but I'll get `g3db` loading working soon enough.

Take a look at (https://github.com/gskleres/FruityMod-StS) to see an example of how to get the character to use these files once you've made them. This feature isn't particularly well supported yet so hop on the StS discord on the `unsupported-modding` channel if you need help with it.

Here's an example of how to load a g3dj file:

```java
		// Model loader needs a binary json reader to decode
		JsonReader jsonReader = new JsonReader();
		
		// Create a model loader passing in our json reader
		G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
		
		// Now load the model by name
		myModel = modelLoader.loadModel(Gdx.files.internal("data/seeker.g3dj"));

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

		// You use an AnimationController to um, control animations. Each
		// control is tied to the model instance
		controller = new AnimationController(myInstance);
		// Pick the current animation by name
		controller.setAnimation("bottom|idle", 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				// this will be called when the current animation is done.
				// queue up another animation called "Jump".
				// Passing a negative to loop count loops forever. 1f for
				// speed is normal speed.
				controller.queue("bottom|idle", -1, 1f, null, 0f);
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				// TODO Auto-generated method stub

			}

		});
```

Code required in the `receiveModelRender(ModelBatch batch, Environment env)` function:

```java
		controller.update(Gdx.graphics.getDeltaTime());
		batch.render(myInstance, env);
```