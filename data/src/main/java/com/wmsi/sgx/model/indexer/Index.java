package com.wmsi.sgx.model.indexer;

import java.util.List;
import com.google.common.base.Objects;
/**
 * Elastic Search Index Model
 * 
 */
public class Index{

	String name;
	List<String> aliases;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name, aliases);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Index) {
			Index that = (Index) object;
			return Objects.equal(this.name, that.name)
				&& Objects.equal(this.aliases, that.aliases);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("name", name)
			.add("aliases", aliases)
			.toString();
	}	
	
}
