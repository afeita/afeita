package com.github.afeita.db.reflect;

import com.github.afeita.db.annotation.Id;
import com.github.afeita.db.annotation.Property;
import com.github.afeita.db.annotation.Transient;
import com.github.afeita.tools.DateTimeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * bean 属性反射相关工具
 * @author chensf5
 *
 */
public class FieldUtils {

	/**
	 * 判断属性是否是基本类型的
	 * @param field
	 * @return
	 */
	public static boolean isBaseDateType(Field field){
		Class<?> clazz = field.getType();
		return   clazz.equals(String.class) ||  
		         clazz.equals(Integer.class)||  
		         clazz.equals(Byte.class) ||  
		         clazz.equals(Long.class) ||  
		         clazz.equals(Double.class) ||  
		         clazz.equals(Float.class) ||  
		         clazz.equals(Character.class) ||  
		         clazz.equals(Short.class) ||  
		         clazz.equals(Boolean.class) ||  
		         clazz.equals(Date.class) ||  
		         clazz.equals(java.util.Date.class) ||
		         clazz.equals(java.sql.Date.class) ||
		         clazz.isPrimitive();
	}
	
	
	/**
	 * 检测 字段是否已经被标注为 非数据库字段
	 * @param f
	 * @return
	 */
	public static boolean isTransient(Field f) {
		return f.getAnnotation(Transient.class) != null;
	}
	
	/**
	 * 获取某个实体执行某个方法的结果
	 * @param obj
	 * @param method
	 * @return
	 */
	private static Object invoke(Object obj , Method method){
		if(obj == null || method == null) return null;
		try {
			return method.invoke(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据属性，获取该属性的set方法
	 * @param clazz
	 * @param f
	 * @return
	 */
	public static Method getFieldSetMethod(Class<?> clazz, Field f) {
		String fn = f.getName();
		String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
		try {
			return clazz.getDeclaredMethod(mn, f.getType());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据属性字符串名字，获取该属性的set方法
	 * @param clazz
	 * @param f
	 * @return
	 */
	public static Method getFieldSetMethod(Class<?> clazz, String fieldName) {
		try {
			return getFieldSetMethod(clazz, clazz.getDeclaredField(fieldName));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据属性，获取该属性的get方法
	 * @param clazz
	 * @param f
	 * @return
	 */
	public static Method getFieldGetMethod(Class<?> clazz, Field f){
		String fn = f.getName();
		String mm = "get"+fn.substring(0, 1).toUpperCase() + fn.substring(1);
		try {
			return clazz.getDeclaredMethod(mm);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据属性字符串名字，获取该属性的get方法
	 * @param clazz
	 * @param f
	 * @return
	 */
	public static Method getFieldGetMethod(Class<?> clazz, String fieldName) {
		try {
			return getFieldGetMethod(clazz, clazz.getDeclaredField(fieldName));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据FieldName字符串及Class 获取某个字段Field
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByName(Class<?> clazz,String fieldName){
		Field field = null;
		if(fieldName!=null){
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return field;
	}
	
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object entity,Field field){
		Method method = getFieldGetMethod(entity.getClass(), field);
		return invoke(entity, method);
	}
	
	/**
	 * 获取某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object entity,String fieldName){
		Method method = getFieldGetMethod(entity.getClass(), fieldName);
		return invoke(entity, method);
	}
	
	/**
	 * 设置某个字段的值
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public static void setFieldValue(Object entity,Field field,Object value){
		try {
			Method set = getFieldSetMethod(entity.getClass(), field);
			if (set != null) {
				set.setAccessible(true);
				Class<?> type = field.getType();
				if (type == String.class) {
					set.invoke(entity, value.toString());
				} else if (type == int.class || type == Integer.class) {
					set.invoke(entity, value == null ? (Integer) null : Integer.parseInt(value.toString()));
				} else if (type == float.class || type == Float.class) {
					set.invoke(entity, value == null ? (Float) null: Float.parseFloat(value.toString()));
				} else if (type == long.class || type == Long.class) {
					set.invoke(entity, value == null ? (Long) null: Long.parseLong(value.toString()));
				} else if (type == Date.class) {
					set.invoke(entity, value == null ? (Date) null: DateTimeUtil.stringToDateTime(value.toString()));
				} else {
					set.invoke(entity, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据bean 的Field 获取 sqlite 中对应的 字段字符串名
	 * @param field
	 * @return
	 */
	public static String getColumnByField(Field field){
		Property property = field.getAnnotation(Property.class);
		if(null != property && 0!=property.column().trim().length()){
			return property.column();
		}
		
		Id idAnnotation = field.getAnnotation(Id.class);
		if(null != idAnnotation && 0!=idAnnotation.column().trim().length()){
			return idAnnotation.column();
		}
		
		return field.getName();
	}
	
	/**
	 * 根据 bean的 Field 获取Property注解上的默认的值
	 * @param field
	 * @return
	 */
	public static String getColumnDefaultValue(Field field){
		Property property = field.getAnnotation(Property.class);
		if(null != property && 0 != property.defaultValue().trim().length()){
			return property.defaultValue();
		}
		
		return null;
	}
}
