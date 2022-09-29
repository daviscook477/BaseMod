package basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;

public class FixIncorrectMouseStates
{
	@SpirePatch(
			clz = ScrollInputProcessor.class,
			method = "touchDown"
	)
	public static class TouchDown
	{
		public static boolean Replace(ScrollInputProcessor __instance, int screenX, int screenY, int pointer, int button)
		{
			if (button == 0) {
				InputHelper.touchDown = true;
			}
			return false;
		}
	}

	@SpirePatch(
			clz = ScrollInputProcessor.class,
			method = "touchUp"
	)
	public static class TouchUp
	{
		public static boolean Replace(ScrollInputProcessor __instance, int screenX, int screenY, int pointer, int button)
		{
			if (button == 0) {
				InputHelper.touchUp = true;
			}
			return false;
		}
	}

	@SpirePatch(
			clz = ScrollInputProcessor.class,
			method = "touchDragged"
	)
	public static class TouchDragged
	{
		public static boolean Replace(ScrollInputProcessor __instance, int screenX, int screenY, int pointer)
		{
			if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				InputHelper.isMouseDown = true;
			}
			return false;
		}
	}
}
