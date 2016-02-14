package com.mermix.model.payload;

/**
 * Created on 01/02/2016
 * Description:
 * class to submit multifield 'field_multiprice'
 * json payload:
 * "field_multiprice":[{"field_multiprice_value":{"und":[{"value":"20.00"}]},"field_multiprice_unit":{"und":[{"tid":"219"}]}}]
 *
 */

import com.mermix.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MultiPricePayload{
	private PriceValueUnd field_multiprice_value;
	private PriceUnitUnd field_multiprice_unit;

	public PriceValueUnd getField_multiprice_value() {
		return field_multiprice_value;
	}

	public void setField_multiprice_value(PriceValueUnd field_multiprice_value) {
		this.field_multiprice_value = field_multiprice_value;
	}

	public PriceUnitUnd getField_multiprice_unit() {
		return field_multiprice_unit;
	}

	public void setField_multiprice_unit(PriceUnitUnd field_multiprice_unit) {
		this.field_multiprice_unit = field_multiprice_unit;
	}

	public void setData(String value, String tid){
		PriceValue priceValue = new PriceValue();
		PriceValueUnd priceValueUnd = new PriceValueUnd();
		List<PriceValue> priceValueList = new ArrayList<>();
		priceValue.setValue(value);
		priceValueList.add(priceValue);
		priceValueUnd.setUnd(priceValueList);
		this.setField_multiprice_value(priceValueUnd);
		if(!tid.equals(Integer.toString(Constants.SPINNERITEMS.EMPTYTERM.TID))) {
			PriceUnit priceUnit = new PriceUnit();
			PriceUnitUnd priceUnitUnd = new PriceUnitUnd();
			List<PriceUnit> priceUnitList = new ArrayList<>();
			priceUnit.setTid(tid);
			priceUnitList.add(priceUnit);
			priceUnitUnd.setUnd(priceUnitList);
			this.setField_multiprice_unit(priceUnitUnd);
		}
	}
}

