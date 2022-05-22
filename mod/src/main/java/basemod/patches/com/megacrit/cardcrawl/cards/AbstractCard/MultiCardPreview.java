package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = AbstractCard.class,
        method = SpirePatch.CLASS
)
public class MultiCardPreview {

    public static SpireField<ArrayList<AbstractCard>> multiCardPreview = new SpireField<>(ArrayList::new);

    public static void add(AbstractCard card, AbstractCard... previews) {
        multiCardPreview.get(card).addAll(Arrays.asList(previews));
    }

    public static void remove(AbstractCard card, AbstractCard preview) {
        multiCardPreview.get(card).remove(preview);
    }

    public static void clear(AbstractCard card) {
        multiCardPreview.get(card).clear();
    }

}
