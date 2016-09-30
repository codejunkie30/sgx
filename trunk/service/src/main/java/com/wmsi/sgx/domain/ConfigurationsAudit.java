package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "ConfigurationsAudit")
@Table(name="configurations_audit")
public class ConfigurationsAudit {
	
	@Id
	@GeneratedValue(generator = "configurationsAuditGenerator")
	@GenericGenerator(name = "configurationsAuditGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long audit_id;
	
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "Property", nullable = false)
	private String property;
	@Column(name = "Value", nullable = false)
	private String value;
	@Column(name = "ChangedBy", nullable = false)
	private String modifiedBy;
	@Column(name = "ChangedDt", nullable = false)
	private Date modifiedDate;
	
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
	
	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("property", property).add("value", value).add("modifiedBy", modifiedBy).add("modifiedDate", modifiedDate)
				.add("id", id).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(property, value, modifiedBy, modifiedDate,id);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof ConfigurationsAudit) {
			ConfigurationsAudit that = (ConfigurationsAudit) object;
			return Objects.equal(this.property, that.property) && Objects.equal(this.value, that.value)
					&& Objects.equal(this.modifiedBy, that.modifiedBy)
					&& Objects.equal(this.modifiedDate, that.modifiedDate)
					&& Objects.equal(this.id, that.id);
		}
		return false;
	}
	
	
}
