package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.enumtypes.Currency;
import org.example.exception.BalanceNotFoundException;
import org.example.exception.ConstantErrorMessages;
import org.example.mapper.BalanceMapper;
import org.example.model.Balance;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceMapper balanceMapper;

    public List<Balance> createBalances(String accountId, List<String> currencyList) {
        return currencyList.stream().map(currency -> insertBalance(accountId, currency))
                .collect(Collectors.toList());
    }

    private Balance insertBalance(String accountId, String currency) {
        Balance balance = new Balance();
        balance.setId(UUID.randomUUID().toString());
        balance.setAccountId(accountId);
        balance.setCurrency(Currency.valueOf(currency));
        balance.setAvailableAmount(BigDecimal.ZERO);

        balanceMapper.insert(balance);

        return balance;
    }

    public List<Balance> retrieveBalancesByAccountId(String accountId) {
        return balanceMapper.findByAccountId(accountId);
    }

    public Balance retrieveBalanceByAccountIdAndCurrency(String accountId, String currency) {
        return balanceMapper.findByAccountIdAndCurrency(accountId, currency).orElseThrow(() -> new BalanceNotFoundException(ConstantErrorMessages.BALANCE_NOT_FOUND));
    }

    public void updateBalanceById(String balanceId, BigDecimal amount){
        balanceMapper.updateAmount(balanceId,amount);
    }
}
