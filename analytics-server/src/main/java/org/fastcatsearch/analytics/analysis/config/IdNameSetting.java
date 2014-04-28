package org.fastcatsearch.analytics.analysis.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "id", "name" })
public abstract class IdNameSetting {
	private String id;
	private String name;
	
	public IdNameSetting() { }
	
	public IdNameSetting(String id, String name) {
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
