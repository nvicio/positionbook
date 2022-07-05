package com.bank.positionbook.service;

import com.bank.positionbook.constant.EventType;
import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService{

    private static final AtomicLong orderIdCount = new AtomicLong(0);
    // CopyOnWriteArrayList has the disadvantage of being very expensive on write (but cheap for reads)
    private static final Queue<Order> orders = new ConcurrentLinkedQueue<>();

    /**
     * Return the order for given orderId
     * @param orderId
     * @return Order with given id
     */
    @Override
    public Order getOrderById(Long orderId) {
        return orders.stream()
                .filter(o -> Objects.equals(o.getId(), orderId))
                .findFirst().orElse(null);
    }

    /**
     * List all orders (events) for given account
     * @param accountId Account id
     * @return List<Order>
     */
    public List<Order> getOrdersFromAcc(Long accountId) {
        return orders.stream()
                .filter(o -> Objects.equals(o.getAccountId(), accountId))
                .collect(Collectors.toList());
    }

    /**
     * List all orders (events) from an account for a given security
     * @param accountId
     * @param securityId
     * @return List<Order>
     */
    public List<Order> getOrdersFromAccAndSecurity(Long accountId, Long securityId) {
        return orders.stream()
                .filter(o -> Objects.equals(o.getAccountId(), accountId) && Objects.equals(o.getSecurityId(), securityId))
                .collect(Collectors.toList());
    }

    /**
     * Create new order record from order Request object
     * @param orderRequest OrderRequest
     * @return the saved order
     */
    public Order addNewOrderFromOrderReq(OrderRequest orderRequest){
        final Long orderId = orderIdCount.incrementAndGet();
        final EventType orderType = EventType.valueOf(orderRequest.getOrderType());
        final Order order = Order.builder()
                .id(orderId)
                .accountId(orderRequest.getAccountId())
                .date(LocalDateTime.now())
                .securityId(orderRequest.getSecurityId())
                .type(orderType)
                .amount(orderRequest.getAmount())
                .build();
        orders.add(order);
        return order;
    }
}
