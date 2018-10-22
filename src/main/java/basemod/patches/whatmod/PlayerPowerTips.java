package basemod.patches.whatmod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

@SpirePatch(
    cls="com.megacrit.cardcrawl.core.AbstractCreature",
    method="renderPowerTips"
)
public class PlayerPowerTips
{
    private static String modName = null;

    @SpireInsertPatch(
        rloc=2,
        localvars={"p"}
    )
    public static void Insert(AbstractCreature __instance, SpriteBatch sb, AbstractPower power)
    {
        modName = WhatMod.findModName(power.getClass());
    }

    public static PowerTip alterTip(PowerTip tip)
    {
        if (WhatMod.enabled && modName != null) {
            tip.body = FontHelper.colorString(modName, "p") + " NL " + tip.body;
        }
        return tip;
    }

    @SpirePatch(
        cls="com.megacrit.cardcrawl.core.AbstractCreature",
        method="renderPowerTips"
    )
    public static class Nested
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                @Override
                public void edit(NewExpr e) throws CannotCompileException
                {
                    if (e.getClassName().equals("com.megacrit.cardcrawl.helpers.PowerTip")) {
                        e.replace("$_ = basemod.patches.whatmod.PlayerPowerTips.alterTip($proceed($$));");
                    }
                }
            };
        }
    }
}
