package org.fastcatgroup.analytics.analysis.valueType;

import org.fastcatgroup.analytics.analysis.ValueType;

public class LongValueType implements ValueType<Long> {

	private Long defaultValue;
	
	public LongValueType(Long defaultValue){
		this.defaultValue = defaultValue;
	}
	@Override
	public Long getValue(String stringValue) {
		if (stringValue == null) {
			return null;
		}

		try {
			return Long.parseLong(stringValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Long defaultValue() {
		// TODO Auto-generated method stub
		return defaultValue;
	}

}
