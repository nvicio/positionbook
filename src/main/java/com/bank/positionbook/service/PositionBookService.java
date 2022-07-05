package com.bank.positionbook.service;

import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;
import com.bank.positionbook.entity.UserSecurity;

import java.util.Set;

public interface PositionBookService {

    /**
     * Process the order (event) from the Order Request based on type SELL, BUY, CANCEL
     * @param orderRequest
     * @return the Order record that was successfully processed
     */
    Order processOrder(OrderRequest orderRequest);

    /**
     * Get all securities for an user (account)
     * @param accountId The account ID number
     * @return set of UserSecurity to hold all positions
     */
    Set<UserSecurity> getAllSecuritiesForUser(Long accountId);

    /**
     * Get the UserSecurity object holding the position of given security for an account
     * @param accountId Account ID number
     * @param securityId Security ID number
     * @return the UserSecurity object holding the position of given security for an account
     */
    UserSecurity getSecurityForUser(Long accountId, Long securityId);
}
