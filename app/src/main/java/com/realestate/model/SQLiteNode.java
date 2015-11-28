package com.realestate.model;

import com.realestate.model.common.Address;
import com.realestate.model.common.Availability;
import com.realestate.model.common.Body;
import com.realestate.model.common.Pojo;
import com.realestate.utils.Constants;

import java.util.Arrays;

/**
 * Created on 15/11/2015
 * Description:
 */
public class SQLiteNode extends Pojo {

	private int nid;
	private String title;
	private String body;
	private Double[] coordinates;
	private String images;		//comma delimeted image urls
	private Float price;

	public SQLiteNode(int nid, String title, String body, Double[] coordinates, String images, Float price) {
		this.nid = nid;
		this.title = title;
		this.body = body;
		this.coordinates = coordinates;
		this.images = images;
		this.price = price;
	}

	public int getNid() {
		return nid;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public Double[] getCoordinates() {
		return coordinates;
	}

	public String getImages() {
		return images;
	}

	public Float getPrice() {
		return price;
	}

	public String getImage(int idx){
		return images.split(Constants.CONCATDELIMETER)[idx];
	}

	public Equipment toEquipment() {
		Equipment equipment = new Equipment();

		Body body = new Body();
		body.setValue(this.getBody());
		Address address = new Address();
		Double[] coords = this.getCoordinates();
		address.setLatitude(coords[0]);
		address.setLongitude(coords[1]);
		Availability availability = new Availability();
		availability.setEnabled(1);

		equipment.setTitle(this.getTitle());
		equipment.setImage(this.getImage(0));
		equipment.setNid(this.getNid());
		equipment.setBody(Arrays.asList(body));
		equipment.setPrice(-1);
		equipment.setAddress(Arrays.asList(address));
		equipment.setAvailability(Arrays.asList(availability));

		return equipment;
	}

}
