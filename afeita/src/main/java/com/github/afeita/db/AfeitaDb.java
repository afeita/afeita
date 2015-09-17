package com.github.afeita.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.afeita.db.exception.DbException;
import com.github.afeita.db.sqlite.CursorUtils;
import com.github.afeita.db.sqlite.SqlBuilder;
import com.github.afeita.db.table.TableInfo;
import com.github.afeita.log.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作sqlite 数据库
 * @author chensf5
 *
 */
public class AfeitaDb {
	
	private static Map<String,AfeitaDb> daoMap = new HashMap<String, AfeitaDb>();

	private DbConfig dbConfig ;
	
	private SQLiteDatabase db;
	
	private AfeitaDb(){}
	
	private AfeitaDb(DbConfig dbConfig){
		if(null == dbConfig){
			throw new DbException("dbConfig is null");
		}
		if(null == dbConfig.getCtx()){
			throw new DbException("dbConfig Context is null");
		}
		
		this.dbConfig = dbConfig;
		
		this.db = new SqliteDbHelp(dbConfig.getCtx(),dbConfig.getDbName(),dbConfig.getVersion()).getWritableDatabase();
		
	}
	
	public static AfeitaDb create(DbConfig dbConfig){
		return getInstance(dbConfig);
	}
	
	public static AfeitaDb create(Context ctx){
		DbConfig dbConfig = new DbConfig();
		dbConfig.setCtx(ctx);
		return getInstance(dbConfig);
	}
	
	public static AfeitaDb create(Context ctx,String dbName){
		DbConfig dbConfig = new DbConfig();
		dbConfig.setCtx(ctx);
		dbConfig.setDbName(dbName);
		return getInstance(dbConfig);
	}
	
	public static AfeitaDb create(Context ctx,String dbName,boolean isDebugSql){
		DbConfig dbConfig = new DbConfig();
		dbConfig.setCtx(ctx);
		dbConfig.setDbName(dbName);
		dbConfig.setDebugSql(isDebugSql);
		return getInstance(dbConfig);
	}
	
	public static AfeitaDb create(Context ctx,boolean isDebugSql){
		DbConfig dbConfig = new DbConfig();
		dbConfig.setCtx(ctx);
		dbConfig.setDebugSql(isDebugSql);
		return getInstance(dbConfig);
	}
	
	private synchronized static AfeitaDb getInstance(DbConfig dbConfig) {
		AfeitaDb afeitaDb = daoMap.get(dbConfig.getDbName());
		if(null == afeitaDb){
			afeitaDb = new AfeitaDb(dbConfig);
			daoMap.put(dbConfig.getDbName(), afeitaDb);
		}
		return afeitaDb;
	}

	
	
	/**
	 * 更新 表中的数据  主键ID必须非空
	 * @param bean
	 */
	public void update(Object bean){
		TableInfo tableInfo = checkIfTableExist(bean.getClass());
		String sql = SqlBuilder.getUpdateSql(bean,tableInfo);
		debugSql(sql);
		db.execSQL(sql);
	}
	
	/**
	 * 更新 表中的数据 
	 * @param bean
	 * @param whereStr  sql语句后面的where 语句
	 */
	public void update(Object bean,String whereStr){
		TableInfo tableInfo = checkIfTableExist(bean.getClass());
		String sql = SqlBuilder.getUpdateSql(bean,tableInfo,whereStr);
		debugSql(sql);
		db.execSQL(sql);
	}
	
	/**
	 * 删除 表中的数据 主键ID必须非常
	 * @param bean
	 */
	public void delete(Object bean){
		TableInfo tableInfo = checkIfTableExist(bean.getClass());
		String sql = SqlBuilder.getDeleteSql(bean,tableInfo);
		debugSql(sql);
		db.execSQL(sql);
	}
	
	/**
	 * 删除 表中的数据，根据id的值，删除。
	 * @param clazz
	 * @param idValue id的值，必须非空
	 */
	public void deleteById(Class clazz,Object idValue){
		checkIfTableExist(clazz);
		String sql = SqlBuilder.getDeleteSql(clazz, idValue);
		debugSql(sql);
		db.execSQL(sql);
	}
	
	/**
	 * 删除 表中的数据，根据sql where的语句，来删除数据
	 * @param clazz
	 * @param whereStr  sql中的where语句
	 */
	public void deleteByWhereStr(Class clazz,String whereStr){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getDeleteSqlByWhereStr(whereStr,tableInfo);
		debugSql(sql);
		db.execSQL(sql);
	}
	
	/**
	 * 根据id的值，查询表中的数据
	 * @param clazz
	 * @param idValue id的值，非空
	 */
	public  <T> T findById(Class<T> clazz,Object idValue){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getSelectSql(tableInfo,idValue);
		debugSql(sql);
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(sql, null);
			T entity = null;
			if(null != cursor&&cursor.moveToNext()){
				entity = CursorUtils.getEntitiy(clazz, cursor);
			}
			return entity;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != cursor)
				cursor.close();
		}
		return null;
	}
	
	/**
	 * 查找实体clazz 在数据库中所有对象数据
	 * @param clazz
	 * @return
	 */
	public <T> List<T> findAll(Class<T> clazz){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getSelectAllBeanSql(tableInfo);
		return findAllBySql(clazz, sql);
	}
	
	/**
	 * 根据where条件语句 查找实体clazz 在数据库中的所有对象数据
	 * @param clazz
	 * @param whereStr where条件语句
	 * @return
	 */
	public <T> List<T> findAllByWhereStr(Class<T> clazz,String whereStr){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getSelectAllByByWhereStr(tableInfo,whereStr);
		return findAllBySql(clazz, sql);
	}
	
	/**
	 * 查找实体clazz 在数据库中的所有对象数据，使用指定的排序顺序
	 * @param clazz
	 * @param orderStr  排序语句
	 * @return
	 */
	public <T> List<T> findAllWithOrderStr(Class<T> clazz,String orderStr){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getSelectAllWithOrderStr(tableInfo,orderStr);
		return findAllBySql(clazz, sql);
	}
	
	public <T> List<T> findAllByWhereStrWithOrderStr(Class<T> clazz,String whereStr,String orderStr){
		TableInfo tableInfo = checkIfTableExist(clazz);
		String sql = SqlBuilder.getSelectAllByByWhereStrWithOrderStr(tableInfo, whereStr,orderStr);
		return findAllBySql(clazz, sql);
	}

	private <T> List<T> findAllBySql(Class<T> clazz, String sql) {
		debugSql(sql);
		Cursor cursor = null;
		List<T> entitys = new ArrayList<T>();
		try{
			cursor = db.rawQuery(sql, null);
			while(null != cursor&&cursor.moveToNext()){
				T entity = CursorUtils.getEntitiy(clazz, cursor);
				entitys.add(entity);
			}
			return entitys;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != cursor)
				cursor.close();
		}
		return null;
	}
	
	/**
	 * 保存入数据库
	 * @param bean
	 */
	public void save(Object bean){
		TableInfo tableInfo = checkIfTableExist(bean.getClass());
		String sql = SqlBuilder.getInsertSql(bean,tableInfo);
		debugSql(sql);
		db.execSQL(sql);
		
	}
	
	//检查表是否存在，不存在创建表结构
	private TableInfo checkIfTableExist(Class clazz) {
		TableInfo tableInfo = TableInfo.get(clazz);
		if(!isTableExist(tableInfo)){
			//创建表
			String sql = SqlBuilder.getCreateTableSql(tableInfo);
			debugSql(sql);
			db.execSQL(sql);
		}
		return tableInfo;
	}
	
	/**
	 * 删除表结构
	 * @param clazz
	 */
	public void dropTableIfTableExist(Class clazz) {
		TableInfo tableInfo = TableInfo.get(clazz);
		if(isTableExist(tableInfo)){
			String sql = "DROP TABLE IF EXISTS "+tableInfo.getTableName();
			debugSql(sql);
			db.execSQL(sql);
			tableInfo.setExistTable(false);
		}
	}


	private boolean isTableExist(TableInfo tableInfo) {
		boolean retValue = false;
		if(tableInfo.isExistTable())
			return true;
		Cursor rawQuery = null;
		try{
			String sql = "select count(*) as c from sqlite_master where type='table' and name='"+tableInfo.getTableName()+"'";
			debugSql(sql);
			rawQuery = db.rawQuery(sql, null);
			if(rawQuery.moveToNext()){
				if(rawQuery.getInt(0)>=1){
					retValue = true;
					tableInfo.setExistTable(true);  //保存表状态，已经存在，不再进行检查表是否存在了。
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			rawQuery.close();
		}
		
		
		return retValue;
	}


	private void debugSql(String sql) {
		if(dbConfig.isDebugSql()){ //dbConfig不可能为null
			L.d("AfeiDb Debug Sql", ">>" + sql);
		}
	}


	public static class DbConfig{
		private String dbName="afeita.db"; //数据库名称，默认为afei.db
		private int version = 1;  //数据库版本名，默认为1
		private boolean isDebugSql = true;  //是否log打印出 操作的sql语句
		private Context ctx;
		public String getDbName() {
			return dbName;
		}
		public void setDbName(String dbName) {
			this.dbName = dbName;
		}
		public int getVersion() {
			return version;
		}
		public void setVersion(int version) {
			this.version = version;
		}
		public boolean isDebugSql() {
			return isDebugSql;
		}
		public void setDebugSql(boolean isDebugSql) {
			this.isDebugSql = isDebugSql;
		}
		public Context getCtx() {
			return ctx;
		}
		public void setCtx(Context ctx) {
			this.ctx = ctx;
		} 
	}
	
	class SqliteDbHelp extends SQLiteOpenHelper{
		public SqliteDbHelp(Context context, String name,
				int version) {
			super(context, name, null, version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

}
