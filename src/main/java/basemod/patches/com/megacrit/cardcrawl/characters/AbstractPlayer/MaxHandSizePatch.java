package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.annotations.SerializedName;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.defect.ScrapeAction;
import com.megacrit.cardcrawl.actions.defect.SeekAction;
import com.megacrit.cardcrawl.actions.unique.AttackFromDeckToHandAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.actions.unique.ExhumeAction;
import com.megacrit.cardcrawl.actions.unique.SkillFromDeckToHandAction;
import com.megacrit.cardcrawl.actions.utility.DrawPileToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CannotCompileException;
import javassist.CodeConverter;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.*;
import javassist.convert.Transformer;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

@SpirePatch(
		clz=DrawCardAction.class,
		method="update"
)
@SpirePatch(
		clz=AbstractPlayer.class,
		method="draw",
		paramtypez={}
)
@SpirePatch(
		clz=MakeTempCardInHandAction.class,
		method="update"
)
@SpirePatch(
		clz=ScrapeAction.class,
		method="update"
)
@SpirePatch(
		clz=SeekAction.class,
		method="update"
)
@SpirePatch(
		clz=AttackFromDeckToHandAction.class,
		method="update"
)
@SpirePatch(
		clz=ExhumeAction.class,
		method="update"
)
@SpirePatch(
		clz=SkillFromDeckToHandAction.class,
		method="update"
)
@SpirePatch(
		clz=DrawPileToHandAction.class,
		method="update"
)
@SpirePatch(
		clz=DiscoveryAction.class,
		method="update"
)
public class MaxHandSizePatch
{
	public static class TransformBipush extends Transformer
	{
		//protected int lineNumber;
		protected int value;
		protected CodeAttribute codeAttr;

		public TransformBipush(Transformer next,  int value)
		{
			super(next);
			this.value = value;
		}

		@Override
		public void initialize(ConstPool cp, CodeAttribute attr)
		{
			codeAttr = attr;
		}

		@Override
		public int transform(CtClass clazz, int pos, CodeIterator iterator,
							 ConstPool cp) throws CannotCompileException, BadBytecode
		{
			//LineNumberAttribute ainfo = (LineNumberAttribute) codeAttr.getAttribute(LineNumberAttribute.tag);
			//if (lineNumber == ainfo.toLineNumber(pos)) {
			int c = iterator.byteAt(pos);
			if (c == BIPUSH) {
				int v = iterator.byteAt(pos + 1);
				if (v == value) {
					iterator.writeByte(Opcode.NOP, pos);
					iterator.writeByte(Opcode.NOP, pos + 1);
					Bytecode bytecode = new Bytecode(cp);
					bytecode.addGetstatic(BaseMod.class.getName(), "MAX_HAND_SIZE", Descriptor.of(CtClass.intType));
					iterator.insert(pos, bytecode.get());
					//System.out.println("EDITED BIPUSH@" + ainfo.toLineNumber(pos));
				} else {
					//System.out.println("FAILED BECAUSE BIPUSH@" + ainfo.toLineNumber(pos) + " != 10: " + v);
				}
			}
			//}

			return pos;
		}
	}

	public static class TestCodeConverter extends CodeConverter
	{
		public void bipush(int value)
		{
			transformers = new TransformBipush(transformers, value);
		}
	}

	public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException
	{
		TestCodeConverter codeConverter = new TestCodeConverter();
		codeConverter.bipush(10);
		ctMethodToPatch.instrument(codeConverter);
	}


	@SpirePatch(
			clz=CardGroup.class,
			method="refreshHandLayout"
	)
	public static class RefreshHandLayout
	{
		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(Logger.class.getName()) && m.getMethodName().equals("info")) {
						m.replace(RefreshHandLayout.class.getName() + ".PositionCards(this);");
					}
				}
			};
		}

		@SuppressWarnings("unused")
		public static void PositionCards(CardGroup hand)
		{
			float middle = hand.size() / 2.0f - 0.5f;

			float index = 0;
			for (AbstractCard card : hand.group) {
				card.target_x = Settings.WIDTH / 2.0f + (AbstractCard.IMG_WIDTH_S * 0.5f * (index - middle));
				if (card.target_y < 0.0f * Settings.scale) {
					card.target_y = 0.0f * Settings.scale;
				}
				if (Math.abs(card.targetAngle) > 20) {
					card.targetAngle = Math.signum(card.targetAngle) * 20.0f;
				}
				card.targetDrawScale = 0.6375f - (hand.size() - 10) * 0.05f;
				index += 1;
			}
		}
	}

	@SpirePatch(
			clz=SaveFile.class,
			method=SpirePatch.CLASS
	)
	public static class SaveField
	{
		@SerializedName("basemod:max_hand_size")
		public static SpireField<Integer> maxHandSize = new SpireField<>(() -> BaseMod.MAX_HAND_SIZE);
	}

	@SpirePatch(
			clz=SaveAndContinue.class,
			method="save"
	)
	public static class Save
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"params"}
		)
		public static void Insert(SaveFile save, @ByRef HashMap<Object, Object>[] params)
		{
			params[0].put("basemod:max_hand_size", SaveField.maxHandSize.get(save));
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(SaveFile.class, "hand_size");
				return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
			}
		}
	}

	@SpirePatch(
			clz=CardCrawlGame.class,
			method="loadPlayerSave"
	)
	public static class Load
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"saveFile"}
		)
		public static void Insert(CardCrawlGame __instance, AbstractPlayer p, SaveFile saveFile)
		{
			BaseMod.MAX_HAND_SIZE = SaveField.maxHandSize.get(saveFile);
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(SaveFile.class, "hand_size");
				return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
			}
		}
	}
}
