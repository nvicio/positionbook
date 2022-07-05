package com.bank.positionbook.service;

import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;

import java.util.List;

public interface OrderService {
    /**
     * Return the order for given orderId
     * @param orderId
     * @return Order with given id
     */
    Order getOrderById(Long orderId);

    /**
     * List all orders (events) for given account
     * @param accountId Account id
     * @return List<Order>
     */
    List<Order> getOrdersFromAcc(Long accountId);

    /**
     * List all orders (events) from an account for a given security
     * @param accountId
     * @param securityId
     * @return List<Order>
     */
    List<Order> getOrdersFromAccAndSecurity(Long accountId, Long securityId);

    /**
     * Create new order record from order Request object
     * @param orderRequest OrderRequest
     * @return the saved order
     */
    Order addNewOrderFromOrderReq(OrderRequest orderRequest);

}
