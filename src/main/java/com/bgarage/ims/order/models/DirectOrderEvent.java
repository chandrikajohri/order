package com.bgarage.ims.order.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DirectOrderEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private SuppliersEventBean supplier;
	private List<PartsEventBean> parts = new ArrayList<PartsEventBean>();

	public DirectOrderEvent() {
		super();
	}

	public DirectOrderEvent(SuppliersEventBean supplier, List<PartsEventBean> parts) {
		super();
		this.supplier = supplier;
		this.parts = parts;
	}

	public SuppliersEventBean getSupplier() {
		return supplier;
	}

	public List<PartsEventBean> getParts() {
		return parts;
	}

}
