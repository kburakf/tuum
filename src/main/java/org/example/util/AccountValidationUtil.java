package org.example.util;

import org.apache.commons.lang3.EnumUtils;
import org.example.dto.request.CreateAccountRequest;
import org.example.enumtypes.Currency;
import org.example.exception.ConstantErrorMessages;
import org.example.exception.InvalidCurrencyException;

import java.util.List;

public class AccountValidationUtil {

    public static void validateAccountRequest(CreateAccountRequest request) {
        validateCurrencies(request.getCurrencies());
    }

    private static void validateCurrencies(List<String> currencies) {
        if (currencies != null && !currencies.isEmpty()) {
            for (String currency : currencies) {
                if (!EnumUtils.isValidEnumIgnoreCase(Currency.class, currency)) {
                    throw new InvalidCurrencyException(ConstantErrorMessages.INVALID_CURRENCY);
                }
            }
        }
    }
}

