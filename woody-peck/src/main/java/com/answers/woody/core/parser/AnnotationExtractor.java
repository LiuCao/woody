package com.answers.woody.core.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.answers.woody.core.model.FuntionObj;
import com.answers.woody.core.model.SettingConstant;
import com.answers.woody.core.model.SupportedClassType;
import com.answers.woody.core.model.annotation.ComboExtract;
import com.answers.woody.core.model.annotation.ComboExtracts;
import com.answers.woody.core.model.annotation.ExprType;
import com.answers.woody.core.model.annotation.ExtractBy;
import com.answers.woody.core.model.annotation.ExtractedBean;
import com.answers.woody.core.model.annotation.KV;
import com.answers.woody.core.model.annotation.OP;
import com.answers.woody.core.model.annotation.Setting;
import com.answers.woody.core.model.annotation.Setting.Function;
import com.answers.woody.core.selector.Selector;

public class AnnotationExtractor extends BasicExtractor {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationExtractor.class);

	private static enum ExtractedAnnotation {
		ExtractedBean, ExtractBy, ComboExtract, ComboExtracts, UNKNOWN;
	}

	@Override
	protected Selector getSelector(Field field) {
		return getSelectorFromAnnotation(field);
	}

	@Override
	protected boolean multi(Field field) {
		boolean multi = false;
		Object result = invokeAnnotationMethod(field, "multi");
		if (result != null) {
			multi = (Boolean) result;
		}
		return multi;
	}

	@Override
	protected boolean notNull(Field field) {
		boolean notNull = false;
		Object result = invokeAnnotationMethod(field, "notNull");
		if (result != null) {
			notNull = (Boolean) result;
		}
		return notNull;
	}

	@Override
	protected String defaultValue(Field field) {
		String defaultValue = null;
		Object result = invokeAnnotationMethod(field, "defaultValue");
		if (result != null) {
			defaultValue = (String) result;
		}
		return defaultValue;
	}

	@Override
	protected Class<?> javaBean(Field field) {
		Class<?> javaBean = null;
		Object result = invokeAnnotationMethod(field, "javaBean");
		if (result != null) {
			javaBean = (Class<?>) result;
		}
		return javaBean;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> dataMap(Field field) {
		Map<String, Object> dataMap = null;
		Object result = invokeAnnotationMethod(field, "dataMap");
		if (result != null) {
			dataMap = (HashMap<String, Object>) result;
		}
		return dataMap;
	}

	private Object invokeAnnotationMethod(Field field, String methodName) {
		Object value = null;
		Annotation annotation = null;
		Annotation[] annotations = field.getAnnotations();
		for (Annotation _annotation : annotations) {
			ExtractedAnnotation extractedAnnotation = getExtractedAnnotation(_annotation);
			switch (extractedAnnotation) {
			case ExtractedBean:
			case ExtractBy:
			case ComboExtract:
			case ComboExtracts:
				annotation = _annotation;
				break;
			default:
				break;
			}
			if (annotation != null) {
				break;
			}
		}
		if (annotation == null) {
			LOG.warn(String.format("ignore to extract the field, because of it don't set Extract Query, at:%s",
					field.getName()));
			return value;
		}

		// handle ExtractedBean case
		if (ExtractedBean.class.equals(annotation.annotationType())) {
			ExtractedBean extractedBean = ((ExtractedBean) annotation);
			if ("javaBean".equals(methodName)) {
				value = extractedBean.clazz();
				return value;
			}
			annotation = extractedBean.border();
		}

		try {
			Method method = annotation.getClass().getDeclaredMethod(methodName);
			if (method != null) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				value = method.invoke(annotation);
				if (method.getName().equals("dataMap")) {
					int len = Array.getLength(value);
					Map<String, Object> dataMap = new HashMap<String, Object>();
					for (int i = 0; i < len; i++) {
						KV kv = (KV) Array.get(value, i);
						String key = kv.key();
						String strVal = kv.value();
						Class<?> clazz = kv.clazz();
						SupportedClassType supportedClassType = SupportedClassType.fromValue(clazz);
						Object val = supportedClassType.parseValue(strVal);
						dataMap.put(key, val);
					}
					value = dataMap;
				}
			}
		} catch (Exception e) {
			LOG.warn(e.toString());
		}
		return value;
	}

	private ExtractedAnnotation getExtractedAnnotation(Annotation annotation) {
		ExtractedAnnotation extractedAnnotation = null;
		if (ExtractBy.class.equals(annotation.annotationType())) {
			extractedAnnotation = ExtractedAnnotation.ExtractBy;
		} else if (ComboExtract.class.equals(annotation.annotationType())) {
			extractedAnnotation = ExtractedAnnotation.ComboExtract;
		} else if (ComboExtracts.class.equals(annotation.annotationType())) {
			extractedAnnotation = ExtractedAnnotation.ComboExtracts;
		} else if (ExtractedBean.class.equals(annotation.annotationType())) {
			extractedAnnotation = ExtractedAnnotation.ExtractedBean;
		} else {
			extractedAnnotation = ExtractedAnnotation.UNKNOWN;
		}
		return extractedAnnotation;
	}

	private Selector getSelectorFromAnnotation(Field field) {
		Selector selector = null;
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation : annotations) {
			ExtractedAnnotation extractedAnnotation = getExtractedAnnotation(annotation);
			boolean skip = false;
			switch (extractedAnnotation) {
			case ExtractedBean:
				ExtractedBean extractedBean = (ExtractedBean) annotation;
				selector = createExtractedBeanSelector(extractedBean);
				skip = true;
				break;
			case ExtractBy:
				ExtractBy extractBy = (ExtractBy) annotation;
				selector = createSingleSelector(extractBy);
				skip = true;
				break;
			case ComboExtract:
				ComboExtract comboExtract = (ComboExtract) annotation;
				selector = createComboSelector(comboExtract);
				skip = true;
				break;
			case ComboExtracts:
				ComboExtracts comboExtracts = (ComboExtracts) annotation;
				selector = createCombosSelector(comboExtracts);
				skip = true;
				break;
			default:
				// nothing
				break;
			}
			if (skip && selector != null) {
				break;
			}
		}
		return selector;
	}

	private Selector createSingleSelector(ExtractBy extractBy) {
		String query = extractBy.value();
		ExprType type = extractBy.type();
		Class<?> impl = extractBy.impl();
		Setting setting = extractBy.setting();
		Map<String, Object> dataMap = getDataMapFromAnnotaion(setting);
		Selector selector = createSingleSelector(query, type, impl, dataMap);
		return selector;
	}

	private Map<String, Object> getDataMapFromAnnotaion(Setting setting) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(SettingConstant.GROUP_NO, setting.groupNo());
		dataMap.put(SettingConstant.OUTER_HTML, setting.outerHtml());
		dataMap.put(SettingConstant.ATTR_NAME, setting.attrName());
		dataMap.put(SettingConstant.TRIM, setting.trim());
		dataMap.put(SettingConstant.FLITERS, setting.fliters());

		Function function = setting.function();
		FuntionObj functionObj = new FuntionObj();
		functionObj.setLanguage(function.language());
		functionObj.setPath(function.path());
		functionObj.setClasspath(function.classpath());
		functionObj.setMethod(function.value());
		functionObj.setArgs(function.args());
		dataMap.put(SettingConstant.FUNCTION, functionObj);

		return dataMap;
	}

	private Selector createComboSelector(ComboExtract comboExtract) {
		Selector selector = null;
		ExtractBy[] extractBys = comboExtract.value();
		List<Selector> selectorList = new ArrayList<Selector>();
		for (ExtractBy extractBy : extractBys) {
			Selector _selector = createSingleSelector(extractBy);
			selectorList.add(_selector);
		}
		OP op = comboExtract.op();
		selector = appendSelector(op, selectorList);
		return selector;
	}

	private Selector createCombosSelector(ComboExtracts comboExtracts) {
		Selector selector = null;
		ComboExtract[] comboExtractArray = comboExtracts.value();
		List<Selector> selectorList = new ArrayList<Selector>();
		for (ComboExtract comboExtract : comboExtractArray) {
			Selector _selector = createComboSelector(comboExtract);
			selectorList.add(_selector);
		}
		OP op = comboExtracts.op();
		selector = appendSelector(op, selectorList);
		return selector;
	}

	private Selector createExtractedBeanSelector(ExtractedBean extractedBean) {
		Selector selector = null;
		ExtractBy extractBy = extractedBean.border();
		selector = createSingleSelector(extractBy);
		return selector;
	}

	public static BasicExtractor me() {
		return new AnnotationExtractor();
	}

}
