package com.github.afeita.db.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.afeita.db.annotation.Id;
import com.github.afeita.db.annotation.Table;
import com.github.afeita.db.table.Property;


/**
 * bean class 相关工具，比如获取bean对应的表名，对应sqlite的所有属性Property，主键等等信息 
 * @author chensf5
 *
 */
public class ClassUtils {

	/**
	 * 获取 指定 bean类对应的 数据库的表名
	 * @param clazz
	 * @return
	 */
	public static String getTable(Class<?> clazz){
		Table table = clazz.getAnnotation(Table.class);
		if(null == table || 0 == table.name().trim().length()){
			return clazz.getName().replace(".", "_");
		}
		return table.name();
	}
	
	
	/**
	 * 获取 指定bean类对应的 数据库中的sqlite主键 字符串名称 ，
	 * 主键名是 1、bean类中Id类注解name  2、有id或_id
	 * 若以上两者都没有的，直接返回null 
	 * @param clazz
	 * @return
	 */
	public static String getPrimaryKeyColumn(Class<?> clazz){
		String primaryKey = null;
		Field[] fields = clazz.getDeclaredFields();
		if(null != fields){
			Id idAnnotation = null;
			Field idField = null;
			for (Field field : fields) {
				idAnnotation = field.getAnnotation(Id.class);
				if(null != idAnnotation){
					idField = field;
					break;
				}
			}
			
			if(null != idAnnotation){
				primaryKey = idAnnotation.column();
				if(null == primaryKey || 0 == primaryKey.trim().length()){
					primaryKey = idField.getName();
				}
			}else{
				for (Field field : fields) {
					if("_id".equals(field.getName().toLowerCase())){
						primaryKey = "_id";
						return primaryKey;
					}
				}
				
				for (Field field : fields) {
					if("id".equals(field.getName().toLowerCase())){
						primaryKey = "id";
						break;
					}
				}
			}
			
		}else{
			throw new RuntimeException("this bean["+clazz+"] has no field");
		}
		return primaryKey;
	}
	
	/**
	 * 获取 指定bean类对应的 数据库中的sqlite主键 的 bean中 属性 field
	 * 主键名是 1、bean类中Id类注解name  2、有id或_id
	 * 若以上两者都没有的，直接返回null 
	 * @param clazz
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz){
		Field primaryField = null;
		Field[] fields = clazz.getDeclaredFields();
		if(null != fields){
			for (Field field : fields) {
				Id idAnnotation = field.getAnnotation(Id.class);
				if(null != idAnnotation){
					primaryField = field;
					return field;
				}
			}
			for (Field field : fields) {
				if("_id".equals(field.getName().toLowerCase())){
					primaryField = field;
					return field;
				}
			}
			for (Field field : fields) {
				if("id".equals(field.getName().toLowerCase())){
					primaryField = field;
					return field;
				}
			}
		}else{
			throw new RuntimeException("this bean["+clazz+"] has no field");
		}
		return primaryField;
	}
	
	/**
	 * 获取sqlite主键对应的 bean 属性字符串名称 
	 * @param clazz
	 * @return
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz){
		Field f = getPrimaryKeyField(clazz);
		return null==f ? null : f.getName();
	}
	
	/**
	 * 获取 bean 属性 对应的 Property
	 * @param clazz
	 * @return
	 */
	public static List<Property> getProPertyList(Class<?> clazz){
		List<Property>  propertys = new ArrayList<Property>();
		Field[] fields = clazz.getDeclaredFields();
		if(null != fields){
			String primaryKey = getPrimaryKeyFieldName(clazz);
			for (Field field : fields) {
				//必须是 非 Transitent型 并且是 基本数据数据加Data类型进行sqlite 
				if(!FieldUtils.isTransient(field)&&FieldUtils.isBaseDateType(field)){
					//过滤掉主键
					if(field.getName().equals(primaryKey)){
						continue;
					}
					
					Property p = new Property();
					p.setColumn(FieldUtils.getColumnByField(field));
					p.setFieldName(field.getName());
					p.setDefaultValue(FieldUtils.getColumnDefaultValue(field));
					p.setDataType(field.getType());
					p.setSet(FieldUtils.getFieldSetMethod(clazz, field));
					p.setGet(FieldUtils.getFieldGetMethod(clazz, field));
					
					propertys.add(p);
				}
			}
			
			return propertys;
		}else{
			throw new RuntimeException("this bean["+clazz+"] has no field");
		}
	}
}
