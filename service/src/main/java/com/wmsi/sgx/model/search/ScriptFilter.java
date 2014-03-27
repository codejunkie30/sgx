package com.wmsi.sgx.model.search;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CustomSerializer.class)
public class ScriptFilter extends AbstractQuery{

	private String script;
	private List<Param> params;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<Param> getParams() {
		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}

}
