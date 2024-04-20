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
    public static SpireField<Boolean> horizontal = new SpireField<>(() -> false);

    public static void add(AbstractCard card, AbstractCard... previews) {
        add(card, false, previews);
    }

    public static void add(AbstractCard card, boolean horizontalOnly, AbstractCard... previews) {
        multiCardPreview.get(card).addAll(Arrays.asList(previews));
        horizontal.set(card, horizontalOnly);
    }

    public static void remove(AbstractCard card, AbstractCard preview) {
        multiCardPreview.get(card).remove(preview);
    }

    public static void clear(AbstractCard card) {
        multiCardPreview.get(card).clear();
    }

    public static void horizontalOnly(AbstractCard card) {
        horizontalOnly(card, true);
    }

    public static void horizontalOnly(AbstractCard card, boolean horizontal) {
        MultiCardPreview.horizontal.set(card, horizontal);
    }

}
