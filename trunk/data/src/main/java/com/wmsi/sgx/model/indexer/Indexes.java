package com.wmsi.sgx.model.indexer;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
/**
 * Elastic Search Index List
 * 
 */
@JsonDeserialize(using=IndexesDeserializer.class)
public class Indexes{

	private List<Index> indexes;

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("indexes", indexes)
			.toString();
	}

}
