package com.bgarage.ims.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bgarage.ims.order.entity.OrderItems;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {

}
