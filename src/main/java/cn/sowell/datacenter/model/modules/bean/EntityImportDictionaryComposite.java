package cn.sowell.datacenter.model.modules.bean;

import java.util.List;

public class EntityImportDictionaryComposite {
	private Long id;
	private String name;
	private String type;
	private List<EntityImportDictionaryField> fields;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<EntityImportDictionaryField> getFields() {
		return fields;
	}
	public void setFields(List<EntityImportDictionaryField> fields) {
		this.fields = fields;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
