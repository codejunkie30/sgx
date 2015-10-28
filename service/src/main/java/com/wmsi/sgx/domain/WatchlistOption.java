package com.wmsi.sgx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "WatchlistOption")
@Table(name="watchlist_option")
public class WatchlistOption {
	@Id
	@GeneratedValue(generator = "watchlistOptionGenerator")
	@GenericGenerator(name = "watchlistOptionGenerator", strategy = "increment")
	private Long id;
	
	
	@Column(name = "watchlist_id", nullable = false)
	public Long watchlistId;
	
	@Column(name = "alert_option", nullable = false)
	public String alert_option;
	
	@Column(name = "option_value", nullable = false)
	public String option_value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWatchlistId() {
		return watchlistId;
	}

	public void setWatchlistId(Long watchlistId) {
		this.watchlistId = watchlistId;
	}

	public String getAlert_option() {
		return alert_option;
	}

	public void setAlert_option(String alert_option) {
		this.alert_option = alert_option;
	}

	public String getOption_value() {
		return option_value;
	}

	public void setOption_value(String option_value) {
		this.option_value = option_value;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id, watchlistId, alert_option, option_value);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WatchlistOption) {
			WatchlistOption that = (WatchlistOption) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.watchlistId, that.watchlistId)
				&& Objects.equal(this.alert_option, that.alert_option)
				&& Objects.equal(this.option_value, that.option_value);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("watchlistId", watchlistId)
			.add("alert_option", alert_option)
			.add("option_value", option_value)
			.toString();
	}

	

	
	
}
	