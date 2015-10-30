package com.realestate.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created on 08/09/2015
 * Description:
 */
public class Address implements Serializable{
	@JsonProperty("lon")
	private float longitude;
	@JsonProperty("lat")
	private float latitude;

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
}
