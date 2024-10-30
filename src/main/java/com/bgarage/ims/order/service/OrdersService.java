package com.bgarage.ims.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bgarage.ims.order.entity.OrderItems;
import com.bgarage.ims.order.entity.Orders;
import com.bgarage.ims.order.exception.ServiceException;
import com.bgarage.ims.order.models.OrderStatus;
import com.bgarage.ims.order.models.PartsDTO;
import com.bgarage.ims.order.models.PartsDTOList;
import com.bgarage.ims.order.models.PartsEventBeanList;
import com.bgarage.ims.order.models.SuppliersEventBean;
import com.bgarage.ims.order.repository.OrderItemsRepository;
import com.bgarage.ims.order.repository.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrdersService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	OrderItemsRepository orderItemsRepository;

	@Value("${investor.api.parts.url}")
	private String partURL;

	@Value("${investor.api.supplier.url}")
	private String supplierURL;

	public PartsDTOList getPartsDetails(List<Long> partIds) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		PartsEventBeanList partsEventBeanList = new PartsEventBeanList();
		partsEventBeanList.setPartsEventBeanList(partIds);

		HttpEntity<PartsEventBeanList> request = new HttpEntity<>(partsEventBeanList, headers);

		ResponseEntity<PartsDTOList> response = restTemplate.exchange(partURL, HttpMethod.POST, request,
				PartsDTOList.class);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			;
			System.out.println(objectMapper.writeValueAsString(response.getBody()));

		} catch (Exception e) {
			throw new ServiceException("getPartsDetails", "Error while fetching parts");
		}

		return response.getBody();
	}

	public SuppliersEventBean getSupplierDetails(Long supplierId) {
		return restTemplate.getForObject(supplierURL, SuppliersEventBean.class);
	}

	public void placeOrderWithSupplierAndUpdateOrderStatus(Orders order, List<PartsDTO> partsDetails,
			SuppliersEventBean supplierDetails) {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("orderId", order.getOrderId());
		requestBody.put("supplierId", supplierDetails.getSupplierId());
		requestBody.put("parts", partsDetails);

		String supplierApiUrl = "http://supplier-api.com/api/orders"; // Dummy API URL
		restTemplate.postForObject(supplierApiUrl, requestBody, Void.class);

		order.setOrderStatus(OrderStatus.PLACED.toString());
		ordersRepository.save(order);
		System.out.println("order placed at supplier");

	}

	public Orders initiateOrders(SuppliersEventBean supplierDetails, List<PartsDTO> partsDetails) {

		Orders order = new Orders();

		BigDecimal subtotal = BigDecimal.ZERO;

		order.setSupplierId(supplierDetails.getSupplierId());
		order.setOrderStatus(OrderStatus.INITIATED.toString());
		ordersRepository.save(order);

		for (PartsDTO part : partsDetails) {
			OrderItems orderItem = new OrderItems();
			orderItem.setOrder(order);
			orderItem.setPartId(part.getPartId());
			orderItem.setQuantity(part.getMinimumOrder());
			orderItem.setCostPrice(part.getPrice());
			subtotal = subtotal.add(part.getPrice());
			orderItemsRepository.save(orderItem);
		}
		order.setSubTotal(subtotal);
		ordersRepository.save(order);
		return order;
	}

	public void scheduleOrders(SuppliersEventBean supplierDetails, List<PartsDTO> partsDetails) {
		Orders order = new Orders();

		BigDecimal subtotal = BigDecimal.ZERO;

		order.setSupplierId(supplierDetails.getSupplierId());
		order.setOrderStatus(OrderStatus.SCHEDULED.toString());
		ordersRepository.save(order);

		for (PartsDTO part : partsDetails) {
			OrderItems orderItem = new OrderItems();
			orderItem.setOrder(order);
			orderItem.setPartId(part.getPartId());
			orderItem.setQuantity(part.getMinimumOrder());
			orderItem.setCostPrice(part.getPrice());
			subtotal = subtotal.add(part.getPrice());
			orderItemsRepository.save(orderItem);
		}
		order.setSubTotal(subtotal);
		ordersRepository.save(order);
	}

	public void processScheduledOrders() {

		List<Orders> scheduledOrderList = ordersRepository.findByOrderStatus(OrderStatus.SCHEDULED.toString());
		for (Orders order : scheduledOrderList) {

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("orderId", order.getOrderId());
			requestBody.put("supplierId", order.getSupplierId());
			requestBody.put("parts", order.getOrderItems());

			String supplierApiUrl = "http://supplier-api.com/api/orders"; // Dummy API URL
			restTemplate.postForObject(supplierApiUrl, requestBody, Void.class);

			order.setOrderStatus(OrderStatus.PLACED.toString());
			ordersRepository.save(order);
		}

	}
}
