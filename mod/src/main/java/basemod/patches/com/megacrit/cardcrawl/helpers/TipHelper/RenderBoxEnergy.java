package basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.TipHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        clz=TipHelper.class,
        method="renderBox"
)
public class RenderBoxEnergy
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"card", "currentOrb"}
    )
	public static void Insert(SpriteBatch sb, String word, float x, float y, AbstractCard card, @ByRef TextureAtlas.AtlasRegion[] currentOrb)
    {
        currentOrb[0] = BaseMod.getCardSmallEnergy(card);
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher matcher = new Matcher.MethodCallMatcher(String.class, "equals");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}
