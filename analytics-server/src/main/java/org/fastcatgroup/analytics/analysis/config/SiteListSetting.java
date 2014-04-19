package org.fastcatgroup.analytics.analysis.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "site-list")
public class SiteListSetting {
	private List<SiteSetting> siteList;

	@XmlElement(name="site")
	public List<SiteSetting> getSiteList() {
		return siteList;
	}
	public void setSiteList(List<SiteSetting> siteList) {
		this.siteList = siteList;
	}

	public static class SiteSetting extends IdNameSetting {
		private StatisticsSettings statisticsSettings;
		public SiteSetting() { }
		public SiteSetting(String id, String name) {
			super(id,name);
		}
		
		//수동으로 넣어줄 것들.
		@XmlTransient
		public StatisticsSettings getStatisticsSettings() {
			return statisticsSettings;
		}
		public void setStatisticsSettings(StatisticsSettings statisticsSettings) {
			this.statisticsSettings = statisticsSettings;
		}
	}
}
