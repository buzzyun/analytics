package org.fastcatgroup.analytics.analysis.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "site-list")
public class SiteListConfig {
	
	private List<SiteConfig> list;

	public void setList(List<SiteConfig> list) {
		this.list = list;
	}

	public static class SiteConfig {
		private String id;
		private String name;
		
		public SiteConfig() { }
		
		public SiteConfig(String id, String name) {
			this.id=id;
			this.name=name;
		}
		
		@XmlAttribute
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		@XmlAttribute
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
}
