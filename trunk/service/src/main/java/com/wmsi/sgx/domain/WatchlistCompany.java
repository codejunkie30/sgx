package com.wmsi.sgx.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import com.google.common.base.Objects;

@Entity(name = "WatchlistCompany")
@Table(name="watchlist_company")
public class WatchlistCompany {
	@Id
	@GeneratedValue(generator = "watchlistCompanyGenerator")
	@GenericGenerator(name = "watchlistCompanyGenerator", strategy = "increment")
	private Long id;
	
	@Column(name = "watchlist_id", nullable = false)
	public Long watchlistId;
	
	@Column(name = "tickerCode", nullable = false)
	public String tickerCode;

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

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id, watchlistId, tickerCode);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WatchlistCompany) {
			WatchlistCompany that = (WatchlistCompany) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.watchlistId, that.watchlistId)
				&& Objects.equal(this.tickerCode, that.tickerCode);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("watchlistId", watchlistId)
			.add("tickerCode", tickerCode)
			.toString();
	}

	
	
	
}
