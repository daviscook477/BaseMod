package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class InitializeEventListPatch {
	@SpirePatch(cls = "com.megacrit.cardcrawl.helpers.EventHelper", method = "getEvent")
	public static class GetEvent{
		public static AbstractEvent Postfix(AbstractEvent __result, String key) {

			Class<? extends AbstractEvent> event = null;
			
			BaseMod.logger.info("Looking for custom events...");

			try {
				event = BaseMod.getEvent(key);
				if (event != null) {
					return event.newInstance();
				}
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return __result;
		}
	}

	@SpirePatch(
			cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon",
			method=SpirePatch.CONSTRUCTOR,
			paramtypes={
					"java.lang.String",
					"java.lang.String",
					"com.megacrit.cardcrawl.characters.AbstractPlayer",
					"java.util.ArrayList"
			}
	)
	public static class EventList {
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getMethodName().equals("initializeEventList")) {
						m.replace("{" +
								"$_ = $proceed($$);" +
								"basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.InitializeEventListPatch.EventList.Nested.Do();" +
								"}");
					}
				}
			};
		}

		public static class Nested {
			public static void Do() {
				BaseMod.logger.info("Adding Custom Events...");
				for (String eventID : BaseMod.getEventList(AbstractDungeon.id).keySet()) {
					BaseMod.logger.info("Added " + eventID + " to the " + AbstractDungeon.id + " eventList.");
					AbstractDungeon.eventList.add(eventID);
				}
			}
		}
	}
	
	@SpirePatch(cls = "com.megacrit.cardcrawl.dungeons.AbstractDungeon", method = "initializeSpecialOneTimeEventList")
	public static class SpecialEventList{
		public static void Postfix(AbstractDungeon dungeon) {
			BaseMod.logger.info("Adding Custom SpecialOneTimeEvents...");
			for(String eventID : BaseMod.getEventList((String)null).keySet()) {
				BaseMod.logger.info("Added " + eventID + " to the specialOneTimeEventList.");
				AbstractDungeon.specialOneTimeEventList.add(eventID);
			}
		}
	}
}
