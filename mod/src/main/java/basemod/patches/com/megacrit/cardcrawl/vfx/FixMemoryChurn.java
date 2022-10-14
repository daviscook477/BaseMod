package basemod.patches.com.megacrit.cardcrawl.vfx;

import basemod.Pair;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.scenes.TheBottomScene;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import com.megacrit.cardcrawl.scenes.TheEndingScene;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.ui.panels.DiscardPilePanel;
import com.megacrit.cardcrawl.ui.panels.DrawPilePanel;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.StunStarEffect;
import com.megacrit.cardcrawl.vfx.combat.UnknownParticleEffect;
import com.megacrit.cardcrawl.vfx.scene.*;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import java.util.HashMap;
import java.util.Map;

public class FixMemoryChurn
{
	private static final Class<?>[] handledVfx = new Class[] {
			// ui
			DiscardGlowEffect.class,
			GameDeckGlowEffect.class,
			CardGlowBorder.class,
			// intents
			DebuffParticleEffect.class,
			BuffParticleEffect.class,
			ShieldParticleEffect.class,
			UnknownParticleEffect.class,
			StunStarEffect.class,
			// torches
			TorchParticleLEffect.class,
			LightFlareLEffect.class,
			TorchParticleMEffect.class,
			LightFlareMEffect.class,
			TorchParticleSEffect.class,
			LightFlareSEffect.class,
			// main menu
			LogoFlameEffect.class,
			TitleDustEffect.class,
			// the bottom
			DustEffect.class,
			BottomFogEffect.class,
			// the city
			FireFlyEffect.class,
			FallingDustEffect.class,
			CeilingDustCloudEffect.class,
			// the beyond
			ShinySparkleEffect.class,
			WobblyCircleEffect.class,
	};
	private static final Pair<Class<?>, String>[] patches = new Pair[]{
			// ui
			new Pair(DiscardPilePanel.class, "updateVfx"),
			new Pair(DrawPilePanel.class, "updateVfx"),
			new Pair(AbstractCard.class, "updateGlow"),
			// intents
			new Pair(AbstractMonster.class, "updateIntentVFX"),
			// torches
			new Pair(InteractableTorchEffect.class, "update"),
			// main menu
			new Pair(TitleBackground.class, "updateFlame"),
			new Pair(TitleBackground.class, "updateDust"),
			// the bottom
			new Pair(TheBottomScene.class, "updateDust"),
			new Pair(TheBottomScene.class, "updateFog"),
			// the city
			new Pair(TheCityScene.class, "updateFireFlies"),
			new Pair(CeilingDustEffect.class, "update"),
			// the city
			new Pair(TheEndingScene.class, "updateParticles"),
	};

	// poolMap gets populated by a patch
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private static final Map<Class<?>, Pool<? extends AbstractGameEffect>> poolMap = new HashMap<>();

	private static final Pool<DiscardGlowEffect> pool_DiscardGlowEffect = new Pool<DiscardGlowEffect>()
	{
		@Override
		protected DiscardGlowEffect newObject()
		{
			return new DiscardGlowEffect(true);
		}
	};
	private static final Pool<GameDeckGlowEffect> pool_GameDeckGlowEffect = new Pool<GameDeckGlowEffect>()
	{
		@Override
		protected GameDeckGlowEffect newObject()
		{
			return new GameDeckGlowEffect(false);
		}
	};
	private static final Pool<CardGlowBorder> pool_CardGlowBorder = new Pool<CardGlowBorder>()
	{
		@Override
		protected CardGlowBorder newObject()
		{
			// placeholder shiv, I guess
			return new CardGlowBorder(new Shiv());
		}
	};
	private static final Pool<DebuffParticleEffect> pool_DebuffParticleEffect = new Pool<DebuffParticleEffect>()
	{
		@Override
		protected DebuffParticleEffect newObject()
		{
			return new DebuffParticleEffect(0, 0);
		}
	};
	private static final Pool<BuffParticleEffect> pool_BuffParticleEffect = new Pool<BuffParticleEffect>()
	{
		@Override
		protected BuffParticleEffect newObject()
		{
			return new BuffParticleEffect(0, 0);
		}
	};
	private static final Pool<ShieldParticleEffect> pool_ShieldParticleEffect = new Pool<ShieldParticleEffect>()
	{
		@Override
		protected ShieldParticleEffect newObject()
		{
			return new ShieldParticleEffect(0, 0);
		}
	};
	private static final Pool<UnknownParticleEffect> pool_UnknownParticleEffect = new Pool<UnknownParticleEffect>()
	{
		@Override
		protected UnknownParticleEffect newObject()
		{
			// placeholder shiv, I guess
			return new UnknownParticleEffect(0, 0);
		}
	};
	private static final Pool<StunStarEffect> pool_StunStarEffect = new Pool<StunStarEffect>()
	{
		@Override
		protected StunStarEffect newObject()
		{
			// placeholder shiv, I guess
			return new StunStarEffect(0, 0);
		}
	};
	private static final Pool<TorchParticleLEffect> pool_TorchParticleLEffect = new Pool<TorchParticleLEffect>()
	{
		@Override
		protected TorchParticleLEffect newObject()
		{
			return new TorchParticleLEffect(0, 0);
		}
	};
	private static final Pool<LightFlareLEffect> pool_LightFlareLEffect = new Pool<LightFlareLEffect>()
	{
		@Override
		protected LightFlareLEffect newObject()
		{
			return new LightFlareLEffect(0, 0);
		}
	};
	private static final Pool<TorchParticleMEffect> pool_TorchParticleMEffect = new Pool<TorchParticleMEffect>()
	{
		@Override
		protected TorchParticleMEffect newObject()
		{
			return new TorchParticleMEffect(0, 0);
		}
	};
	private static final Pool<LightFlareMEffect> pool_LightFlareMEffect = new Pool<LightFlareMEffect>()
	{
		@Override
		protected LightFlareMEffect newObject()
		{
			return new LightFlareMEffect(0, 0);
		}
	};
	private static final Pool<TorchParticleSEffect> pool_TorchParticleSEffect = new Pool<TorchParticleSEffect>()
	{
		@Override
		protected TorchParticleSEffect newObject()
		{
			return new TorchParticleSEffect(0, 0);
		}
	};
	private static final Pool<LightFlareSEffect> pool_LightFlareSEffect = new Pool<LightFlareSEffect>()
	{
		@Override
		protected LightFlareSEffect newObject()
		{
			return new LightFlareSEffect(0, 0);
		}
	};
	private static final Pool<LogoFlameEffect> pool_LogoFlameEffect = new Pool<LogoFlameEffect>()
	{
		@Override
		protected LogoFlameEffect newObject()
		{
			return new LogoFlameEffect();
		}
	};
	private static final Pool<TitleDustEffect> pool_TitleDustEffect = new Pool<TitleDustEffect>()
	{
		@Override
		protected TitleDustEffect newObject()
		{
			return new TitleDustEffect();
		}
	};
	private static final Pool<DustEffect> pool_DustEffect = new Pool<DustEffect>()
	{
		@Override
		protected DustEffect newObject()
		{
			return new DustEffect();
		}
	};
	private static final Pool<BottomFogEffect> pool_BottomFogEffect = new Pool<BottomFogEffect>()
	{
		@Override
		protected BottomFogEffect newObject()
		{
			return new BottomFogEffect(true);
		}
	};
	private static final Pool<FireFlyEffect> pool_FireFlyEffect = new Pool<FireFlyEffect>()
	{
		@Override
		protected FireFlyEffect newObject()
		{
			return new FireFlyEffect(Color.WHITE.cpy());
		}
	};
	private static final Pool<FallingDustEffect> pool_FallingDustEffect = new Pool<FallingDustEffect>()
	{
		@Override
		protected FallingDustEffect newObject()
		{
			return new FallingDustEffect(0, 0);
		}
	};
	private static final Pool<CeilingDustCloudEffect> pool_CeilingDustCloudEffect = new Pool<CeilingDustCloudEffect>()
	{
		@Override
		protected CeilingDustCloudEffect newObject()
		{
			return new CeilingDustCloudEffect(0, 0);
		}
	};
	private static final Pool<ShinySparkleEffect> pool_ShinySparkleEffect = new Pool<ShinySparkleEffect>()
	{
		@Override
		protected ShinySparkleEffect newObject()
		{
			return new ShinySparkleEffect();
		}
	};
	private static final Pool<WobblyCircleEffect> pool_WobblyCircleEffect = new Pool<WobblyCircleEffect>()
	{
		@Override
		protected WobblyCircleEffect newObject()
		{
			return new WobblyCircleEffect();
		}
	};

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void free(AbstractGameEffect vfx)
	{
		if (vfx.isDone) {
			Pool pool = poolMap.get(vfx.getClass());
			if (pool == null) {
				return;
			}
			pool.free(vfx);
		}
	}

	@SpirePatch2(
			clz = AbstractGameEffect.class,
			method = SpirePatch.CONSTRUCTOR
	)
	public static class SetupAndCreateAndFree
	{
		public static void Raw(CtBehavior ctBehavior) throws CannotCompileException, NotFoundException
		{
			ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
			CtClass ctChurn = pool.get(FixMemoryChurn.class.getName());
			CtConstructor sinit = ctChurn.getClassInitializer();

			for (Class<?> cls : handledVfx) {
				CtClass ctClass = pool.get(cls.getName());

				CtMethod update = ctClass.getDeclaredMethod("update");
				update.insertAfter(FixMemoryChurn.class.getName() + ".free(this);");

				// Add pool_ to poolMap
				sinit.insertAfter(
						"poolMap.put(" + cls.getName() + ".class, pool_" + cls.getSimpleName() + ");"
				);

				// Create setupSuper()
				CtMethod method = ctClass.getSuperclass().getDeclaredConstructor(new CtClass[]{}).toMethod("setupSuper", ctClass);
				ctClass.addMethod(method);

				// Create setup(...) and alloc_(...)
				for (CtConstructor ctor : ctClass.getDeclaredConstructors()) {
					method = ctor.toMethod("setup", ctClass);
					if (ctor.callsSuper()) {
						method.insertBefore("setupSuper();");
					}

					ctClass.addMethod(method);
					method = CtNewMethod.make(
							Modifier.PUBLIC | Modifier.STATIC,
							ctClass, // return type
							"alloc_" + ctClass.getSimpleName(), // name
							ctor.getParameterTypes(), // param types
							null,
							"{" +
									ctClass.getName() + " ret = (" + ctClass.getName() + ") pool_" + ctClass.getSimpleName() + ".obtain();" +
									"ret.setup($$);" +
									"return ret;" +
									"}",
							ctChurn
					);
					ctChurn.addMethod(method);
				}
			}

			// Replace appropriate new calls with alloc_
			for (Pair<Class<?>, String> patch : patches) {
				CtClass ctClass = pool.get(patch.getKey().getName());
				CtMethod method = ctClass.getDeclaredMethod(patch.getValue());
				method.instrument(exprEditor);
			}
		}

		private static final ExprEditor exprEditor = new ExprEditor() {
			@Override
			public void edit(NewExpr e) throws CannotCompileException
			{
				for (Class<?> cls : handledVfx) {
					if (e.getClassName().equals(cls.getName())) {
						e.replace("$_ = " + FixMemoryChurn.class.getName() + ".alloc_" + cls.getSimpleName() + "($$);");
						break;
					}
				}
			}
		};
	}
}
