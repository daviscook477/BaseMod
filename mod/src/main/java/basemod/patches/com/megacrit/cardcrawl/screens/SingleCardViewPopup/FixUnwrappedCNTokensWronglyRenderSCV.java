package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCustomDynamicVariableCN;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.*;
import javassist.bytecode.*;
import javassist.convert.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FixUnwrappedCNTokensWronglyRenderSCV {
    private static final Logger logger = LogManager.getLogger(FixUnwrappedCNTokensWronglyRenderSCV.class);
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
    public static class FormatOnlySurroundedToken {

        private static boolean badCodeLocated = false;
        private static int timesLocated = 0;
        private static int firstLocated = -1;
        private static int localVarTmpIndex = -1;
        private static int localVarUpdateTmpIndex = -1;
        private static int localVarJIndex = -1;

        private static int ascii_D = 68;
        private static int ascii_M = 77;

        @SpireRawPatch
        public static void MakeRaw(CtBehavior ctBehavior) throws CannotCompileException, NotFoundException {
            // make sure only the surrounded tokens can be formatted
            CodeAttribute ca = ctBehavior.getMethodInfo().getCodeAttribute();
            LocalVariableAttribute localVarTable = (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag);
            // the old bad code is
            // if (tmp.chatAt(j) == 'D' || (tmp.charAt(j) == 'B' && !tmp.contains("[B]")) || tmp.charAt(j) == 'M') { // do bad things }
            // skip the whole bad logic to prevent it doing wrong thing
            // there are 2 places where the bad code lies in the source code
            // thankfully their logic are mostly the same, easy to locate

            if (localVarTmpIndex == -1 || localVarUpdateTmpIndex == -1 || localVarJIndex == -1) {
                for (int i = 0; i < localVarTable.tableLength(); i++) {
                    String varName = localVarTable.variableName(i);
                    if ("tmp".equals(varName)) {
                        localVarTmpIndex = localVarTable.index(i);
                    }
                    if ("updateTmp".equals(varName)) {
                        localVarUpdateTmpIndex = localVarTable.index(i);
                    }
                    if ("j".equals(varName)) {
                        localVarJIndex = localVarTable.index(i);
                    }
                }
            }

            ctBehavior.instrument(new CodeConverter() {{
                transformers = new CodeReplacement(transformers);
            }});
        }

        private static class CodeReplacement extends Transformer {

            public CodeReplacement(Transformer t) {
                super(t);
            }

            @Override
            public int transform(CtClass ctClass, final int index, CodeIterator iterator, ConstPool constPool) throws BadBytecode {
                if (timesLocated >= 2) {
                    return index;
                }
                CtClass stringClz;
                try {
                    stringClz = ctClass.getClassPool().get(String.class.getName());
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
                int codeAtCurrIndex = iterator.byteAt(index);
                int skipStartingIndex = -1;
                if (codeAtCurrIndex == BIPUSH && !badCodeLocated) {
                    int byteVal = iterator.byteAt(index + 1);
                    // the bad codes checks 'D' first so it is a head marker
                    if (byteVal == ascii_D) {
                        if (index < firstLocated) {
                            return index;
                        }
                        iterator.setMark(index);
                        // goes up, find the very head
                        int reverseIndex = index;
                        boolean locatedHead = false;
                        while (reverseIndex > 0) {
                            int reverseCode = iterator.byteAt(--reverseIndex);
                            // the bad logic loads "tmp" first using aload
                            if (reverseCode != ALOAD) continue;
                            int aloadValIndex = iterator.byteAt(reverseIndex + 1);
                            if (aloadValIndex == localVarTmpIndex) {
                                locatedHead = true;
                                // set the starting position of new logic
                                skipStartingIndex = reverseIndex;
                                break;
                            }
                        }
                        iterator.move(iterator.getMark());
                        if (locatedHead) {
                            iterator.setMark(iterator.lookAhead());
                            // goes down to check 'M' and if_icmpne
                            boolean locatedM = false;
                            boolean locatedIf = false;
                            while (iterator.hasNext() && !locatedIf) {
                                int nextPos = iterator.next();
                                int nextCode = iterator.byteAt(nextPos);
                                if (locatedM) {
                                    // after finding possible 'M', checks if its next is if
                                    if (nextCode != IF_ICMPNE) continue;
                                    locatedIf = true;
                                }
                                if (nextCode == BIPUSH) {
                                    int nextByteVal = iterator.byteAt(nextPos + 1);
                                    if (nextByteVal == ascii_M)
                                        locatedM = true;
                                }
                            }
                            badCodeLocated = locatedIf;
                            iterator.move(iterator.getMark());
                        }
                    }
                }
                if (badCodeLocated) {
                    Bytecode bc = new Bytecode(constPool);
                    bc.addInvokestatic(FormatOnlySurroundedToken.class.getName(), "FixingDBM", Descriptor.ofMethod(CtClass.booleanType, new CtClass[0]));
                    bc.add(Opcode.IFNE);
                    // leave for ifne, the index is to be located later
                    bc.addIndex(Opcode.NOP);
                    iterator.insertAt(skipStartingIndex, bc.get());
                    // now goes down again to find a goto (the break in the source code)
                    // need to add new logic after the goto
                    boolean locatedM = false;
                    boolean locatedIf = false;
                    boolean locatedBreak = false;
                    int gotoPos = -1;
                    while (iterator.hasNext() && !locatedBreak) {
                        int nextPos = iterator.next();
                        int nextCode = iterator.byteAt(nextPos);
                        if (locatedIf) {
                            if (nextCode != GOTO) continue;
                            locatedBreak = true;
                            gotoPos = nextPos;
                        }
                        if (locatedM) {
                            if (nextCode != IF_ICMPNE) continue;
                            locatedIf = true;
                        }
                        if (nextCode == BIPUSH) {
                            int nextByteVal = iterator.byteAt(nextPos + 1);
                            if (nextByteVal == ascii_M)
                                locatedM = true;
                        }
                    }
                    // two operators for goto
                    int destination = gotoPos + 3;
                    // add new logic
                    bc = new Bytecode(constPool);
                    // load "this" ref
                    bc.addAload(0);
                    bc.addAload(localVarTmpIndex);
                    bc.addIload(localVarJIndex);
                    bc.addInvokestatic(FormatOnlySurroundedToken.class.getName(), "GetCorrectWord", Descriptor.ofMethod(stringClz, new CtClass[] {ctClass, stringClz, CtClass.intType}));
                    bc.addAstore(localVarUpdateTmpIndex);
                    int skipEndLocation = iterator.insertAt(destination, bc.get());
                    iterator.move(gotoPos);
                    iterator.writeByte(Opcode.NOP, gotoPos + 1);
                    iterator.writeByte(Opcode.NOP, gotoPos + 2);
                    iterator.write16bit(skipEndLocation - gotoPos, gotoPos + 1);
                    iterator.move(skipStartingIndex);
                    while (iterator.hasNext() && iterator.byteAt(iterator.lookAhead()) != Opcode.IFNE)
                        iterator.next();
                    // correct ifne offset to new logic position
                    iterator.write16bit(destination - iterator.lookAhead(), iterator.lookAhead() + 1);
                    timesLocated++;
                    badCodeLocated = false;
                    firstLocated = gotoPos;
                }
                return index;
            }
        }

        // Thanks to Casey Yano, the god of the spire, for he keeps the bad codes almost the same as the ones in AbstractCard
        // So simply changing param AbstractCard to SCV is okay
        public static String GetCorrectWord(SingleCardViewPopup scv, String tmp, int j) {
            String text = tmp;
            try {
                AbstractCard card = ReflectionHacks.getPrivate(scv, SingleCardViewPopup.class, "card");
                // formatted tokens, whether well-formatted or not, should be converted into string values of the corresponding variables
                // so there shouldn't exist tokens like that but normal unformatted letters D, B and M.
                // Still, it's best to check if the word contains formatted tokens
                tmp = RenderCustomDynamicVariableCN.MatchVariablesAndReplace(card, text);
            } catch (Exception e) {
                logger.info("Failed to get correct word for {}: {}", tmp, e.getCause());
            }
            return tmp;
        }

        public static boolean FixingDBM() {
            return true;
        }

        private static int paramIndex = 1;

        private static String getParamName(LocalVariableAttribute table, String key) {
            int index = (table != null ? table.tableLength() : 0) + paramIndex++;
            return "_param_" + index + "_" + key;
        }
    }
}