package com.mermix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mermix.model.common.Address;
import com.mermix.model.common.Availability;
import com.mermix.model.common.Location;
import com.mermix.model.common.Node;
import com.mermix.model.common.Price;
import com.mermix.utils.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 02/08/2015
 * Description:
 * drupal content type equipment
 * to parse 'availability' use inner class Availability
 * more on nested classes @ http://stackoverflow.com/a/70358
 */
public class Equipment extends Node implements Serializable{
	@JsonProperty("field_multiprice")
	private List<Price> price;
	@JsonProperty("field_availability")
	private List<Availability> availability;
	@JsonProperty("field_image")
	private String image;
	//private List<Image> image;
	@JsonProperty("field_address")
	private List<Address> address;

	@JsonProperty("field_location")
	private Location location;

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public List<Price> getPrice() {
		return price;
	}

	public void setPrice(List<Price> price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Availability> getAvailability() {
		return availability;
	}

	public void setAvailability(List<Availability> availability) {	this.availability = availability;	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getMultiPriceString(){
		String priceStr = "";
		for(int i=0; i<this.price.size(); i++){
			priceStr += Constants.MULTIPRICEDELIMETER + this.price.get(i).getValue() + Constants.PRICEUNITDELIMETER + this.price.get(i).getUnit().getName();
		}
		if(!priceStr.isEmpty())
			priceStr = priceStr.substring(Constants.MULTIPRICEDELIMETER.length());
		return priceStr;
	}

	public String getMultiPriceString2Display(){
		String multiPrice = getMultiPriceString();
		return multiPrice.replace(Constants.MULTIPRICEDELIMETER, " , ");
	}

}
