package org.fastcatgroup.analytics.analysis.valueType;

import org.fastcatgroup.analytics.analysis.ValueType;

public class EnumValueType implements ValueType<String> {
	private String[] valueList;
	private String defaultValue;
	
	public EnumValueType(String[] valueList, String defaultValue){
		this.valueList = valueList;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getValue(String stringValue) {
		if(stringValue == null){
			return null;
		}
		for(String enumValue : valueList){
			if(enumValue.equalsIgnoreCase(stringValue)){
				return enumValue;
			}
		}
		return null;
	}

	@Override
	public String defaultValue() {
		return defaultValue;
	}

}
