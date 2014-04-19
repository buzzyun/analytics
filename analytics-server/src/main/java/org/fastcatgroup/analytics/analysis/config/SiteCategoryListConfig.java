//package org.fastcatgroup.analytics.analysis.config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;
//
//@XmlRootElement(name = "site-category-list")
//public class SiteCategoryListConfig {
//	
//	private List<SiteCategoryConfig> list;
//
//	@XmlElement(name = "site-category")
//	public List<SiteCategoryConfig> getList() {
//		return list;
//	}
//
//	public void setList(List<SiteCategoryConfig> list) {
//		this.list = list;
//	}
//
//	@XmlType
//	public static class SiteCategoryConfig {
//		
//		private String siteId;
//		private String siteName;
//		private List<CategoryConfig> categoryList;
//		
//		public SiteCategoryConfig(){}
//		
//		public SiteCategoryConfig(String siteId, String siteName){
//			this.siteId = siteId;
//			this.siteName = siteName;
//			categoryList = new ArrayList<CategoryConfig>();
//		}
//		
//		@XmlAttribute
//		public String getSiteId() {
//			return siteId;
//		}
//		public void setSiteId(String siteId) {
//			this.siteId = siteId;
//		}
//		
//		@XmlAttribute
//		public String getSiteName() {
//			return siteName;
//		}
//		public void setSiteName(String siteName) {
//			this.siteName = siteName;
//		}
//		
//		@XmlElement(name ="category")
//		public List<CategoryConfig> getCategoryList() {
//			return categoryList;
//		}
//		public void setCategoryList(List<CategoryConfig> categoryList) {
//			this.categoryList = categoryList;
//		}
//	}
//	
//	@XmlType
//	public static class CategoryConfig {
//		private String id;
//		private String name;
//		
//		public CategoryConfig(){}
//		
//		public CategoryConfig(String id, String name){
//			this.id = id;
//			this.name = name;
//		}
//		
//		@XmlAttribute
//		public String getId() {
//			return id;
//		}
//		public void setId(String id) {
//			this.id = id;
//		}
//		
//		@XmlAttribute
//		public String getName() {
//			return name;
//		}
//		public void setName(String name) {
//			this.name = name;
//		}
//		
//	}
//}
