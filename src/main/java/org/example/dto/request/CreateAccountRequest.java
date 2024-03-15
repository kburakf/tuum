package org.example.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.enumtypes.Currency;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.example.exception.ConstantErrorMessages;
import org.example.exception.InvalidCurrencyException;

@Getter
@Setter
public class CreateAccountRequest implements Serializable {
    private String customerId;
    private String country;
    private List<String> currencies;

    private void validateCurrency() {
        for (String currency : this.getCurrencies()) {
            if (!EnumUtils.isValidEnumIgnoreCase(Currency.class, currency)) {
                throw new InvalidCurrencyException(ConstantErrorMessages.INVALID_CURRENCY);
            }
        }
    }

    public void validate() {
        validateCurrency();
    }
}
