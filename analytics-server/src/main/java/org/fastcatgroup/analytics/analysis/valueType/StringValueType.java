package org.fastcatgroup.analytics.analysis.valueType;

import org.fastcatgroup.analytics.analysis.ValueType;

public class StringValueType implements ValueType<String> {

	@Override
	public String getValue(String stringValue) {
		return stringValue;
	}

	@Override
	public String defaultValue() {
		return null;
	}

}
