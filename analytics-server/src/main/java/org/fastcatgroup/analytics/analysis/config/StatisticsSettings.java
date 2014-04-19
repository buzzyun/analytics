package org.fastcatgroup.analytics.analysis.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="statistics")
public class StatisticsSettings {
	
	private List<CategorySetting> categoryList;
	private String banwords;
	private String fileEncoding;
	private RealTimePopularKeywordSetting realtimePopularKeywordSetting;
	private PopularKeywordSetting popularKeywordSetting;
	private RelateKeywordSetting relateKeywordSetting;
	private SiteAttribute siteAttribute;
	
	public StatisticsSettings() { }
	
	@XmlElementWrapper(name="category-list")
	@XmlElement(name="category")
	public List<CategorySetting> getCategoryList() {
		return categoryList;
	}
	
	public void setCategoryList(List<CategorySetting> categoryList) {
		this.categoryList = categoryList;
	}
	
	@XmlElement(name="banwords")
	public String getBanwords() {
		return banwords;
	}
	
	public void setBanwords(String banwords) {
		this.banwords = banwords;
	}
	
	@XmlElement(name="file-encoding")
	public String getFileEncoding() {
		return fileEncoding;
	}
	
	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
	
	@XmlElement(name="realtime-popular-keyword")
	public RealTimePopularKeywordSetting getRealtimePopularKeywordSetting() {
		return realtimePopularKeywordSetting;
	}
	
	public void setRealtimePopularKeywordSetting(RealTimePopularKeywordSetting setting) {
		this.realtimePopularKeywordSetting = setting;
	}
	
	@XmlElement(name="popular-keyword")
	public PopularKeywordSetting getPopularKeywordSetting() {
		return popularKeywordSetting;
	}
	
	public void setPopularKeywordSetting(PopularKeywordSetting setting) {
		this.popularKeywordSetting = setting;
	}
	
	@XmlElement(name="relate-keyword")
	public RelateKeywordSetting getRelateKeywordSetting() {
		return relateKeywordSetting;
	}
	
	public void setRelateKeywordSetting(RelateKeywordSetting setting) {
		this.relateKeywordSetting = setting;
	}
	
	@XmlElement(name="site-attribute")
	public SiteAttribute getSiteAttribute() {
		return siteAttribute;
	}
	
	public void setSiteAttribute (SiteAttribute siteAttribute) {
		this.siteAttribute = siteAttribute;
	}
	
	@XmlType 
	public static class SiteAttribute {
		private List<TypeSetting> typeList;
		private List<ServiceSetting> serviceList;
		private List<ClickTypeSetting> clickTypeList;
		
		public SiteAttribute() {}
		public SiteAttribute(List<TypeSetting>typeList, List<ServiceSetting>serviceList, List<ClickTypeSetting>clickTypeList) {
			this.typeList = typeList;
			this.serviceList = serviceList;
			this.clickTypeList = clickTypeList;
		}

		@XmlElementWrapper(name="type-list")
		@XmlElement(name="type")
		public List<TypeSetting> getTypeList() {
			return typeList;
		}
		public void setTypeList(List<TypeSetting> typeList) {
			this.typeList = typeList;
		}
		@XmlElementWrapper(name="service-list")
		@XmlElement(name="service")
		public List<ServiceSetting> getServiceList() {
			return serviceList;
		}
		public void setServiceList(List<ServiceSetting> serviceList) {
			this.serviceList = serviceList;
		}
		@XmlElementWrapper(name="click-type-list")
		@XmlElement(name="click")
		public List<ClickTypeSetting> getClickTypeList() {
			return clickTypeList;
		}
		public void setClickTypeList(List<ClickTypeSetting> clickTypeList) {
			this.clickTypeList = clickTypeList;
		}
	}
	
	@XmlType 
	public static class RealTimePopularKeywordSetting extends PopularKeywordSetting {
		private Integer recentCount;
		public RealTimePopularKeywordSetting() { }
		public RealTimePopularKeywordSetting(Integer recentCount, Integer topCount, Integer minimumHitCount) { 
			super(topCount, minimumHitCount);
			this.recentCount = recentCount;
		}
		@XmlElement(name="recentLogUsingCount")
		public Integer getRecentCount() {
			return recentCount;
		}
		public void setRecentCount(Integer recentCount) {
			this.recentCount = recentCount;
		}
		
	}
	
	@XmlType 
	public static class PopularKeywordSetting extends KeywordSetting {
		private Integer topCount;
		public PopularKeywordSetting() { }
		public PopularKeywordSetting(Integer topCount, Integer minimumHitCount) {
			super(minimumHitCount);
			this.topCount = topCount;
		}
		@XmlElement(name="topCount")
		public Integer getTopCount() {
			return topCount;
		}
		public void setTopCount(Integer topCount) {
			this.topCount = topCount;
		}
	}
	
	@XmlType 
	public static class RelateKeywordSetting extends KeywordSetting {
		public RelateKeywordSetting() { }
		public RelateKeywordSetting(Integer i) { super(i); } 
	}
	
	@XmlType(propOrder = { "useRelateKeyword", "usePopularKeyword", "useRealTimePopularKeyword"})
	public static class CategorySetting extends IdNameSetting {
		private Boolean usePopularKeyword;
		private Boolean useRelateKeyword;
		private Boolean useRealTimePopularKeyword;
		public CategorySetting() { }
		public CategorySetting(String id, String name, Boolean usePopularKeyword, Boolean useRelateKeyword, Boolean useRealTimePopularKeyword) { 
			super(id, name); 
			this.usePopularKeyword = usePopularKeyword;
			this.useRelateKeyword = useRelateKeyword;
			this.useRealTimePopularKeyword = useRealTimePopularKeyword;
		}
		
		@XmlAttribute(name="usePopularKeyword")
		public Boolean isUsePopularKeyword() {
			return usePopularKeyword;
		}
		public void setUsePopularKeyword(Boolean usePopularKeyword) {
			this.usePopularKeyword = usePopularKeyword;
		}
		@XmlAttribute(name="useRelateKeyword")
		public Boolean isUseRelateKeyword() {
			return useRelateKeyword;
		}
		public void setUseRelateKeyword(Boolean useRelateKeyword) {
			this.useRelateKeyword = useRelateKeyword;
		}
		@XmlAttribute(name="useRealTimePopularKeyword")
		public Boolean isUseRealTimePopularKeyword() {
			return useRealTimePopularKeyword;
		}
		public void setUseRealTimePopularKeyword(Boolean useRealTimePopularKeyword) {
			this.useRealTimePopularKeyword = useRealTimePopularKeyword;
		}
		
	}
	
	@XmlType public static class TypeSetting extends IdNameSetting {
		public TypeSetting() { }
		public TypeSetting(String id, String name) { super(id, name); }
	}
	
	@XmlType public static class ServiceSetting extends IdNameSetting {
		public ServiceSetting() { }
		public ServiceSetting(String id, String name) { super(id, name); }
	}
	
	@XmlType public static class ClickTypeSetting extends IdNameSetting {
		public ClickTypeSetting() { }
		public ClickTypeSetting(String id, String name) { super(id, name); }
	}
	
	public static abstract class KeywordSetting {
		private Integer minimumHitCount;
		
		public KeywordSetting() { }
		public KeywordSetting(Integer minimumHitCount) { 
			this.minimumHitCount = minimumHitCount;
		}
		@XmlElement(name="minimumHitCount")
		public Integer getMinimumHitCount() {
			return minimumHitCount;
		}
		public void setMinimumHitCount(Integer minimumHitCount) {
			this.minimumHitCount = minimumHitCount;
		}
	}
}
