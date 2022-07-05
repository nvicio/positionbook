package com.bank.positionbook.service;

import com.bank.positionbook.constant.EventType;
import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;
import com.bank.positionbook.entity.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PositionBookServiceImpl implements PositionBookService{

    private final Map<Long, Set<UserSecurity>> accountMap = new ConcurrentHashMap<>();
    private final OrderService orderService;

    public PositionBookServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Process the order (event) from the Order Request based on type SELL, BUY, CANCEL
     * @param orderRequest
     * @return the Order record that was successfully processed
     */
    @Override
    public Order processOrder(OrderRequest orderRequest) {
        final Long accountId = orderRequest.getAccountId();
        final Set<UserSecurity> userSecurities = accountMap.computeIfAbsent(accountId, k -> new HashSet<>());
        EventType orderType;
        try {
            orderType = EventType.valueOf(orderRequest.getOrderType());
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            // Bad request, order type not in (SELL, BUY, CANCEL)
            return null;
        }

        UserSecurity userSecurity = userSecurities.stream()
                .filter(o -> o.getId().equals(orderRequest.getSecurityId()))
                .findAny().orElse(null);
        if (userSecurity == null) {
            userSecurity = new UserSecurity(orderRequest.getSecurityId());
            userSecurity.setCurrentPosition(new AtomicInteger(0));
            userSecurities.add(userSecurity);
        }

        if (orderType.equals(EventType.BUY)){
            userSecurity.getCurrentPosition().getAndAdd(orderRequest.getAmount());
        } else if (orderType.equals(EventType.SELL)){
            userSecurity.getCurrentPosition().getAndAdd(-orderRequest.getAmount());
        } else if (orderType.equals(EventType.CANCEL)){
            Order tobeCancelled = orderService.getOrderById(orderRequest.getId());
            EventType toCancelledType = tobeCancelled.getType();
            if (tobeCancelled != null) {
                tobeCancelled.setCancelled(true);
                if (toCancelledType.equals(EventType.BUY)) {
                    userSecurity.getCurrentPosition().getAndAdd(-tobeCancelled.getAmount());
                } else if (toCancelledType.equals(EventType.SELL)){
                    userSecurity.getCurrentPosition().getAndAdd(tobeCancelled.getAmount());
                }
            }
        } else {
            // not supported event type; Bad request, orderType not in (SELL, BUY, CANCEL)
            return null;
        }
        return orderService.addNewOrderFromOrderReq(orderRequest);
    }

    /**
     * Get the UserSecurity object holding the position of given security for an account
     * @param accountId Account ID number
     * @param securityId Security ID number
     * @return the UserSecurity object holding the position of given security for an account
     */
    public UserSecurity getSecurityForUser(Long accountId, Long securityId){
        Set<UserSecurity> userSecurities = accountMap.get(accountId);
        if (userSecurities == null) {
            return null;
        } else {
            return userSecurities.stream()
                    .filter(o -> o.getId().equals(securityId))
                    .findAny().orElse(null);
        }

    }

    /**
     * Get all securities for an user (account)
     * @param accountId The account ID number
     * @return set of UserSecurity to hold all positions
     */
    public Set<UserSecurity> getAllSecuritiesForUser(Long accountId) {
        return accountMap.get(accountId);
    }

}
