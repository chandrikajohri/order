package com.bgarage.ims.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bgarage.ims.order.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

	List<Orders> findByOrderStatus(String orderStatus);


}
