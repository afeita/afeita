package com.github.afeita.db.table;

/**
 * sqlite column  对应的 列名及值
 * @author chensf5
 *
 */
public class KeyValue {

	private String key;
	private Object value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
