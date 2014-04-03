package com.wmsi.sgx.model.search;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.wmsi.sgx.util.TemplateUtil;

public class SearchRequest{

	@Valid
	@NotNull
	@Size(min = 1, max = 5, message="Invalid criteria size")
	private List<Criteria> criteria;
	
	public List<Criteria> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criteria> criteria) {
		this.criteria = criteria;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("criteria", criteria)
			.toString();
	}
}
