package com.github.afeita.db.sqlite;

import android.text.TextUtils;

import com.github.afeita.db.exception.DbException;
import com.github.afeita.db.table.Id;
import com.github.afeita.db.table.KeyValue;
import com.github.afeita.db.table.Property;
import com.github.afeita.db.table.TableInfo;
import com.github.afeita.tools.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 获取 crud及创建表等等操作的 sql 语句
 * @author chensf5
 *
 */
public class SqlBuilder {

	/**
	 * 获取 创建表的sql语句
	 * @param tableInfo bean对应映射对象
	 * @return
	 */
	public static String getCreateTableSql(TableInfo tableInfo){
		StringBuffer sb = new StringBuffer("create table if not exists ");
		sb.append(tableInfo.getTableName()).append("(");
		Id id = tableInfo.getId();
		if(int.class == id.getDataType()||Integer.class == id.getDataType()){
			sb.append(id.getColumn()).append(" integer primary key autoincrement,");
		}else{
			sb.append(id.getColumn()).append(" text primary key,");
		}
		Collection<Property> propertys = tableInfo.getPropertyMap().values();
		for (Property property : propertys) {
			sb.append(property.getColumn()).append(",");
		}
		
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 获取 插入数据库表的操作
	 * @param bean
	 * @param tableInfo
	 * @return
	 */
	public static String getInsertSql(Object bean, TableInfo tableInfo) {
		List<KeyValue> keyValues = new ArrayList<KeyValue>();
		Id id = tableInfo.getId();
		if(!(int.class == id.getDataType())&&!(Integer.class == id.getDataType())){
			if(String.class == id.getDataType()){
				KeyValue kv = new KeyValue();
				kv.setKey(id.getColumn());
				kv.setValue(id.getValue(bean));
				keyValues.add(kv);
			}
		}
		
		Collection<Property> propertys = tableInfo.getPropertyMap().values();
		for (Property property : propertys) {
			KeyValue kv = property2KeyValue(bean,property);
			if(null != kv)
				keyValues.add(kv);
		}
		StringBuffer sb =  new StringBuffer();
		if(null != keyValues && keyValues.size()>0){
			sb.append("insert into ");
			sb.append(tableInfo.getTableName()).append(" ( ");
			for(KeyValue kv : keyValues){
				sb.append(kv.getKey()).append(" ,");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(" ) values( ");
			for(KeyValue kv : keyValues){
				Object value = kv.getValue();
				if(value instanceof String){
					sb.append("'").append(value).append("',");
				}else if(value instanceof Date){
					sb.append("'").append(DateTimeUtil.dateTimeToString(value)).append("',");
				}else{
					sb.append(value).append(",");
				}
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}
		return sb.toString();
	}

	

	/**
	 * 获取 更新数据语句
	 * @param bean
	 * @param tableInfo
	 * @return
	 */
	public static String getUpdateSql(Object bean, TableInfo tableInfo) {
		StringBuffer sb = new StringBuffer();
		Id id = tableInfo.getId();
		if(null == id){
			throw new DbException(bean.getClass().getName()+" have not id property!");
		}
		Object idValue = id.getValue(bean);
		if(idValue == null){
			throw new DbException(bean.getClass().getName()+" id property have not value!");
		}
		
		Collection<Property> propertys = tableInfo.getPropertyMap().values();
		if(null != propertys && propertys.size()>0){
			sb.append("update ").append(tableInfo.getTableName()).append(" set ");
			for (Property property : propertys) {
				KeyValue kv = property2KeyValue(bean,property);
				if(null != kv){
					sb.append(getProperty2SqlStr(kv.getKey(),kv.getValue()));
				}
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(" where ").append(getProperty2SqlStr(id.getColumn(),idValue));
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	
	/**
	 * 获取 表的更新sql语句
	 * @param bean
	 * @param tableInfo
	 * @param whereStr sql语句的where语句
	 * @return
	 */
	public static String getUpdateSql(Object bean, TableInfo tableInfo,
			String whereStr) {
		StringBuffer sb = new StringBuffer();
		
		Collection<Property> propertys = tableInfo.getPropertyMap().values();
		if(null != propertys && propertys.size()>0){
			sb.append("update ").append(tableInfo.getTableName()).append(" set ");
			for (Property property : propertys) {
				KeyValue kv = property2KeyValue(bean,property);
				if(null != kv){
					sb.append(getProperty2SqlStr(kv.getKey(),kv.getValue()));
				}
			}
			sb.deleteCharAt(sb.length()-1);
			if(!TextUtils.isEmpty(whereStr))
				sb.append(" where ").append(whereStr);
		}
		return sb.toString();
	}
	
	/**
	 * 获取 删除表的sql语句
	 * @param bean
	 * @param tableInfo
	 * @return
	 */
	public static String getDeleteSql(Object bean, TableInfo tableInfo) {
		StringBuffer sb = new StringBuffer();
		Id id = tableInfo.getId();
		if(null == id){
			throw new DbException(bean.getClass().getName()+" have not id property!");
		}
		Object idValue = id.getValue(bean);
		if(idValue == null){
			throw new DbException(bean.getClass().getName()+" id property have not value!");
		}
		sb.append("delete from ").append(tableInfo.getTableName()).append(" where ")
		  .append(getProperty2SqlStr(id.getColumn(),idValue));
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * 获取 删除表的sql语句
	 * @param clazz 
	 * @param idValue  id值，必须非空
	 * @return
	 */
	public static String getDeleteSql(Class clazz, Object idValue) {
		StringBuffer sb = new StringBuffer();
		TableInfo tableInfo = TableInfo.get(clazz);
		Id id = tableInfo.getId();
		if(null == id){
			throw new DbException(clazz.getName()+" have not id property!");
		}
		if(idValue == null){
			throw new DbException(clazz.getName()+" id property have not value!");
		}
		sb.append("delete from ").append(tableInfo.getTableName()).append(" where ")
		  .append(getProperty2SqlStr(id.getColumn(),idValue));
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * 获取 删除表中的sql语句 
	 * @param whereStr sql语句后面的where语句
	 * @param tableInfo
	 * @return
	 */
	public static String getDeleteSqlByWhereStr(String whereStr,
			TableInfo tableInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ").append(tableInfo.getTableName());
		if(!TextUtils.isEmpty(whereStr)){
			sb.append(" where ").append(whereStr);
		}
		return sb.toString();
	}
	
	private static String getSelectSqlByTableName(String tableName){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(tableName);
		return sb.toString();
	}
	
	/**
	 * 查询 表中的数据，根据id的值来
	 * @param tableInfo
	 * @param idValue  id的值
	 * @return
	 */
	public static String getSelectSql(TableInfo tableInfo, Object idValue) {
		StringBuffer sb = new StringBuffer();
		Id id = tableInfo.getId();
		if(null == id){
			throw new DbException(tableInfo.getClassName()+" have not id property!");
		}
		if(idValue == null){
			throw new DbException(tableInfo.getClassName()+" id property have not value!");
		}
		sb.append(getSelectSqlByTableName(tableInfo.getTableName()));
		sb.append(" where ").append(getProperty2SqlStr(id.getColumn(),idValue));
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * 查找表中的所有对象的数据
	 * @param tableInfo
	 * @return
	 */
	public static String getSelectAllBeanSql(TableInfo tableInfo) {
		if(null == tableInfo)
			throw new DbException("tableInfo is null!");
		return getSelectSqlByTableName(tableInfo.getTableName());
	}
	
	/**
	 * 根据whereStr条件语句 ，查找表中的所有对象
	 * @param tableInfo
	 * @param whereStr 条件语句 
	 * @return
	 */
	public static String getSelectAllByByWhereStr(TableInfo tableInfo,
			String whereStr) {
		StringBuffer sb = new StringBuffer();
		sb.append(getSelectSqlByTableName(tableInfo.getTableName()));
		if(!TextUtils.isEmpty(whereStr)){
			sb.append(" where ").append(whereStr);
		}
		return sb.toString();
	}

	/**
	 * 查询表中的所有数据对象，并且依据指定的order进行排序
	 * @param tableInfo
	 * @param orderStr  排序
	 * @return
	 */
	public static String getSelectAllWithOrderStr(TableInfo tableInfo,
			String orderStr) {
		StringBuffer sb = new StringBuffer();
		sb.append(getSelectSqlByTableName(tableInfo.getTableName()));
		if(!TextUtils.isEmpty(orderStr)){
			sb.append(" order by ").append(orderStr);
		}
		return sb.toString();
	}
	
	/**
	 * 查询表中的所有数据对象，根据指定的where条件及相应的order进行排序
	 * @param tableInfo
	 * @param whereStr where语句条件 
	 * @param orderStr order排序
	 * @return
	 */
	public static String getSelectAllByByWhereStrWithOrderStr(
			TableInfo tableInfo, String whereStr, String orderStr) {
		StringBuffer sb = new StringBuffer();
		sb.append(getSelectSqlByTableName(tableInfo.getTableName()));
		if(!TextUtils.isEmpty(whereStr)){
			sb.append(" where ").append(whereStr);
		}
		if(!TextUtils.isEmpty(orderStr)){
			sb.append(" order by ").append(orderStr);
		}
		return sb.toString();
	}
	
	//有可能 bean中某个属性没有set 值 ，但有注解 默认值的，若已经set值了，则当然直接用set的值
	private static KeyValue property2KeyValue(Object bean,Property property) {
		String key = property.getColumn();
		Object value = property.getValue(bean);
		KeyValue keyValue = null;
		if(null != value){
			keyValue = new KeyValue();
			keyValue.setKey(key);
			keyValue.setValue(value);
		}else{
			String defValue = property.getDefaultValue();
			if(null != defValue && 0 != defValue.trim().length()){
				keyValue = new KeyValue();
				keyValue.setKey(key);
				keyValue.setValue(defValue);
			}
		}
		
		return keyValue;
	}

	private static String getProperty2SqlStr(String key, Object value){
		StringBuffer sb = new StringBuffer();
		if(value instanceof String || value instanceof Date){
			sb.append(key).append("='").append(value).append("',");
		}else{
			sb.append(key).append("=").append(value).append(",");
			//sb.append(key).append("='").append(value).append("',");
		}
		return sb.toString();
	}

	

}
