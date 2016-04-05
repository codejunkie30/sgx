package com.wmsi.sgx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "Configurations")
@Table(name="configurations")
public class Configurations {
	
	@Id
	@GeneratedValue(generator = "configurationsGenerator")
	@GenericGenerator(name = "configurationsGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;
	
	@Column(name = "Property", nullable = false)
	private String property;
	@Column(name = "Value", nullable = false)
	private String value;
	@Column(name = "ChangedBy", nullable = false)
	private String modifiedBy;
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("property", property).add("value", value).add("modifiedBy", modifiedBy)
				.toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(property, value, modifiedBy);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof Configurations) {
			Configurations that = (Configurations) object;
			return Objects.equal(this.property, that.property) && Objects.equal(this.value, that.value)
					&& Objects.equal(this.modifiedBy, that.modifiedBy);
		}
		return false;
	}
	
	
}
