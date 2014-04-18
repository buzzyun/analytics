package org.fastcatgroup.analytics.analysis.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="statistics")
public class StatisticsSettings {
	
	private List<CategorySetting> categoryList;
	private String banwords;
	private String fileEncoding;
	private RealtimePopularKeywordSetting realtimePopularKeywordSetting;
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
	public RealtimePopularKeywordSetting getRealtimePopularKeywordSetting() {
		return realtimePopularKeywordSetting;
	}
	
	public void setRealtimePopularKeywordSetting(RealtimePopularKeywordSetting setting) {
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
	
	public static class RealtimePopularKeywordSetting extends PopularKeywordSetting {
		private int recentCount;
		
		public RealtimePopularKeywordSetting() { }
		
		public RealtimePopularKeywordSetting(int recentCount, int topCount, int minimumHitCount) { 
			super(topCount, minimumHitCount);
			this.recentCount = recentCount;
		}
		@XmlElement(name="recentLogUsingCount")
		public int getRecentCount() {
			return recentCount;
		}
		public void setRecentCount(int recentCount) {
			this.recentCount = recentCount;
		}
		
	}
	
	public static class PopularKeywordSetting extends KeywordSetting {
		private int topCount;
		public PopularKeywordSetting() { }
		public PopularKeywordSetting(int topCount, int minimumHitCount) {
			super(minimumHitCount);
			this.topCount = topCount;
		}
		@XmlElement(name="topCount")
		public int getTopCount() {
			return topCount;
		}
		public void setTopCount(int topCount) {
			this.topCount = topCount;
		}
	}
	
	public static class RelateKeywordSetting extends KeywordSetting {
		public RelateKeywordSetting(int i) { super(i); } 
	}
	
	public static abstract class KeywordSetting {
		private int minimumHitCount;
		
		public KeywordSetting() { }
		public KeywordSetting(int minimumHitCount) { 
			this.minimumHitCount = minimumHitCount;
		}
		@XmlElement(name="minimumHitCount")
		public int getMinimumHitCount() {
			return minimumHitCount;
		}
		public void setMinimumHitCount(int minimumHitCount) {
			this.minimumHitCount = minimumHitCount;
		}
	}
	
	public static class CategorySetting extends IdNameSetting {
		private boolean usePopularKeyword;
		private boolean useRelateKeyword;
		private boolean useRealtimePopularKeyword;
		public CategorySetting(String id, String name, boolean usePopularKeyword, boolean useRelateKeyword, boolean useRealtimePopularKeyword) { 
			super(id, name); 
			this.usePopularKeyword = usePopularKeyword;
			this.useRelateKeyword = useRelateKeyword;
			this.useRealtimePopularKeyword = useRealtimePopularKeyword;
		}
		
		@XmlAttribute(name="usePopularKeyword")
		public boolean isUsePopularKeyword() {
			return usePopularKeyword;
		}
		public void setUsePopularKeyword(boolean usePopularKeyword) {
			this.usePopularKeyword = usePopularKeyword;
		}
		@XmlAttribute(name="useRelateKeyword")
		public boolean isUseRelateKeyword() {
			return useRelateKeyword;
		}
		public void setUseRelateKeyword(boolean useRelateKeyword) {
			this.useRelateKeyword = useRelateKeyword;
		}
		@XmlAttribute(name="useRealtimePopularKeyword")
		public boolean isUseRealtimePopularKeyword() {
			return useRealtimePopularKeyword;
		}
		public void setUseRealtimePopularKeyword(boolean useRealtimePopularKeyword) {
			this.useRealtimePopularKeyword = useRealtimePopularKeyword;
		}
		
	}
	
	public static class TypeSetting extends IdNameSetting {
		public TypeSetting(String id, String name) { super(id, name); }
	}
	
	public static class ServiceSetting extends IdNameSetting {
		public ServiceSetting(String id, String name) { super(id, name); }
	}
	
	public static class ClickTypeSetting extends IdNameSetting {
		public ClickTypeSetting(String id, String name) { super(id, name); }
	}
}
