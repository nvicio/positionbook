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

/**
 * Test processing the orders (events)
 */
@SpringBootTest
public class PositionBookServiceTests {

    @Autowired
    PositionBookService positionBookService;

    @Autowired
    OrderService orderService;

    @Test
    public void whenBuyNewSecurity_thenAddPosition(){
        final Long accountId = 1234L;
        final Long securityId = 111L;
        final OrderRequest orderRequest =  OrderRequest.builder()
                .orderType("BUY")
                .accountId(accountId)
                .securityId(securityId)
                .amount(50)
                .build();
        positionBookService.processOrder(orderRequest);
        final UserSecurity userSecurity = positionBookService.getSecurityForUser(accountId, securityId);
        Assertions.assertEquals(50, userSecurity.getCurrentPosition().get());
    }

    @Test
    public void givenSecurity_whenSell_thenDecreaseAmount(){
        final Long accountId = 100L;
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
        final UserSecurity userSecurity = positionBookService.getSecurityForUser(accountId, securityId);
        Assertions.assertEquals(40, userSecurity.getCurrentPosition().get());
    }

    @Test
    public void givenSecurity_whenSellThenCancel_thenAmountUnchanged(){
        final Long accountId = 100L;
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
        final UserSecurity userSecurity = positionBookService.getSecurityForUser(accountId, securityId);
        Assertions.assertEquals(50, userSecurity.getCurrentPosition().get());
    }

    @Test
    public void givenSecurity_whenBuyThenCancelNotLast_thenAmountUnchanged(){
        final Long accountId = 100L;
        final Long securityId = 113L;
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
        // Cancel buy request, not last order -> nothing happens
        final OrderRequest cancelRequest =  OrderRequest.builder()
                .orderType("CANCEL")
                .id(buyOrder.getId())
                .accountId(accountId)
                .securityId(securityId)
                .build();

        positionBookService.processOrder(cancelRequest);
        final UserSecurity userSecurity = positionBookService.getSecurityForUser(accountId, securityId);
        Assertions.assertEquals(40, userSecurity.getCurrentPosition().get());
    }

    @Test
    public void givenSecurity_whenBuySellThenCancelLastOrder_thenAmountChanged(){
        final Long accountId = 105L;
        final Long securityId = 113L;
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
        // Cancel sell request, last order -> successfully canceled
        final OrderRequest cancelRequest =  OrderRequest.builder()
                .orderType("CANCEL")
                .id(sellOrder.getId())
                .accountId(accountId)
                .securityId(securityId)
                .build();

        positionBookService.processOrder(cancelRequest);
        final UserSecurity userSecurity = positionBookService.getSecurityForUser(accountId, securityId);
        Assertions.assertEquals(50, userSecurity.getCurrentPosition().get());
    }


}
