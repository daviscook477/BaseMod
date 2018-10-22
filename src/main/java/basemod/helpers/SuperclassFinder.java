package basemod.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SuperclassFinder {
	public static Method getSuperClassMethod(Class<?> obj_class, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException{
		Method obj_method;
		try {
			obj_method = obj_class.getDeclaredMethod(methodName, parameterTypes);
			return obj_method;
		} catch (NoSuchMethodException | SecurityException e) {
			// prevent confusing stackoverflowexception and instead throw the correct exception
			if (obj_class.getClass().equals(Object.class)) {
				throw new NoSuchMethodException();
			}
			Method obj_super_method = getSuperClassMethod(obj_class.getSuperclass(), methodName, parameterTypes);
				return obj_super_method;
		}
	}
	
	public static Field getSuperclassField(Class<?> obj_class, String fieldName) throws NoSuchFieldException {
		Field obj_field;
		try {
			obj_field = obj_class.getDeclaredField(fieldName);
			return obj_field;
		} catch (NoSuchFieldException | SecurityException e) {
			// prevent confusing stackoverflowexception and instead throw the correct exception
			if (obj_class.getClass().equals(Object.class)) {
				throw new NoSuchFieldException();
			}
			Field obj_super_field = getSuperclassField(obj_class.getSuperclass(), fieldName);
			return obj_super_field;
		}
	}
}
