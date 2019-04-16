package basemod6.eventbus;

import basemod6.events.Event;
import basemod6.events.ResultEvent;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus
{
	private ConcurrentHashMap<Class<? extends Event>, Collection<IEventListener>> eventListeners = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<?>, Collection<IEventListener>> clzListeners = new ConcurrentHashMap<>();

	public void register(Class<?> clz)
	{
		if (clzListeners.containsKey(clz)) {
			return;
		}

		Arrays.stream(clz.getMethods())
				.filter(m -> Modifier.isStatic(m.getModifiers()))
				.filter(m -> m.isAnnotationPresent(SubscribeEvent.class))
				.forEach(m -> registerListener(clz, m));
	}

	@SuppressWarnings("unchecked")
	private void registerListener(Class<?> target, Method method)
	{
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			throw new IllegalArgumentException(
					// TODO
			);
		}

		Class<?> eventType = parameterTypes[0];
		if (!Event.class.isAssignableFrom(eventType)) {
			throw new IllegalArgumentException(
					// TODO
			);
		}

		register((Class<? extends Event>) eventType, target, method);
	}

	private void register(Class<? extends Event> eventType, Class<?> target, Method method)
	{
		try {
			ClassEventHandler handler = new ClassEventHandler(target, method);

			addToListeners(target, eventType, handler);
		} catch (IllegalAccessException | InstantiationException | CannotCompileException | NotFoundException e) {
			e.printStackTrace();
		}
	}

	private void addToListeners(Class<?> target, Class<? extends Event> eventType, IEventListener listener)
	{
		Collection<IEventListener> others = eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>());
		others.add(listener);

		Collection<IEventListener> clzOthers = clzListeners.computeIfAbsent(target, k -> new ArrayList<>());
		clzOthers.add(listener);
	}

	public void unregister(Class<?> clz)
	{
		Collection<IEventListener> list = clzListeners.remove(clz);
		if (list == null) {
			return;
		}
		for (IEventListener listener : list) {
			for (Collection<IEventListener> other : eventListeners.values()) {
				other.remove(listener);
			}
		}
	}

	public void post(Event event)
	{
		Class<?> clz = event.getClass();
		do {
			Collection<IEventListener> others = eventListeners.get(clz);
			if (others != null) {
				for (IEventListener listener : others) {
					listener.invoke(event);
				}
			}

			if (clz.equals(Event.class)) {
				break;
			}
			clz = clz.getSuperclass();
		} while (clz != null);

		event.finish();
	}

	public <T> T post(ResultEvent<T> event)
	{
		post((Event) event);

		return event.result();
	}
}
