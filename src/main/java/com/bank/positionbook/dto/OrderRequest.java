package com.bank.positionbook.dto;

import com.bank.positionbook.constant.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {
    private Long id;
    private Long accountId;
    private Long securityId;
    private String orderType;
    private Integer amount;
}
