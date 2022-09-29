package basemod.devcommands.statics;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.evacipated.cardcrawl.modthespire.Loader;
import javassist.*;
import javassist.bytecode.*;
import javassist.convert.Transformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//Invoke code and output the result as toString
public class EvalCode extends ConsoleCommand
{
    public static class OurCode {}

    private static int evalCount = 0;

    public EvalCode() {
        minExtraTokens = 1;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        List<String> imports = new ArrayList<>();
        for (Iterator<String> it = Loader.getClassPool().getImportedPackages(); it.hasNext(); ) {
            String s = it.next();
            imports.add(s);
        }
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl");
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl.core");
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl.dungeons");
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl.cards");
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl.powers");
        Loader.getClassPool().importPackage("com.megacrit.cardcrawl.relics");

        String src =
                "public static Object eval() {" +
                "return " + String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        if (!tokens[1].endsWith(";")) {
            src += ";";
        }
        src += "}";
        System.out.println(src);
        try {
            CtClass ctClass = Loader.getClassPool().makeClass(OurCode.class.getName() + "$Eval" + evalCount);

            CtMethod ctMethod = CtNewMethod.make(src, ctClass);
            ctClass.addMethod(ctMethod);
            CodeAttribute code = ctMethod.getMethodInfo().getCodeAttribute();
            boolean stackUnderflow = false;
            try {
                code.computeMaxStack();
            } catch (BadBytecode e) {
                if (e.getMessage().startsWith("stack underflow")) {
                    stackUnderflow = true;
                } else {
                    throw e;
                }
            }
            ReturnCodeConvertor codeConvertor = new ReturnCodeConvertor(stackUnderflow);
            ctMethod.instrument(codeConvertor);

            Class<?> cls = ctClass.toClass(EvalCode.class.getClassLoader(), null);
            Method method = cls.getMethod("eval");
            Object output = method.invoke(null);
            if (output == null) {
                DevConsole.log("Output: null");
            } else {
                DevConsole.log("Output: " + output);
            }
        } catch (CannotCompileException e ) {
            DevConsole.log("Cannot compile: " + e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            DevConsole.log("Exception in eval code: " + e.getCause());
            e.getCause().printStackTrace();
        } catch (Exception | Error e) {
            DevConsole.log("Something bad happened. Check the log");
            e.printStackTrace();
        } finally {
            Loader.getClassPool().clearImportedPackages();
            imports.remove(0); // removes the "java.lang" import because it gets added by clearImportedPackages
            for (String s : imports) {
                System.out.println(s);
                Loader.getClassPool().importPackage(s);
            }
        }

        ++evalCount;
    }

    private static class ReturnCodeConvertor extends CodeConverter
    {
        public ReturnCodeConvertor(boolean stackUnderflow)
        {
            transformers = new TransformPrimitiveReturn(transformers, stackUnderflow);
        }

        private static class TransformPrimitiveReturn extends Transformer
        {
            private final boolean voidReturn;

            public TransformPrimitiveReturn(Transformer t, boolean stackUnderflow)
            {
                super(t);
                voidReturn = stackUnderflow;
            }

            @Override
            public int transform(CtClass clazz, int pos, CodeIterator iterator,
                                 ConstPool cp) throws CannotCompileException, BadBytecode
            {
                ClassPool pool = clazz.getClassPool();
                int c = iterator.byteAt(pos);
                CtPrimitiveType primitiveType = null;
                if (c == IRETURN && voidReturn) {
                    c = RETURN;
                }
                switch (c) {
                    case IRETURN: // int
                        primitiveType = (CtPrimitiveType) CtClass.intType;
                        break;
                    case LRETURN: // long
                        primitiveType = (CtPrimitiveType) CtClass.longType;
                        break;
                    case FRETURN: // float
                        primitiveType = (CtPrimitiveType) CtClass.floatType;
                        break;
                    case DRETURN: // double
                        primitiveType = (CtPrimitiveType) CtClass.doubleType;
                        break;
                    case RETURN: // void
                    {
                        iterator.writeByte(ARETURN, pos);
                        Bytecode bytecode = new Bytecode(cp);
                        bytecode.add(ACONST_NULL);
                        iterator.insert(pos, bytecode.get());
                        break;
                    }
                }
                if (primitiveType != null) {
                    iterator.writeByte(NOP, pos);
                    Bytecode bytecode = new Bytecode(cp);
                    try {
                        CtClass wrapperType = pool.get(primitiveType.getWrapperName());
                        bytecode.addInvokestatic(wrapperType, "valueOf", wrapperType, new CtClass[] { primitiveType });
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    bytecode.add(ARETURN);
                    iterator.insert(pos + 1, bytecode.get());
                }
                return pos;
            }
        }
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("");
    }
}
