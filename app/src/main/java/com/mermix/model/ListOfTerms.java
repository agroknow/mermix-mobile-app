package com.mermix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mermix.model.common.Pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 05/10/2015
 * Description:
 */
public class ListOfTerms extends Pojo implements Serializable{
	@JsonProperty("list")
	private List<PojoTerm> terms;

	public List<PojoTerm> getTerms() {
		return terms;
	}

	public void setTerms(List<PojoTerm> terms) {
		this.terms = terms;
	}
}
