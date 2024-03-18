package org.example.util;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.dto.request.CreateTransactionRequest;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;
import org.example.exception.*;

import java.math.BigDecimal;

public class TransactionValidationUtil {

    public static void validateTransactionRequest(CreateTransactionRequest request) {
        validateCurrency(request.getCurrency());
        validateTransactionDirection(request.getDirection());
        validateAmount(request.getAmount());
        validateAccountId(request.getAccountId());
        validateDescription(request.getDescription());
    }

    private static void validateCurrency(String currency) {
        if (!EnumUtils.isValidEnumIgnoreCase(Currency.class, currency)) {
            throw new InvalidCurrencyException(ConstantErrorMessages.INVALID_CURRENCY);
        }
    }

    private static void validateTransactionDirection(String direction) {
        if (!EnumUtils.isValidEnum(TransactionDirection.class, direction)) {
            throw new InvalidTransactionDirectionException(ConstantErrorMessages.INVALID_DIRECTION);
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(ConstantErrorMessages.INVALID_AMOUNT);
        }
    }

    private static void validateAccountId(String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new AccountMissingException(ConstantErrorMessages.ACCOUNT_MISSING);
        }
    }

    private static void validateDescription(String description) {
        if (StringUtils.isBlank(description)) {
            throw new DescriptionMissingException(ConstantErrorMessages.DESCRIPTION_MISSING);
        }
    }
}
