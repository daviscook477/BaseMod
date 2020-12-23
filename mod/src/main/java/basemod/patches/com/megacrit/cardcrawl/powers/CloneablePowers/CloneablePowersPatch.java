package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.*;

@SuppressWarnings("unused")
public abstract class CloneablePowersPatch {
    public static void addMakeCopyMethod(CtBehavior ctMethodToPatch, String signature) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "", signature);
    }
    public static void addMakeCopyMethod(CtBehavior ctMethodToPatch, String prefix, String signature) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, prefix, "owner", signature);
    }
    public static void addMakeCopyMethod(CtBehavior ctMethodToPatch, String prefix, String middle, String signature) throws NotFoundException, CannotCompileException {
        CtClass ctClass = ctMethodToPatch.getDeclaringClass();
        ClassPool pool = ctClass.getClassPool();

        CtClass ctCloneablePowerInterface = pool.get(CloneablePowerInterface.class.getName());
        ctClass.addInterface(ctCloneablePowerInterface);
        CtClass ctAbstractPower = pool.get(AbstractPower.class.getName());

        CtMethod method = CtNewMethod.make(
                ctAbstractPower, // Return
                "makeCopy", // Method name
                new CtClass[]{},
                null, // Exceptions
                "return new " + ctClass.getName() + "(" + prefix + middle + (signature.isEmpty() ? "" : ", " + signature) + ");",
                ctClass
        );
        ctClass.addMethod(method);
    }
    /* based on:
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            ClassPool pool = ctClass.getClassPool();

            CtClass ctCloneablePowerInterface = pool.get(CloneablePowerInterface.class.getName());
            ctClass.addInterface(ctCloneablePowerInterface);
            CtClass ctAbstractPower = pool.get(AbstractPower.class.getName());

            CtMethod method = CtNewMethod.make(
                    ctAbstractPower, // Return
                    "makeCopy", // Method name
                    new CtClass[]{},
                    null, // Exceptions
                    "return new " + WraithFormPower.class.getName() + "(owner, amount);",
                    ctClass
            );
            ctClass.addMethod(method);
     */
}
