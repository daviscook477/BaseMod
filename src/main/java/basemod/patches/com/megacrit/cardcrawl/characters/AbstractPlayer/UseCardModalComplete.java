package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(
        cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
        method="useCard"
)
public class UseCardModalComplete
{
    public static void Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse)
    {
        if (BaseMod.modalChoiceScreen.isOpen) {
            AbstractDungeon.player.cardsPlayedThisTurn -= 1;
            AbstractDungeon.actionManager.cardsPlayedThisCombat.remove(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);

            BaseMod.modalChoiceScreen.close();
        }
    }
}
