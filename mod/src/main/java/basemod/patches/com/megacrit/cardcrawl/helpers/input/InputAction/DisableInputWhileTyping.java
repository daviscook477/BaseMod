package basemod.patches.com.megacrit.cardcrawl.helpers.input.InputAction;

import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.input.InputAction;

public class DisableInputWhileTyping {
	@SpirePatch(
			clz = InputAction.class,
			method = "isJustPressed"
	)
	public static class onInitialPress
	{
		@SpirePrefixPatch
		public static SpireReturn<Boolean> preventInitialPress(InputAction __instance)
		{
			if (TextInput.isTextInputActive())
			{
				return SpireReturn.Return(false);
			}
			return SpireReturn.Continue();
		}
	}
	@SpirePatch(
			clz = InputAction.class,
			method = "isPressed"
	)
	public static class onPress
	{
		@SpirePrefixPatch
		public static SpireReturn<Boolean> preventPress(InputAction __instance)
		{
			if (TextInput.isTextInputActive())
			{
				return SpireReturn.Return(false);
			}
			return SpireReturn.Continue();
		}
	}
}
