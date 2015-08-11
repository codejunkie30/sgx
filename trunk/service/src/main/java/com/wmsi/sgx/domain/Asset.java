package com.wmsi.sgx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "Assets")
public class Asset{

		@Id
		@GeneratedValue(generator = "assetGenerator")
		@GenericGenerator(name = "assetGenerator", strategy = "increment")
		private Long id;

		@Column(name = "ticker")
		private String ticker;
		
		@Column(name = "isin")
		private String isin;
		
		@Column(name = "last")
		private double last;

		@Column(name = "bid")
		private double bid;

		@Column(name = "ask")
		private double ask;
		
		@Column(name = "volume")
		private double volume;

		public Long getId() {
			return id;
		}

		public String getTicker() {
			return ticker;
		}

		public String getIsin() {
			return isin;
		}

		public double getLast() {
			return last;
		}

		public double getBid() {
			return bid;
		}

		public double getAsk() {
			return ask;
		}

		public double getVolume() {
			return volume;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public void setTicker(String ticker) {
			this.ticker = ticker;
		}

		public void setIsin(String isin) {
			this.isin = isin;
		}

		public void setLast(double last) {
			this.last = last;
		}

		public void setBid(double bid) {
			this.bid = bid;
		}

		public void setAsk(double ask) {
			this.ask = ask;
		}

		public void setVolume(double volume) {
			this.volume = volume;
		}

}
