package org.example.dto.request;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest implements Serializable {
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String direction;
    private String description;
}
