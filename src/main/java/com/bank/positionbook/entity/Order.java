package com.bank.positionbook.entity;

import com.bank.positionbook.constant.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Long id;
    private EventType type;
    private Long accountId;
    private Long securityId;
    private Integer amount;
    private boolean isCancelled;
    private LocalDateTime date;
}
