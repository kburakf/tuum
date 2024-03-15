package org.example.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.enumtypes.Currency;

import java.math.BigDecimal;

@Getter
@Setter
public  class BalanceResponse {
    private Currency currency;
    private BigDecimal availableAmount;
}