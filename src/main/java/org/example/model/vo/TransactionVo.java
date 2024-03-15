package org.example.model.vo;

import lombok.*;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionVo implements Serializable {
    private String accountId;
    private String balanceId;
    private BigDecimal amount;
    private BigDecimal balanceAmount;
    private TransactionDirection direction;
    private Currency currency;
    private String description;
}
