package org.example.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;
import org.example.exception.*;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionRequest {
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String direction;
    private String description;

    public void validateCurrency() {
        if (!EnumUtils.isValidEnumIgnoreCase(Currency.class, this.getCurrency())) {
            throw new InvalidCurrencyException(ConstantErrorMessages.INVALID_CURRENCY);
        }
    }

    public void validateTransactionDirection() {
        if (direction == null || !EnumUtils.isValidEnum(TransactionDirection.class, this.getDirection())) {
            throw new InvalidTransactionDirectionException(ConstantErrorMessages.INVALID_DIRECTION);
        }
    }

    public void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(ConstantErrorMessages.INVALID_AMOUNT);
        }
    }

    public void validateAccountId() {
        if (StringUtils.isBlank(this.accountId)) {
            throw new AccountMissingException(ConstantErrorMessages.ACCOUNT_MISSING);
        }
    }

    public void validateDescription() {
        if (StringUtils.isBlank(this.description)) {
            throw new DescriptionMissingException(ConstantErrorMessages.DESCRIPTION_MISSING);
        }
    }


    public void validate() {
        validateCurrency();
        validateTransactionDirection();
        validateAmount();
        validateAccountId();
        validateDescription();
    }
}
