package com.mermix.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created on 08/09/2015
 * Description:
 */
public class Address implements Serializable{
	@JsonProperty("lon")
	private double longitude;
	@JsonProperty("lat")
	private double latitude;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
