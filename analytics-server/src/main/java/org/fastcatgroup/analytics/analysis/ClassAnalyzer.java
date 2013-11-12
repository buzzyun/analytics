package org.fastcatgroup.analytics.analysis;

import java.util.List;
import java.util.Map;

public class ClassAnalyzer {

	Map<String, List<FeatureDefinition>> map;
	
	
	
	FeatureValueAggregator featureValueAggregator;
	
	public void run(){
		
		
		String line = null;
		
		while((line = readLine()) != null){
			FeatureValue[] featureValueList = getFeatureValue(line);
			for(FeatureValue featureValue : featureValueList){
				addFeatureValue(featureValue);
			}
		}
		
		
	}

	private void addFeatureValue(FeatureValue featureValue) {
		List<FeatureDefinition> featureDefinitionList = map.get(featureValue.getId());
		for(FeatureDefinition featureDefinition : featureDefinitionList){
			//여러 feature에 데이터를 채워넣는다.
			
			
			
			
//			featureValueAggregator.add(featureValue);
		}
		
	}

	private FeatureValue[] getFeatureValue(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	class FeatureValue {
		private String id;
		private String value;
		
		public FeatureValue(String id, String value){
			this.id = id;
			this.value = value;
		}
		
		public String getId() {
			return id;
		}
		public String getValue() {
			return value;
		}
	}
	private String readLine() {
		// TODO Auto-generated method stub
		return null;
	}
}
