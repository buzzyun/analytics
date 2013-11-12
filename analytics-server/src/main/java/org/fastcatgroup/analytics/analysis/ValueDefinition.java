package org.fastcatgroup.analytics.analysis;

public class ValueDefinition {
	protected String valueId;
	protected String valueName;
	protected ValueType valueType;
	
	public ValueObject createValueObject(String stringValue){
		Object value = valueType.getValue(stringValue);
		return new ValueObject(this, value);
	}
}
