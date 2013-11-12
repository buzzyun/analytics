package org.fastcatgroup.analytics.analysis;

public class ValueObject {
	
	protected ValueDefinition valueDefinition;
	protected Object value;
	
	public ValueObject(ValueDefinition valueDefinition, Object value) {
		this.valueDefinition = valueDefinition;
		this.value = value;
	}
}
