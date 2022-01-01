package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.ExhaustPileViewScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class DynamicTextBlocks {
    private static final String REGEX = "\\{!.*?!\\|.*?}";
    private static final String DYNAMIC_KEY = "{@@}";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    //TODO: Efficiency improvements?

    //Spire Field for fixing the Location var in ExhaustPileViewScreen thanks to the fact it returns a copy of the card that isn't actually in the Exhaust pile when you pull up the screen
    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class ExhaustViewFixField {
        public static final SpireField<Boolean> exhaustViewCopy = new SpireField<>(() -> Boolean.FALSE);
    }

    //Spire Field for if dynamic text was found. This allows faster checking since we don't need to regex each time
    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class DynamicTextField {
        public static final SpireField<Boolean> isDynamic = new SpireField<>(() -> Boolean.FALSE);
    }

    //When we render said card copy, set its field and initialize description
    @SpirePatch(clz = ExhaustPileViewScreen.class, method = "open")
    public static class FixExhaustText {
        @SpireInsertPatch(locator = Locator.class, localvars = "toAdd")
        public static void setField(ExhaustPileViewScreen __instance, AbstractCard toAdd) {
            if (DynamicTextField.isDynamic.get(toAdd)) {
                ExhaustViewFixField.exhaustViewCopy.set(toAdd, true);
                toAdd.initializeDescription();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToBottom");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    //Allows dynamic text to work with powers while in your hand and when targeting enemies
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class UpdateTextForPowers {
        @SpirePostfixPatch
        public static void updateAfterVarChange(AbstractCard __instance) {
            if (DynamicTextField.isDynamic.get(__instance)) {
                __instance.initializeDescription();
            }
        }
    }

    //Allows !L! to set its location properly by forcing an update when the deck is initialized at the start of combat
    @SpirePatch(clz = CardGroup.class, method = "initializeDeck")
    public static class UpdateTextOnDeckCreation {
        @SpirePostfixPatch()
        public static void updateAfterAdding(CardGroup __instance, CardGroup masterDeck) {
            for (AbstractCard c : __instance.group) {
                if (DynamicTextField.isDynamic.get(c)) {
                    c.initializeDescription();
                }
            }
        }
    }

    //Force a Location update when a card is exhausted.
    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class UpdateTextOnExhaust{
        @SpirePostfixPatch()
        public static void updateAfter(CardGroup __instance, AbstractCard c) {
            if (DynamicTextField.isDynamic.get(c)) {
                c.initializeDescription();
            }
        }
    }

    //Patch to take place in for the foreach loop. Takes place after card mod patch
    @SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
    @SpirePatch(clz = AbstractCard.class, method = "initializeDescriptionCN")
    public static class ProcessDynamicText {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(String.class.getName()) && m.getMethodName().equals("split")) {
                        m.replace("{ $_ = " + basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.DynamicTextBlocks.class.getName()+".checkForUnwrapping(this, $proceed($$)); }");
                    }
                }
            };
        }
    }

    public static String[] checkForUnwrapping(AbstractCard c, String[] splitText) {
        //If the string contains the key, or is known to contain the key, we need to unpack it
        if (DynamicTextField.isDynamic.get(c)) {
            //Replace all the dynamic text blocks with their unwrapped equivalent
            java.util.regex.Matcher m = PATTERN.matcher(String.join(" ", splitText).replace(DYNAMIC_KEY,""));
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, unwrap(c, m.group()));
            }
            m.appendTail(sb);
            return sb.toString().split(" ");
        } else if (Arrays.stream(splitText).anyMatch(s -> s.contains(DYNAMIC_KEY))) {
            DynamicTextField.isDynamic.set(c, true);
            return checkForUnwrapping(c, splitText);
        } else {
            //Else just return the split
            return splitText;
        }
    }

    public static String unwrap(AbstractCard c, String key) {
        //Cut the leading { and the trailing } and then split the string along each |
        key = key.substring(1, key.length()-1);
        String[] parts = key.split("\\|");
        //Our first piece will always be the dynamic variable we care about. Find its value
        Integer var = null;
        if (parts[0].equals("!D!")) {
            //Uses !D! for damage, just like normal dynvars, same applies to !B! and !M!
            var = c.damage;
        } else if (parts[0].equals("!B!")) {
            var = c.block;
        } else if (parts[0].equals("!M!")) {
            var = c.magicNumber;
        } else if (parts[0].equals("!Location!")) {
            //Used to grab the location of the card. Isn't a real dynvar, but we can pretend
            var = -1; //Master Deck, Compendium, Limbo, modded CardGroups
            if (CardCrawlGame.dungeon != null && AbstractDungeon.player != null) {
                if (AbstractDungeon.player.hand.contains(c)) {
                    var = 0;
                } else if (AbstractDungeon.player.drawPile.contains(c)) {
                    var = 1;
                } else if (AbstractDungeon.player.discardPile.contains(c)) {
                    var = 2;
                } else if (AbstractDungeon.player.exhaustPile.contains(c) || ExhaustViewFixField.exhaustViewCopy.get(c)) {
                    //This is where we need the field. Without it this will default back to -1 as the cards shown in the Exhaust View are copies that aren't actually in the exhaust pile
                    var = 3;
                }
            }
        } else if (parts[0].equals("!Upgrades!")) {
            //Used to grab the amount of times the card was upgraded. Again, isn't a real dynvar
            var = c.timesUpgraded;
        } else if (BaseMod.cardDynamicVariableMap.containsKey(parts[0].replace("!",""))) {
            //Check to see if it's a recognized dynvar registered by some mod
            var = BaseMod.cardDynamicVariableMap.get(parts[0].replace("!","")).value(c);
        }
        //Clean up the first string since we don't need it
        parts = Arrays.copyOfRange(parts, 1, parts.length);
        //If we found a var then we can do stuff, else just return an empty string
        key = "";
        if (var != null) {
            //Define a matched flag. Lets us know to NOT overwrite the output string with the default output if it actually matched a case
            boolean matched = false;
            //Iterate each case. Note that the right most matching case will be the output string, or the default output if no cases match
            for (String s : parts) {
                //All cases need to contain a single =, which we use for splitting the case into its values and output
                if (s.contains("=")) {
                    String[] split = s.split("=");
                    //Check if the condition has commas, an indicator of a case having multiple conditions.
                    if (split[0].contains(",")) {
                        String[] numbers = split[0].split(",");
                        //Iterate each condition, as long as at least 1 condition matches we set the text
                        for (String n : numbers) {
                            if(checkMatch(var, n)) {
                                //Checking the length allows up to know if there is actual text or just an empty string
                                if (split.length > 1) {
                                    key = split[1];
                                } else {
                                    key = "";
                                }
                                matched = true;
                            }
                        }
                    } else {
                        //Else just check the condition directly
                        if (checkMatch(var, split[0])) {
                            if (split.length > 1) {
                                key = split[1];
                            } else {
                                key = "";
                            }
                            matched = true;
                        }
                    }
                    //If this is the default output designated by @= then set this as the output text if we haven't matched anything else yet.
                    if (split[0].equals("@") && !matched) {
                        if (split.length > 1) {
                            key = split[1];
                        } else {
                            key = "";
                        }
                    }
                }
            }
        }
        return key;
    }

    private static boolean checkMatch(Integer var, String s) {
        //Greater Than, Less Than, Divisible By, and Ends With are the 4 supported conditional checks, in addition to Direct Match, which has no symbol attached
        boolean greater = s.contains(">");
        boolean less = s.contains("<");
        boolean mod = s.contains("%");
        boolean ends = s.contains("&");
        //Remove all these special symbols before checking if the condition is a number we can make
        s = s.replace(">", "").replace("<", "").replace("%", "").replace("&","");
        if (NumberUtils.isCreatable(s)) {
            //Grab our var minus the number, helpful for comparisons
            int comp = var.compareTo(NumberUtils.createInteger(s));
            //Checks the Direct Match, Greater Than, and Less Than cases
            boolean signCheck = !greater && !less && comp == 0 || greater && comp > 0 || less && comp < 0;
            //Checks the Divisible By case. If the numbers are equal (comp is 0), or if variable mod N is 0, then its divisible
            boolean moduloCheck = mod && (comp == 0 || var % NumberUtils.createInteger(s) == 0);
            //Checks the Ends With case. If the numbers are equal, or if the trailing digits of comp are all 0's, then var ends with N
            boolean digitCheck = ends && (comp == 0 || comp % Math.pow(10, s.length()) == 0);
            //As long as at least one condition matches we are good
            return signCheck || moduloCheck || digitCheck;
        }
        //If it's not a creatable number, there was a format error. Just return false instead of blowing up
        return false;
    }
}
