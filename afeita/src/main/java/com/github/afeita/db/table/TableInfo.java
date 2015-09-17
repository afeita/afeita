package com.github.afeita.db.table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.afeita.db.exception.DbException;
import com.github.afeita.db.reflect.ClassUtils;
import com.github.afeita.db.reflect.FieldUtils;

/**
 * TableInfo 后续sqlite游标Cursor查出column后，可以直接拿到Property后，进行设值进去。
 * 并保存 bean类对应的 类名及表名，后续进行的crud操作，从此拿表名
 * @author chensf5
 *
 */
public class TableInfo {
	
	private String tableName;
	
	private String className;
	
	private Id id;
	
	//注意key String是column
	private Map<String,Property> propertyMap;
	
	private boolean isExistTable; //是否存在表 sqlite中是否已经存在表结构了
	
	//第一次class的name,new 出 TableInfo后，保存起来，下次直接get即可，不再进行费时的反射操作
	private static Map<String,TableInfo> tableInfos = new HashMap<String, TableInfo>();
	
	private TableInfo(){}
	
	public static TableInfo get(Class clazz){
		if(null == clazz){
			throw new DbException("get tableinfo error,because clazz is null!");
		}
		
		TableInfo tableInfo = tableInfos.get(clazz.getName());
		if(null == tableInfo){
			tableInfo = new TableInfo();
			tableInfo.setTableName(ClassUtils.getTable(clazz));
			tableInfo.setClassName(clazz.getName());
			
			Field pkf = ClassUtils.getPrimaryKeyField(clazz);
			if(null != pkf){
				Id id = new Id();
				id.setColumn(FieldUtils.getColumnByField(pkf));
				id.setFieldName(pkf.getName());
				id.setDataType(pkf.getType());
				//ID 没有默认值 的
				id.setGet(FieldUtils.getFieldGetMethod(clazz, pkf));
				id.setSet(FieldUtils.getFieldSetMethod(clazz, pkf));
				tableInfo.setId(id);
			}
			
			Map<String,Property> propertyMap = new HashMap<String,Property>();
			List<Property> propertyList = ClassUtils.getProPertyList(clazz);
			if(null != propertyList){
				for (Property property : propertyList) {
					if(null != property)
						propertyMap.put(property.getColumn(), property);
				}
			}
			tableInfo.setPropertyMap(propertyMap);
			tableInfos.put(clazz.getName(), tableInfo);
		}
		
		return tableInfo;
		
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public Map<String, Property> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, Property> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public static Map<String, TableInfo> getTableInfos() {
		return tableInfos;
	}

	public static void setTableInfos(Map<String, TableInfo> tableInfos) {
		TableInfo.tableInfos = tableInfos;
	}

	public boolean isExistTable() {
		return isExistTable;
	}

	public void setExistTable(boolean isExistTable) {
		this.isExistTable = isExistTable;
	}

}
