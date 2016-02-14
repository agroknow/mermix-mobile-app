package com.mermix.model.common;

import java.io.Serializable;

/**
 * Created on 06/08/2015
 * Description:
 * field body of drupal entity 'node'
 */
public class Body implements Serializable{
	private String format;
	private String summary;
	private String value;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}