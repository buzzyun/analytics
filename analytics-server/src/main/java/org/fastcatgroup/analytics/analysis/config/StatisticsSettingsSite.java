package org.fastcatgroup.analytics.analysis.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="statistics")
public class StatisticsSettingsSite {
	
	private List<CategorySetting> categoryList;
	private String banwords;
	private String fileEncoding;
	
	@XmlElement(name="category-list")
	public List<CategorySetting> getCategoryList() {
		return categoryList;
	}
	
	
	@XmlElement(name="banwords")
	public String getBanwords() {
		return banwords;
	}
	
	@XmlElement(name="file-encoding")
	public String getFileEncoding() {
		return fileEncoding;
	}
	
	public static class CategorySetting {
		private String id;
		private String name;
		
		public CategorySetting() { }
		public CategorySetting(String id, String name) {
			this.id=id;
			this.name=name;
		}
		
		@XmlAttribute
		public String getId() {
			return id;
		}
		
		@XmlAttribute
		public String getName() {
			return name;
		}
	}
}
