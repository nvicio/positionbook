package com.bank.positionbook;

import com.bank.positionbook.constant.EventType;
import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;
import com.bank.positionbook.entity.UserSecurity;
import com.bank.positionbook.service.OrderService;
import com.bank.positionbook.service.PositionBookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Test the Order Services
 */
@SpringBootTest
public class OrderServiceTests {

    @Autowired
    PositionBookService positionBookService;

    @Autowired
    OrderService orderService;

    @Test
    public void whenBuyNewSecurity_thenOrderIsAdded(){
        final Long accountId = 11L;
        final Long securityId = 111L;
        final OrderRequest orderRequest =  OrderRequest.builder()
                .orderType("BUY")
                .accountId(accountId)
                .securityId(securityId)
                .amount(50)
                .build();
        positionBookService.processOrder(orderRequest);
        final List<Order> orders = orderService.getOrdersFromAccAndSecurity(accountId, securityId);
        Assertions.assertEquals(1, orders.size());
    }

    @Test
    public void givenSecurity_whenSell_thenOrderIsAdded(){
        final Long accountId = 12L;
        final Long securityId = 111L;
        final OrderRequest buyRequest =  OrderRequest.builder()
                .orderType("BUY")
                .accountId(accountId)
                .securityId(securityId)
                .amount(50)
                .build();
        positionBookService.processOrder(buyRequest);
        final OrderRequest sellRequest =  OrderRequest.builder()
                .orderType("SELL")
                .accountId(accountId)
                .securityId(securityId)
                .amount(10)
                .build();

        positionBookService.processOrder(sellRequest);
        final List<Order> orders = orderService.getOrdersFromAcc(accountId);
        Assertions.assertEquals(2, orders.size());
        Assertions.assertEquals(EventType.SELL, orders.get(1).getType());
    }

    @Test
    public void givenSecurity_whenSellThenCancel_thenUpdateCanceledFlag(){
        final Long accountId = 13L;
        final Long securityId = 112L;
        final OrderRequest buyRequest =  OrderRequest.builder()
                .orderType("BUY")
                .accountId(accountId)
                .securityId(securityId)
                .amount(50)
                .build();
        final Order buyOrder = positionBookService.processOrder(buyRequest);
        final OrderRequest sellRequest =  OrderRequest.builder()
                .orderType("SELL")
                .accountId(accountId)
                .securityId(securityId)
                .amount(10)
                .build();

        final Order sellOrder = positionBookService.processOrder(sellRequest);
        // Cancel sell request
        final OrderRequest cancelRequest =  OrderRequest.builder()
                .orderType("CANCEL")
                .id(sellOrder.getId())
                .accountId(accountId)
                .securityId(securityId)
                .build();

        positionBookService.processOrder(cancelRequest);
        final List<Order> orders = orderService.getOrdersFromAccAndSecurity(accountId, securityId);
        Assertions.assertEquals(EventType.SELL,
                orders.stream()
                        .filter(o -> o.getId().equals(sellOrder.getId()))
                        .findFirst().orElse(null)
                        .getType());
        Assertions.assertTrue(orders.stream()
                .filter(o -> o.getId().equals(sellOrder.getId()))
                .findFirst().orElse(null)
                .isCancelled());
    }

}
