package basemod.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ModalChoiceCard extends AbstractCard
{
    public static final String ID = "ModalChoiceCard";
    private int index;
    ModalChoice.Callback callback;

    public ModalChoiceCard(String name, String rawDescription, CardType type, CardColor color, CardTarget target)
    {
        this(ID, name, rawDescription, type, color, target, -1, null);
    }

    ModalChoiceCard(String name, String rawDescription, CardType type, CardColor color, CardTarget target, int index, ModalChoice.Callback callback)
    {
        this(ID, name, rawDescription, type, color, target, index, callback);
    }

    ModalChoiceCard(String id, String name, String rawDescription, CardType type, CardColor color, CardTarget target, int index, ModalChoice.Callback callback)
    {
        super(id, name, null, null, -2, rawDescription, type, color, CardRarity.SPECIAL, target);
        dontTriggerOnUseCard = true;
        if (type != CardType.POWER) {
            purgeOnUse = true;
        } else {
            purgeOnUse = false;
        }

        this.index = index;
        this.callback = callback;
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster)
    {
        if (callback != null) {
            callback.optionSelected(abstractPlayer, abstractMonster, index);
        }
    }

    @Override
    public void upgrade()
    {
        // NOP. Should never be upgraded
    }

    @Override
    public AbstractCard makeCopy()
    {
        return new ModalChoiceCard(name, rawDescription, type, color, target, index, callback);
    }
}
