package org.fastcatsearch.analytics.analysis.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="statistics")
public class StatisticsSettings {
	
	private List<CategorySetting> categoryList;
	private RealTimePopularKeywordSetting realtimePopularKeywordSetting;
	private PopularKeywordSetting popularKeywordSetting;
	private RelateKeywordSetting relateKeywordSetting;
	private CTRSetting ctrSetting;
	private SiteProperties siteProperties;
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
	
	@XmlElement(name="ctr")
	public CTRSetting getCtrSetting() {
		return ctrSetting;
	}

	public void setCtrSetting(CTRSetting ctrSetting) {
		this.ctrSetting = ctrSetting;
	}
	
	@XmlElement(name="properties")
	public SiteProperties getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(SiteProperties siteProperties) {
		this.siteProperties = siteProperties;
	}

	@XmlElement(name="site-attribute")
	public SiteAttribute getSiteAttribute() {
		return siteAttribute;
	}
	
	public void setSiteAttribute (SiteAttribute siteAttribute) {
		this.siteAttribute = siteAttribute;
	}
	
	@XmlType
	public static class SiteProperties {
		
		private String banwords;
		private int maxKeywordLength;
		
		@XmlElement
		public String getBanwords() {
			return banwords;
		}
		
		public void setBanwords(String banwords) {
			this.banwords = banwords;
		}

		public Set<String> getBanwordSet() {
			String[] list = banwords.split("\t");
			Set<String> set = new HashSet<String>();
			for(String w : list) {
				set.add(w);
			}
			return set;
		}
		
		@XmlElement
		public int getMaxKeywordLength() {
			return maxKeywordLength;
		}

		public void setMaxKeywordLength(int maxKeywordLength) {
			this.maxKeywordLength = maxKeywordLength;
		}
		
		
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
	
	@XmlType 
	public static class CTRSetting {
		private Integer dumpFileDaySize;
		private String targetFilePath;

		public CTRSetting() { }

		@XmlElement
		public Integer getDumpFileDaySize() {
			return dumpFileDaySize == null ? 0 : dumpFileDaySize;
		}

		public void setDumpFileDaySize(Integer dumpFileDaySize) {
			if(dumpFileDaySize == null) {
				dumpFileDaySize = 0;
			}
			this.dumpFileDaySize = dumpFileDaySize;
		}
		
		@XmlElement
		public String getTargetFilePath() {
			return targetFilePath;
		}

		public void setTargetFilePath(String targetFilePath) {
			this.targetFilePath = targetFilePath;
		}
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
		private Boolean prime;
		public TypeSetting() { }
		public TypeSetting(String id, String name, Boolean prime) { 
			super(id, name); 
			this.setPrime(prime);
		}
		
		@XmlAttribute(name = "isPrime")
		public Boolean isPrime() {
			if(prime!=null) {
				return prime;
			}
			return false;
		}
		public void setPrime(Boolean prime) {
			this.prime = prime;
		}
	}
	
	@XmlType public static class ServiceSetting extends IdNameSetting {
		private Boolean prime;
		public ServiceSetting() { }
		public ServiceSetting(String id, String name, Boolean prime) { 
			super(id, name); 
			this.setPrime(prime);
		}
		@XmlAttribute(name = "isPrime")
		public Boolean isPrime() {
			if(prime!=null) {
				return prime;
			}
			return false;
		}
		public void setPrime(Boolean prime) {
			this.prime = prime;
		}
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