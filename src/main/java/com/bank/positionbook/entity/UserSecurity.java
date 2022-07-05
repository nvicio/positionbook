package com.bank.positionbook.entity;

import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserSecurity {

    @EqualsAndHashCode.Include
    @NonNull
    private Long id;
    private String name;
    private String symbol;
    private AtomicInteger currentPosition;
    // Keep it simple, in reality there could be more CUSIP, ISIN, Issuers etc.

}
