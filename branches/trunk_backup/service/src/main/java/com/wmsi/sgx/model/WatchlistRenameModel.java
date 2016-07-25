package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class WatchlistRenameModel {
	public String watchlistName;
	public String id;
	public String getWatchlistName() {
		return watchlistName;
	}
	public void setWatchlistName(String watchlistName) {
		this.watchlistName = watchlistName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(watchlistName, id);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WatchlistRenameModel) {
			WatchlistRenameModel that = (WatchlistRenameModel) object;
			return Objects.equal(this.watchlistName, that.watchlistName)
				&& Objects.equal(this.id, that.id);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("watchlistName", watchlistName)
			.add("id", id)
			.toString();
	}
	
	
}
