package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.Pair;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.Field;
import java.util.function.Predicate;

@SpirePatch(
		clz=AbstractCard.class,
		method="renderCard"
)
public class RenderRelicOnCard
{
	private static Field relicRotation;

	static {
		try {
			relicRotation = AbstractRelic.class.getDeclaredField("rotation");
			relicRotation.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void Postfix(AbstractCard __instance, SpriteBatch sb, boolean hovered, boolean selected)
	{
		if (!Settings.hideCards /*&& !__instance.isOnScreen()*/ && !__instance.isFlipped) {
			for (Pair<Predicate<AbstractCard>, AbstractRelic> info : BaseMod.getBottledRelicList()) {
				if (info.getKey().test(__instance)) {
					AbstractRelic r = info.getValue();
					r.scale = __instance.drawScale * 0.8f;
					try {
						relicRotation.set(r, __instance.angle);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

					Vector2 tmp = new Vector2(135.0f, 185.0f);
					tmp.scl(__instance.drawScale * Settings.scale);
					tmp.rotate(__instance.angle);
					r.currentX = __instance.current_x + tmp.x;
					r.currentY = __instance.current_y + tmp.y;
					r.render(sb);
				}
			}
		}
	}
}
