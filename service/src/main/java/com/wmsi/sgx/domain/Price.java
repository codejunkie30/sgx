package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "prices")
public class Price{

	@Id
	@GeneratedValue(generator = "priceGenerator")
	@GenericGenerator(name = "priceGenerator", strategy = "increment")
	private Long id;

	@ManyToOne(cascade = { CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "assets_id")
	private Asset asset;

	@Column(name = "time")
	private Date time;

	@Column(name = "price")
	private Double price;

	@Column(name = "volume")
	private Double volume;

	public Long getId() {
		return id;
	}

	public Asset getAsset() {
		return asset;
	}

	public Date getTime() {
		return time;
	}

	public Double getPrice() {
		return price;
	}

	public Double getVolume() {
		return volume;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
}
