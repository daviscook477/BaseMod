package basemod.patches.com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostCampfireEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
        clz=AbstractCampfireOption.class,
        method="update"
)
public class PostCampfireHook
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            public void edit(FieldAccess f) throws CannotCompileException
            {
                if (f.getClassName().equals(CampfireUI.class.getName()) && f.getFieldName().equals("somethingSelected") && f.isWriter()) {
                    f.replace("{ $0.somethingSelected = " + PostCampfireHook.class.getName() + ".Do(); }");
                }
            }
        };
    }

    @SuppressWarnings("unused")
    public static boolean Do()
    {
        boolean ret = BaseMod6.EVENT_BUS.post(new PostCampfireEvent());
        if (!BaseMod.publishPostCampfire()) {
            ret = false;
        }
        return ret;
    }
}