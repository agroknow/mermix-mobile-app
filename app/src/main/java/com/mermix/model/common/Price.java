package com.mermix.model.common;

import java.io.Serializable;

/**
 * Created on 30/01/2016
 * Description:
 */
public class Price implements Serializable {
	private String value;
	private PriceUnit unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PriceUnit getUnit() {
		return unit;
	}

	public void setUnit(PriceUnit unit) {
		this.unit = unit;
	}

	public class PriceUnit implements Serializable {
		private String tid;
		private String name;

		public String getTid() {
			return tid;
		}

		public void setTid(String tid) {
			this.tid = tid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
