package org.example.model;

import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionDirection direction;
    private String description;
    private Date transactionDate;
}
