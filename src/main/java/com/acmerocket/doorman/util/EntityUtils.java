package com.acmerocket.doorman.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.mongodb.morphia.annotations.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.model.Identifiable;

public final class EntityUtils {
    private static final Logger LOG = LoggerFactory.getLogger(EntityUtils.class);

	private EntityUtils() {}
	
	public static String getId(Object obj) {
		if (obj instanceof Identifiable) {
			return ((Identifiable)obj).getId();
		}
		else {
			return getAnnotatedId(obj);
		}
	}
	
	public static void setId(Identifiable entity, String id) {
	    Field idField = getIdField(entity);
	    idField.setAccessible(true);
	    try {
            idField.set(entity, id);
        }
        catch (Exception e) {
            LOG.error("Unable to set id={} on {}", id, entity, e);
        }
	}
	
	public static String getAnnotatedId(Object obj) {
	    Object value = getValueAnnotatedWith(Id.class, obj);
		return value != null ? value.toString() : null;
	}
	
	public static Object getValueAnnotatedWith(Class<? extends Annotation> annotation, Object obj) {
		Object value = null;
		
		Field field = getFieldAnnotatedWith(annotation, obj.getClass());
		if (field != null) {
			try {
				field.setAccessible(true);
				value = field.get(obj);
			} 
			catch (Exception e) {
				LOG.debug("Unable to get value for {} annotated with ", field, annotation, e);
			}
		}
		
		return value;
	}
	
	public static Field getIdField(Object obj) {
		return getFieldAnnotatedWith(Id.class, obj.getClass());
	}
	
	public static Field getFieldAnnotatedWith(Class<? extends Annotation> annotation, Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		
    	for (Field field : clazz.getDeclaredFields()) {
    		Annotation found = field.getAnnotation(annotation);
    		    		
    		if (found != null) {
    			return field;
    		}
    	}
    	
        return getFieldAnnotatedWith(annotation, clazz.getSuperclass());
	}
}
