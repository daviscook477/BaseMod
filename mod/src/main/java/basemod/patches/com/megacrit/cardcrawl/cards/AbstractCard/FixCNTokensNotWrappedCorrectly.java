package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.*;
import javassist.bytecode.*;
import javassist.convert.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FixCNTokensNotWrappedCorrectly {
    private static final Logger logger = LogManager.getLogger(FixCNTokensNotWrappedCorrectly.class);

    private static final String damage_var_id = " D ";
    
    /*
    This makes the source code in initializeDescriptionCN correctly deal with !D!, !B! and !M!.
    Now it would be " !D! ", " !B! ", " !M! " after initialization instead of bad D, !B!! or !M!!
     */
    @SpirePatch(clz = AbstractCard.class, method = "initializeDescriptionCN")
    public static class MakeSureTokenWrappedCorrectlyInCard {
        private static boolean located = false;
        private static int insertions = 0;
        private static int timesLocated = 0;

        @SpireRawPatch
        public static void MakeRaw(CtBehavior ctBehavior) throws CannotCompileException, NotFoundException {
            CtClass stringClz = ctBehavior.getDeclaringClass().getClassPool().get(String.class.getName());
            
            // surrounds the token !D! !B! and !M!
            ctBehavior.instrument(new CodeConverter() {{
                transformers = new Transformer(transformers) {
                    @Override
                    public int transform(CtClass ctClass, int index, CodeIterator iterator, ConstPool constPool) throws BadBytecode {
                        if (timesLocated >= 3) {
                            return index;
                        }
                        int codeAtCurrIndex = iterator.byteAt(index);
                        if (timesLocated < 3 && codeAtCurrIndex == LDC) {
                            int ldcIndex = iterator.byteAt(index + 1);
                            String ldcVal = constPool.getStringInfo(ldcIndex);
                            if (!located) {
                                located = "!D!".equals(ldcVal) || "!B!".equals(ldcVal) || "!M!".equals(ldcVal);
                            } else {
                                // source code always checks !D! first 
                                // so the first time located should land for !D!
                                // source code makes the word be like " D " which is so stupid for later matching
                                // here make it " !D! "
                                if (timesLocated <= 0) {
                                    if (" D ".equals(ldcVal)) {
                                        // javassist seems to refuse to add string constants with whitespace " " into constpool
//                                        int ldcValConstIndex = constPool.addStringInfo(" !D! ");
//                                        iterator.writeByte(ldcValConstIndex, index + 1);
                                        Bytecode bc = new Bytecode(constPool);
                                        bc.addInvokestatic(FixCNTokensNotWrappedCorrectly.class.getName(), "GetIdentifiedVarWord", Descriptor.ofMethod(stringClz, new CtClass[]{stringClz}));
                                        // insert after ldc and its index value
                                        iterator.insertAt(index + 2, bc.get());
                                        insertions++;
                                    }
                                } else {
                                    // source code makes the word be like " !B!! "
                                    // we need to make it " !B! " which is more correct
                                    if ("! ".equals(ldcVal)) {
//                                        int ldcValConstIndex = constPool.addStringInfo(" ");
//                                        iterator.writeByte(ldcValConstIndex, index + 1);
                                        Bytecode bc = new Bytecode(constPool);
                                        bc.addInvokestatic(FixCNTokensNotWrappedCorrectly.class.getName(), "GetIdentifiedVarWord", Descriptor.ofMethod(stringClz, new CtClass[]{stringClz}));
                                        // insert after ldc and its index value
                                        iterator.insertAt(index + 2, bc.get());
                                        insertions++;
                                    }
                                }

                                // only need to do two replacement each time located
                                if (insertions > 0 && insertions % 2 == 0) {
                                    located = false;
                                    timesLocated++;
                                    insertions = 0;
                                }
                            }
                        }
                        return index;
                    }
                };
            }});
        }
    }

    public static String GetIdentifiedVarWord(String identifier) {
        // should be only two kinds of identifier: " D " and "! "
        switch (identifier) {
            case damage_var_id:
                return " !D! ";
            case "! ":
                return " ";
            default:
                return identifier;
        }
    }
}
