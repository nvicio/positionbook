package com.bank.positionbook.controller;

import com.bank.positionbook.dto.OrderRequest;
import com.bank.positionbook.entity.Order;
import com.bank.positionbook.entity.UserSecurity;
import com.bank.positionbook.service.OrderService;
import com.bank.positionbook.service.PositionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/positionbook")
public class PositionBookController {

    private final PositionBookService positionBookService;
    private final OrderService orderService;

    public PositionBookController(PositionBookService positionBookService, OrderService orderService) {
        this.positionBookService = positionBookService;
        this.orderService = orderService;
    }

    @PostMapping("/event")
    public ResponseEntity<Order> processEvent(@RequestBody OrderRequest orderRequest) {
        final Order ret = positionBookService.processOrder(orderRequest);
        if (ret == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/position/{accountId}")
    public ResponseEntity<List<UserSecurity>> getSecuritiesForAcc(@PathVariable Long accountId, @RequestParam(required = false) Long securityId){
        if (securityId == null) {
            Set<UserSecurity> userSecurities = positionBookService.getAllSecuritiesForUser(accountId);
            if (userSecurities != null) {
                return new ResponseEntity<>(new ArrayList<>(userSecurities), HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(Collections.singletonList(positionBookService.getSecurityForUser(accountId, securityId)), HttpStatus.OK);
        }
    }

    @GetMapping("/order/{accountId}")
    public ResponseEntity<List<Order>> getOrderForAcc(@PathVariable Long accountId, @RequestParam(required = false) Long securityId){
        if (securityId == null) {
            final List<Order> orders = orderService.getOrdersFromAcc(accountId);
            if (orders != null) {
                return new ResponseEntity<>(orders, HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(orderService.getOrdersFromAccAndSecurity(accountId, securityId), HttpStatus.OK);
        }
    }

}
