package basemod6.eventbus;

import basemod6.events.Event;
import com.evacipated.cardcrawl.modthespire.Loader;
import javassist.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class ClassEventHandler implements IEventListener
{
	private static final Map<Method, Class<?>> cache = new HashMap<>();

	private final IEventListener handler;

	ClassEventHandler(Class<?> target, Method method) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException
	{
		handler = (IEventListener)createWrapper(method).newInstance();
	}

	private Class<?> createWrapper(Method callback) throws NotFoundException, CannotCompileException
	{
		if (cache.containsKey(callback)) {
			return cache.get(callback);
		}

		ClassPool pool = Loader.getClassPool();
		CtClass ctClass = pool.makeClass(getUniqueName(callback));

		CtClass eventListenerInterface = pool.get(IEventListener.class.getName());
		ctClass.addInterface(eventListenerInterface);

		CtMethod method = CtNewMethod.make(
				CtClass.voidType, // Return
				"invoke", // Method name
				pool.get(new String[]{Event.class.getName()}), // Params
				null, // Exceptions
				String.format("%s.%s((%s)$1);",
						callback.getDeclaringClass().getName(),
						callback.getName(),
						callback.getParameterTypes()[0].getName()
				), // Body
				ctClass
		);
		ctClass.addMethod(method);

		Class<?> ret = ctClass.toClass();
		cache.put(callback, ret);
		return ret;
	}

	private String getUniqueName(Method callback)
	{
		return String.format(
				"%s_%d_%s_%s_%s",
				getClass().getName(),
				0,
				callback.getDeclaringClass().getSimpleName(),
				callback.getName(),
				callback.getParameterTypes()[0].getSimpleName()
		);
	}

	@Override
	public void invoke(Event event)
	{
		handler.invoke(event);
	}
}
