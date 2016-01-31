package com.realestate.model;

import com.realestate.model.common.Address;
import com.realestate.model.common.Availability;
import com.realestate.model.common.Body;
import com.realestate.model.common.Pojo;
import com.realestate.model.common.Price;
import com.realestate.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private String multiprice;

	public SQLiteNode(int nid, String title, String body, Double[] coordinates, String images,String multiprice) {
		this.nid = nid;
		this.title = title;
		this.body = body;
		this.coordinates = coordinates;
		this.images = images;
		this.multiprice = multiprice;
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

	public String getMultiprice() {
		return multiprice;
	}

	public List<Price> getPriceObject(){
		List<Price> priceObj = new ArrayList<>();
		Price price;
		Price.PriceUnit priceUnit;
		if(!this.multiprice.isEmpty()) {
			String[] multiPriceUnits = this.multiprice.split(Constants.MULTIPRICEDELIMETER);
			for (int i = 0; i < multiPriceUnits.length; i++) {
				String[] multiPriceUnitValue = multiPriceUnits[i].split(Constants.PRICEUNITDELIMETER);
				price = new Price();
				priceUnit = price.new PriceUnit();
				priceUnit.setName(multiPriceUnitValue[1]);
				price.setUnit(priceUnit);
				price.setValue(multiPriceUnitValue[0]);
				priceObj.add(price);
			}
		}
		return priceObj;
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
		equipment.setPrice(this.getPriceObject());
		equipment.setAddress(Arrays.asList(address));
		equipment.setAvailability(Arrays.asList(availability));

		return equipment;
	}

}
