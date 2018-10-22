package basemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHacks {
    public static final Logger logger = LogManager.getLogger(ReflectionHacks.class.getName());

	private ReflectionHacks() {}

    //
    // Reflection hacks
    //
    
    // getPrivateStatic - read private static variables
	public static Object getPrivateStatic(Class<?> objClass, String fieldName) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            return targetField.get(null);
        } catch (Exception e) {
            logger.error("Exception occurred when getting private static field " + fieldName + " of " + objClass.getName(), e);
        }
        
        return null;
    }
    
    // setPrivateStatic - modify private static variables
	public static void setPrivateStatic(Class<?> objClass, String fieldName, Object newValue) {
		try {
			Field targetField = objClass.getDeclaredField(fieldName);
			targetField.setAccessible(true);
			targetField.set(null, newValue);
		} catch (Exception e) {
			logger.error("Exception occurred when setting private static field " + fieldName + " of " + objClass.getName(), e);
		}
    }
    
    // setPrivateStaticFinal - modify (private) static (final) variables
	public static void setPrivateStaticFinal(Class<?> objClass, String fieldName, Object newValue) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(targetField, targetField.getModifiers() & ~Modifier.FINAL);
            
            targetField.setAccessible(true);
            targetField.set(null, newValue);
        } catch (Exception e) {
            logger.error("Exception occurred when setting private static (final) field " + fieldName + " of " + objClass.getName(), e);
        }
    }

    // getPrivate - read private variables of an object
	public static Object getPrivate(Object obj, Class<?> objClass, String fieldName) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            return targetField.get(obj);
        } catch (Exception e) {
            logger.error("Exception occurred when getting private field " + fieldName + " of " + objClass.getName(), e);
        }

        return null;
    }

    // setPrivate - set private variables of an object
	public static void setPrivate(Object obj, Class<?> objClass, String fieldName, Object newValue) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            targetField.set(obj, newValue);
        } catch (Exception e) {
            logger.error("Exception occurred when setting private field " + fieldName + " of " + objClass.getName(), e);
        }
    }
    
    // setPrivateInherited - set private variable of superclass of an object
	public static void setPrivateInherited(Object obj, Class<?> objClass, String fieldName, Object newValue) {
    	try {
    		Field targetField = objClass.getSuperclass().getDeclaredField(fieldName);
    		targetField.setAccessible(true);
    		targetField.set(obj, newValue);
    	} catch (Exception e) {
    		logger.error("Exception occurred when setting private field " + fieldName + " of the superclass of " + objClass.getName(), e);
    	}
    }
	
}
