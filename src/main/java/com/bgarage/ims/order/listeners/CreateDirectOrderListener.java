package com.bgarage.ims.order.listeners;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.bgarage.ims.order.entity.Orders;
import com.bgarage.ims.order.models.DirectOrderEvent;
import com.bgarage.ims.order.models.PartsDTOList;
import com.bgarage.ims.order.models.PartsEventBean;
import com.bgarage.ims.order.models.SuppliersEventBean;
import com.bgarage.ims.order.service.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CreateDirectOrderListener {

	@Autowired
	OrdersService ordersService;

	@KafkaListener(topics = "${bgarage.kafka.topic.create-direct-order}", groupId = "${create.direct.order.consumer.group-id}")
	public void listen(String message) {
		System.out.println("Received message inside Create Order Listener");

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			DirectOrderEvent directOrderEvent = objectMapper.readValue(message, DirectOrderEvent.class);

			List<Long> partIds = directOrderEvent.getParts().stream().map(PartsEventBean::getPartId)
					.collect(Collectors.toList());
			Long supplierId = directOrderEvent.getSupplier().getSupplierId();

			System.out.println("Fetch all parts from inventory service");
			PartsDTOList partsDetails = ordersService.getPartsDetails(partIds);

			System.out.println("Fetch supplier from inventory service");
			SuppliersEventBean supplierDetails = ordersService.getSupplierDetails(supplierId);

			System.out.println("Create orders");
			Orders order = ordersService.initiateOrders(supplierDetails, partsDetails.getPartsResponseBeans());

			System.out.println("Call to external API of supplier to place order");
			ordersService.placeOrderWithSupplierAndUpdateOrderStatus(order, partsDetails.getPartsResponseBeans(),
					supplierDetails);

		} catch (Exception e) {
			System.err.println("Error processing message: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
