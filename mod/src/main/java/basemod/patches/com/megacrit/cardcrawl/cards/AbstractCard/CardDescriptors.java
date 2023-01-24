package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.List;

public class CardDescriptors {

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class CardDescriptorField {
        public static SpireField<List<String>> cardDescriptors = new SpireField<>(ArrayList::new);
    }

    public static List<String> getDescriptors(AbstractCard card) {
        return CardDescriptorField.cardDescriptors.get(card);
    }

    public static void addDescriptors(AbstractCard card, String... descriptors) {
        for (String s : descriptors) {
            CardDescriptorField.cardDescriptors.get(card).add(s);
        }
    }

    public static void removeDescriptors(AbstractCard card, String... descriptors) {
        for (String s : descriptors) {
            CardDescriptorField.cardDescriptors.get(card).remove(s);
        }
    }

    public static void removeAllDescriptors(AbstractCard card) {
        CardDescriptorField.cardDescriptors.get(card).clear();
    }

}
