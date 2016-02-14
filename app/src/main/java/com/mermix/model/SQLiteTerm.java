package com.mermix.model;

/**
 * Created on 22/09/2015
 * Description:
 */
public class SQLiteTerm {
	private int tid;
	private String name;
	private String vocabulary;

	public SQLiteTerm(int tid, String name, String vocabulary) {
		this.tid = tid;
		this.name = name;
		this.vocabulary = vocabulary;
	}

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
