package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "Watchlist")
@Table(name="watchlist")
public class Watchlist {
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Id
	@GeneratedValue(generator = "watchlistGenerator")
	@GenericGenerator(name = "watchlistGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long watchlist_id;
	
	@Column(name = "date_created", nullable = false)
	private Date date_created = new Date();
	
	@Column(name = "watchlist_name", nullable = false)
	private String name;
	
	@Column(name = "updated_dt", nullable = false)
	private Date updatedDate = new Date();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getWatchlist_id() {
		return watchlist_id;
	}

	public void setWatchlist_id(Long watchlist_id) {
		this.watchlist_id = watchlist_id;
	}

	public Date getDate_created() {
		return date_created;
	}

	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(user, watchlist_id, date_created, name, updatedDate);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Watchlist) {
			Watchlist that = (Watchlist) object;
			return Objects.equal(this.user, that.user)
				&& Objects.equal(this.watchlist_id, that.watchlist_id)
				&& Objects.equal(this.date_created, that.date_created)
				&& Objects.equal(this.name, that.name)
			    && Objects.equal(this.updatedDate, that.updatedDate);
		}
		return false;
	}@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("user", user)
			.add("watchlist_id", watchlist_id)
			.add("date_created", date_created)
			.add("name", name)
			.add("updatedDate", updatedDate)
			.toString();
	}

	

	
	
}
