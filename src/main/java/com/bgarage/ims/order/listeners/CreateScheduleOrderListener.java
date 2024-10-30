package com.bgarage.ims.order.listeners;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bgarage.ims.order.models.PartsDTOList;
import com.bgarage.ims.order.models.PartsEventBean;
import com.bgarage.ims.order.models.ScheduleOrderEvent;
import com.bgarage.ims.order.models.SuppliersEventBean;
import com.bgarage.ims.order.service.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CreateScheduleOrderListener {

	@Autowired
	OrdersService ordersService;

	@KafkaListener(topics = "${bgarage.kafka.topic.create-schedule-order}", groupId = "${create.schedule.order.consumer.group-id}")
	public void listen(String message) {
		System.out.println("Received message inside Schedule Order Listener");

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ScheduleOrderEvent scheduleOrderEvent = objectMapper.readValue(message, ScheduleOrderEvent.class);

			List<Long> partIds = scheduleOrderEvent.getParts().stream().map(PartsEventBean::getPartId)
					.collect(Collectors.toList());
			Long supplierId = scheduleOrderEvent.getSupplier().getSupplierId();

			System.out.println("Fetch all parts from inventory service");
			PartsDTOList partsDetails = ordersService.getPartsDetails(partIds);

			System.out.println("Fetch supplier from inventory service");
			SuppliersEventBean supplierDetails = ordersService.getSupplierDetails(supplierId);

			System.out.println("Schedule orders");
			ordersService.scheduleOrders(supplierDetails, partsDetails.getPartsResponseBeans());

		} catch (Exception e) {
			System.err.println("Error processing message: " + e.getMessage());
			e.printStackTrace();
		}
	}

}