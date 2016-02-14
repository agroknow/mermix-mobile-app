package com.mermix.model;

import com.mermix.model.common.Pojo;

import java.io.Serializable;

/**
 * Created on 07/10/2015
 * Description:
 */
public class PojoTerm extends Pojo implements Serializable{
	private int tid;
	private String name;
	private String vocabulary;

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
}
