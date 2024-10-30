package com.bgarage.ims.order.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PartsEventBeanList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Long> partsEventBeanList = new ArrayList<Long>();

	public PartsEventBeanList() {
	}

	public List<Long> getPartsEventBeanList() {
		return partsEventBeanList;
	}

	public void setPartsEventBeanList(List<Long> partsEventBeanList) {
		this.partsEventBeanList = partsEventBeanList;
	}

}
