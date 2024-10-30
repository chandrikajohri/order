package com.bgarage.ims.order.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bgarage.ims.order.exception.ServiceException;
import com.bgarage.ims.order.models.ProcessScheduledOrderEvent;
import com.bgarage.ims.order.service.OrdersService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProcessScheduledOrderListener {

	@Autowired
	OrdersService ordersService;

	@KafkaListener(topics = "${bgarage.kafka.topic.process-schedule-order}", groupId = "${process.schedule.order.consumer.group-id}")
	public void listen(String message) {
		System.out.println("Received message inside Schedule Order Listener");

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ProcessScheduledOrderEvent scheduleOrderEvent = objectMapper.readValue(message,
					ProcessScheduledOrderEvent.class);

			ordersService.processScheduledOrders();

		} catch (Exception e) {
			throw new ServiceException("ProcessScheduledOrderListener", "Error while publishing process schedule order event");

		}
	}

}