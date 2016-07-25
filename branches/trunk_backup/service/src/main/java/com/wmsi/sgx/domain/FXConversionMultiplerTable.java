package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "daily_fx_multiplier")
@Table(name="daily_fx_multiplier")
public class FXConversionMultiplerTable {
	
	@Id
	@GeneratedValue(generator = "fXConversionMultiplerGenerator")
	@GenericGenerator(name = "fXConversionMultiplerGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;

	@Column(name = "date", nullable = false)
	private Date date;
	
	@Column(name = "sgd_to_myr", nullable = false)
	private double sgd_to_myr;
	
	@Column(name = "sgd_to_usd", nullable = false)
	private double sgd_to_usd;
	
	@Column(name = "sgd_to_hkd", nullable = false)
	private double sgd_to_hkd;
	
	@Column(name = "sgd_to_idr", nullable = false)
	private double sgd_to_idr;
	
	@Column(name = "sgd_to_php", nullable = false)
	private double sgd_to_php;
	
	@Column(name = "sgd_to_thb", nullable = false)
	private double sgd_to_thb;
	
	@Column(name = "sgd_to_twd", nullable = false)
	private double sgd_to_twd;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getSgd_to_myr() {
		return sgd_to_myr;
	}

	public void setSgd_to_myr(double sgd_to_myr) {
		this.sgd_to_myr = sgd_to_myr;
	}

	public double getSgd_to_usd() {
		return sgd_to_usd;
	}

	public void setSgd_to_usd(double sgd_to_usd) {
		this.sgd_to_usd = sgd_to_usd;
	}

	public double getSgd_to_hkd() {
		return sgd_to_hkd;
	}

	public void setSgd_to_hkd(double sgd_to_hkd) {
		this.sgd_to_hkd = sgd_to_hkd;
	}

	public double getSgd_to_idr() {
		return sgd_to_idr;
	}

	public void setSgd_to_idr(double sgd_to_idr) {
		this.sgd_to_idr = sgd_to_idr;
	}

	public double getSgd_to_php() {
		return sgd_to_php;
	}

	public void setSgd_to_php(double sgd_to_php) {
		this.sgd_to_php = sgd_to_php;
	}

	public double getSgd_to_thb() {
		return sgd_to_thb;
	}

	public void setSgd_to_thb(double sgd_to_thb) {
		this.sgd_to_thb = sgd_to_thb;
	}

	public double getSgd_to_twd() {
		return sgd_to_twd;
	}

	public void setSgd_to_twd(double sgd_to_twd) {
		this.sgd_to_twd = sgd_to_twd;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("date", date).add("sgd_to_myr", sgd_to_myr)
				.add("sgd_to_usd", sgd_to_usd).add("sgd_to_hkd", sgd_to_hkd).add("sgd_to_idr", sgd_to_idr)
				.add("sgd_to_php", sgd_to_php).add("sgd_to_thb", sgd_to_thb).add("sgd_to_twd", sgd_to_twd).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(date, sgd_to_myr, sgd_to_usd, sgd_to_hkd, sgd_to_idr, sgd_to_php, sgd_to_thb,
				sgd_to_twd);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FXConversionMultiplerTable) {
			FXConversionMultiplerTable that = (FXConversionMultiplerTable) object;
			return Objects.equal(this.date, that.date) && Objects.equal(this.sgd_to_myr, that.sgd_to_myr)
					&& Objects.equal(this.sgd_to_usd, that.sgd_to_usd)
					&& Objects.equal(this.sgd_to_hkd, that.sgd_to_hkd)
					&& Objects.equal(this.sgd_to_idr, that.sgd_to_idr)
					&& Objects.equal(this.sgd_to_php, that.sgd_to_php)
					&& Objects.equal(this.sgd_to_thb, that.sgd_to_thb)
					&& Objects.equal(this.sgd_to_twd, that.sgd_to_twd);
		}
		return false;
	}
	
	
	
	
}
