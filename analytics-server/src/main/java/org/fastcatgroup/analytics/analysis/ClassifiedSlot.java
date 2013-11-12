package org.fastcatgroup.analytics.analysis;

public class ClassifiedSlot {

	private String classId;
	private ValueDefinition valueDefinition;
	
	public ClassifiedSlot(ValueDefinition valueDefinition){
		this.valueDefinition = valueDefinition;
	}
	
	public void put(String stringValue){
		Object value = valueDefinition.createValueObject(stringValue);
		
	}
}
