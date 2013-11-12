package org.fastcatgroup.analytics.analysis.valueType;

import org.fastcatgroup.analytics.analysis.ValueType;

public class IntegerValueType implements ValueType<Integer> {
	private Integer defaultValue;

	public IntegerValueType(Integer defaultValue){
		this.defaultValue = defaultValue;
	}
	
	@Override
	public Integer getValue(String stringValue) {
		if (stringValue == null) {
			return null;
		}

		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Integer defaultValue() {
		return defaultValue;
	}

}
