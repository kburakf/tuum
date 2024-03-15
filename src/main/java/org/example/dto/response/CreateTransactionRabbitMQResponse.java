package org.example.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionRabbitMQResponse extends BaseResponse {
    private String accountId;
    private String transactionId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionDirection direction;
    private String description;
    private BigDecimal balanceAfterTransaction;
}
