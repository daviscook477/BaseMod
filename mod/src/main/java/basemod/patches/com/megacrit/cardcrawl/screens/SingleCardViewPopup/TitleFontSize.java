package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.util.HashMap;
import java.util.Map;

public class TitleFontSize
{
	public static FileHandle fontFile;

	private static Map<Class<? extends CustomCard>, BitmapFont> titleFontMap = new HashMap<>();
	private static BitmapFont savedFont;

	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderTitle"
	)
	public static class UseCustomFontSize
	{
		@SpireInsertPatch(
				rloc = 0,
				localvars = {"card"}
		)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard card)
		{
			savedFont = FontHelper.SCP_cardTitleFont_small;
			if (!card.isLocked && card.isSeen && card instanceof CustomCard) {
				BitmapFont titleFont = getTitleFont((CustomCard) card);
				if (titleFont != null) {
					FontHelper.SCP_cardTitleFont_small = titleFont;
				}
			}
		}

		public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb)
		{
			FontHelper.SCP_cardTitleFont_small = savedFont;
		}
	}

	@SpirePatch(
			clz=FontHelper.class,
			method="initialize"
	)
	public static class GrabFontFile
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"fontFile"}
		)
		public static void Insert(FileHandle localFontFile)
		{
			fontFile = localFontFile;
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "cardTitleFont");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	private static BitmapFont getTitleFont(CustomCard card)
	{
		if (card.getTitleFontSize() < 0) {
			return null;
		}

		BitmapFont font = titleFontMap.get(card.getClass());
		if (font == null) {
			font = generateTitleFont(card.getTitleFontSize() * 2);
			titleFontMap.put(card.getClass(), font);
		}
		return font;
	}

	private static BitmapFont generateTitleFont(float size)
	{
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.minFilter = Texture.TextureFilter.Linear;
		param.magFilter = Texture.TextureFilter.Linear;
		param.hinting = FreeTypeFontGenerator.Hinting.Slight;
		param.spaceX = 0;
		param.kerning = true;
		param.borderColor = new Color(0.35f, 0.35f, 0.35f, 1);
		param.borderWidth = 4 * Settings.scale;
		param.gamma = 0.9f;
		param.borderGamma = 0.9f;
		param.shadowColor = new Color(0, 0, 0, 0.25f);
		param.shadowOffsetX = Math.round(6 * Settings.scale);
		param.shadowOffsetY = Math.round(6 * Settings.scale);
		param.borderStraight = false;

		param.characters = "";
		param.incremental = true;
		param.size = Math.round(size * Settings.scale);

		FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);
		g.scaleForPixelHeight(param.size);

		BitmapFont font = g.generateFont(param);
		font.setUseIntegerPositions(false);
		font.getData().markupEnabled = true;
		if (LocalizedStrings.break_chars != null) {
			font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
		}

		return font;
	}
}
