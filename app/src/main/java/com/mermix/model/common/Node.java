package com.mermix.model.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 02/08/2015
 * Description:
 * drupal entity 'node'
 */
public class Node extends Pojo implements Serializable{
	private String title;
	private List<Body> body;
	private String type;
	private String url;
	private int nid;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Body> getBody() {
		return body;
	}

	public void setBody(List<Body> body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

}
