package org.fastcatgroup.analytics.analysis.config;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "site-list")
public class SiteListSetting {
	
	private List<SiteSetting> siteList;

	public List<SiteSetting> getSiteList() {
		return siteList;
	}
	public void setSiteList(List<SiteSetting> siteList) {
		this.siteList = siteList;
	}

	public static class SiteSetting extends IdNameSetting {
		public SiteSetting(String id, String name) {
			super(id,name);
		}
	}
}
