package com.answers.woody.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.answers.woody.core.selector.Selector;

public class ExtractedField {

	private Selector selector;

	private final Field field;

	private Method setterMethod;

	private boolean isMulti;

	private boolean isNotNull;

	private String defaultValue;

	private Class<?> javeBean;

	private Map<String, Object> dataMap;

	public ExtractedField(Selector selector, Field field, Method setterMethod) {
		super();
		this.selector = selector;
		this.field = field;
		this.setterMethod = setterMethod;
		this.isMulti = false;
		this.isNotNull = false;
		this.defaultValue = null;
		this.javeBean = null;
		this.dataMap = null;
	}

	public Selector getSelector() {
		return selector;
	}

	public Field getField() {
		return field;
	}

	public Method getSetterMethod() {
		return setterMethod;
	}

	public boolean isMulti() {
		return isMulti;
	}

	public ExtractedField setMulti(boolean isMulti) {
		this.isMulti = isMulti;
		return this;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public ExtractedField setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public ExtractedField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public Class<?> javeBean() {
		return javeBean;
	}

	public ExtractedField setJaveBean(Class<?> javeBean) {
		this.javeBean = javeBean;
		return this;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public ExtractedField setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	@Override
	public String toString() {
		return "ExtractedField [selector=" + selector + ", field=" + field + ", setterMethod=" + setterMethod
				+ ", isMulti=" + isMulti + ", isNotNull=" + isNotNull + ", defaultValue=" + defaultValue
				+ ", javeBean=" + javeBean + ", dataMap=" + dataMap + "]";
	}

	

}
