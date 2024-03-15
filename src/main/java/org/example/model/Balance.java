package org.example.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enumtypes.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private String id;
    private String accountId;
    private BigDecimal availableAmount;
    private Currency currency;
    private Date createTimestamp;
    private Date updateTimestamp;
}
