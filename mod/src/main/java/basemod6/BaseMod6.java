package basemod6;

import basemod6.eventbus.EventBus;
import basemod6.eventbus.EventBusSubscriber;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.util.Set;

@SpireInitializer
public class BaseMod6
{
	public static final Logger logger = LogManager.getLogger(BaseMod6.class.getName());

	public static final EventBus EVENT_BUS = new EventBus();

	public static void initialize()
	{
		for (AnnotationDB db : Patcher.annotationDBMap.values()) {
			Set<String> subscribers = db.getAnnotationIndex().get(EventBusSubscriber.class.getName());
			if (subscribers != null) {
				for (String sub : subscribers) {
					try {
						logger.debug("Auto-subscribing {}", sub);
						Class<?> clz = Class.forName(sub, false, BaseMod6.class.getClassLoader());
						EVENT_BUS.register(clz);
					} catch (ClassNotFoundException e) {
						logger.error("Failed to load mod class {} for @EventBusSubscriber annotation", sub);
					}
				}
			}
		}
	}
}
