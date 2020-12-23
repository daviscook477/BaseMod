package basemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHacks
{
	public static final Logger logger = LogManager.getLogger(ReflectionHacks.class.getName());

	private static final Map<Pair<Class<?>, String>, Field> fieldMap = new HashMap<>();
	private static final Map<String, Method> methodMap = new HashMap<>();

	public static class RMethod
	{
		private final Method method;

		private RMethod(Class<?> clz, String methodName, Class<?>... parameterTypes)
		{
			method = getCachedMethod(clz, methodName, parameterTypes);
		}

		public <R> R invoke(Object instance, Object... args)
		{
			try {
				//noinspection unchecked
				return (R) method.invoke(instance, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class RStaticMethod extends RMethod
	{
		private RStaticMethod(Class<?> clz, String methodName, Class<?>... parameterTypes)
		{
			super(clz, methodName, parameterTypes);
		}

		public <R> R invoke(Object... args)
		{
			return super.invoke(null, args);
		}
	}

	private ReflectionHacks()
	{
	}

	private static String toDescriptor(Class<?> clz, String methodName, Class<?>... parameterTypes)
	{
		StringBuilder buf = new StringBuilder();
		buf.append(clz.getName().replace('.', '/'))
				.append('.')
				.append(methodName)
				.append(":(");
		for (Class<?> paramType : parameterTypes) {
			toDescriptor(buf, paramType);
		}
		buf.append(')');

		return buf.toString();
	}

	private static void toDescriptor(StringBuilder buf, Class<?> clz)
	{
		if (clz.isPrimitive()) {
			if (clz == byte.class) {
				buf.append('B');
			} else if (clz == char.class) {
				buf.append('C');
			} else if (clz == float.class) {
				buf.append('F');
			} else if (clz == double.class) {
				buf.append('D');
			} else if (clz == int.class) {
				buf.append('I');
			} else if (clz == long.class) {
				buf.append('J');
			} else if (clz == short.class) {
				buf.append('S');
			} else if (clz == boolean.class) {
				buf.append('Z');
			} else if (clz == void.class) {
				buf.append('V');
			} else {
				throw new RuntimeException("Unrecognized primitive " + clz);
			}
		} else if (clz.isArray()) {
			buf.append('[');
			toDescriptor(buf, clz.getComponentType());
		} else {
			buf.append('L').append(clz.getName().replace('.', '/')).append(';');
		}
	}

	/**
	 * Returns the {@code Field} object for the field from the class described by {@code clz} with the name {@code fieldName}.
	 * <p> {@code Field}s returned by this method have already had their {@code accessible} flag set to true.
	 * <p> After the first request for a specific {@code Field}, the {@code Field} is cached so future requests are faster.
	 *
	 * @param clz the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @return the {@code Field} object for the specified field in {@code clz}
	 */
	public static Field getCachedField(Class<?> clz, String fieldName)
	{
		Pair<Class<?>, String> pair = new Pair<>(clz, fieldName);
		Field ret = fieldMap.get(pair);
		if (ret == null) {
			try {
				ret = clz.getDeclaredField(fieldName);
				ret.setAccessible(true);
				fieldMap.put(pair, ret);
			} catch (NoSuchFieldException e) {
				logger.error("Exception occurred when getting field " + fieldName + " of " + clz.getName(), e);
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Returns the {@code Method} object for the method from the class described by {@code clz} with the name {@code methodName}
	 * and parameter types {@code parameterTypes}.
	 * <p> {@code Method}s returned by this method have already had their {@code accissible} flag set to true.
	 * <p> After the first request for a specific {@code Method}, the {@code Method} is cached so future requests are faster.
	 *
	 * @param clz the {@code Class} the method is declared in
	 * @param methodName the name of the method
	 * @param parameterTypes the parameter type array
	 * @return the {@code Method} object for the specified method
	 */
	public static Method getCachedMethod(Class<?> clz, String methodName, Class<?>... parameterTypes)
	{
		String descriptor = toDescriptor(clz, methodName, parameterTypes);
		Method ret = methodMap.get(descriptor);
		if (ret == null) {
			try {
				ret = clz.getDeclaredMethod(methodName, parameterTypes);
				ret.setAccessible(true);
				methodMap.put(descriptor, ret);
			} catch (NoSuchMethodException e) {
				logger.error("Exception occurred when getting method " + methodName + " of " + clz.getName(), e);
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Returns the value of a {@code static} field.
	 *
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param <T> the type of the field
	 * @return the value of the field
	 */
	public static <T> T getPrivateStatic(Class<?> objClass, String fieldName)
	{
		try {
			//noinspection unchecked
			return (T) getCachedField(objClass, fieldName).get(null);
		} catch (Exception e) {
			logger.error("Exception occurred when getting private static field " + fieldName + " of " + objClass.getName(), e);
		}
		return null;
	}

	/**
	 * Sets the value of a {@code static} field.
	 *
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param newValue the value to set the field to
	 */
	public static void setPrivateStatic(Class<?> objClass, String fieldName, Object newValue)
	{
		try {
			getCachedField(objClass, fieldName).set(null, newValue);
		} catch (Exception e) {
			logger.error("Exception occurred when setting private static field " + fieldName + " of " + objClass.getName(), e);
		}
	}

	/**
	 * Sets the value of a {@code final static} field.
	 *
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param newValue the value to set the field to
	 */
	public static void setPrivateStaticFinal(Class<?> objClass, String fieldName, Object newValue)
	{
		try {
			Field f = getCachedField(objClass, fieldName);

			Field modifiers = getCachedField(Field.class, "modifiers");
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);

			f.set(null, newValue);

			modifiers.setInt(f, f.getModifiers() & Modifier.FINAL);
		} catch (Exception e) {
			logger.error("Exception occurred when setting private static (final) field " + fieldName + " of " + objClass.getName(), e);
		}
	}

	/**
	 * Returns the value of a field from the given object.
	 *
	 * @param obj the object to get the field value from
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param <T> the type of the field
	 * @return the value of the field
	 */
	public static <T> T getPrivate(Object obj, Class<?> objClass, String fieldName)
	{
		try {
			//noinspection unchecked
			return (T) getCachedField(objClass, fieldName).get(obj);
		} catch (Exception e) {
			logger.error("Exception occurred when getting private field " + fieldName + " of " + objClass.getName(), e);
		}

		return null;
	}

	/**
	 * Sets the value of a field in the given object.
	 *
	 * @param obj the object to set the field value of
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param newValue the value to set the field to
	 */
	public static void setPrivate(Object obj, Class<?> objClass, String fieldName, Object newValue)
	{
		try {
			getCachedField(objClass, fieldName).set(obj, newValue);
		} catch (Exception e) {
			logger.error("Exception occurred when setting private field " + fieldName + " of " + objClass.getName(), e);
		}
	}

	/**
	 * Sets the value of a {@code static} field.
	 *
	 * @param obj the object to set the field value of
	 * @param objClass the {@code Class} the field is declared in
	 * @param fieldName the name of the field
	 * @param newValue the value to set the field to
	 */
	public static void setPrivateFinal(Object obj, Class<?> objClass, String fieldName, Object newValue)
	{
		try {
			Field f = getCachedField(objClass, fieldName);

			Field modifiers = getCachedField(Field.class, "modifiers");
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);

			f.set(obj, newValue);

			modifiers.setInt(f, f.getModifiers() & Modifier.FINAL);
		} catch (Exception e) {
			logger.error("Exception occurred when setting private (final) field " + fieldName + " of " + objClass.getName(), e);
		}
	}

	/**
	 * Returns the value of a field from a superclass of {@code obj}.
	 * <p> Searches up the chain of superclasses until it finds a field with the correct name.
	 * If no such field is found, returns {@code null}.
	 * <p> If the found field type cannot be cast to {@code <T>} a {@code ClassCastException} will be thrown.
	 *
	 * @param obj the object to get the field from
	 * @param objClass the {@code Class} of {@code obj}
	 * @param fieldName the name of the field
	 * @param <T> the type of the field
	 * @return the value of the field
	 */
	public static <T> T getPrivateInherited(Object obj, Class<?> objClass, String fieldName)
	{
		objClass = objClass.getSuperclass();
		while (objClass != null && objClass != Object.class) {
			try {
				Field f = objClass.getDeclaredField(fieldName);
				f.setAccessible(true);
				try {
					//noinspection unchecked
					return (T) f.get(obj);
				} catch (IllegalAccessException e) {
					logger.error("Exception occurred when getting private field " + fieldName + " of the superclass of " + objClass.getName(), e);
					return null;
				}
			} catch (NoSuchFieldException ignored) {}
			objClass = objClass.getSuperclass();
		}

		return null;
	}

	/**
	 * Sets the value of a field of a superclass of {@code obj}.
	 * <p> Searches up the chain of superclasses until it finds a field with the correct name.
	 *
	 * @param obj the object to set the field of
	 * @param objClass the {@code Class} of {@code obj}
	 * @param fieldName the name of the field
	 * @param newValue the value to set the field to
	 */
	public static void setPrivateInherited(Object obj, Class<?> objClass, String fieldName, Object newValue)
	{
		objClass = objClass.getSuperclass();
		while (objClass != null && objClass != Object.class) {
			try {
				Field f = objClass.getDeclaredField(fieldName);
				f.setAccessible(true);
				try {
					f.set(obj, newValue);
				} catch (IllegalAccessException e) {
					logger.error("Exception occurred when setting private field " + fieldName + " of the superclass of " + objClass.getName(), e);
				}
				return;
			} catch (NoSuchFieldException ignored) {}
			objClass = objClass.getSuperclass();
		}
	}

	/**
	 * Returns a {@code RMethod} wrapping the {@code Method} object representing the method specified.
	 *
	 * @param objClass the {@code Class} the method is declared in
	 * @param methodName the name of the method
	 * @param parameterTypes the parameter types
	 * @return a {@code RMethod} object representing the method
	 */
	public static RMethod privateMethod(Class<?> objClass, String methodName, Class<?>... parameterTypes)
	{
		return new RMethod(objClass, methodName, parameterTypes);
	}

	/**
	 * Returns a {@code RStaticMethod} wrapping the {@code Method} object representing the static method specified.
	 *
	 * @param objClass the {@code Class} the method is declared in
	 * @param methodName the name of the method
	 * @param parameterTypes the parameter types
	 * @return a {@code RStaticMethod} object representing the static method
	 */
	public static RStaticMethod privateStaticMethod(Class<?> objClass, String methodName, Class<?>... parameterTypes)
	{
		return new RStaticMethod(objClass, methodName, parameterTypes);
	}
}
