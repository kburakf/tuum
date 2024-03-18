package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.CreateAccountRabbitMQResponse;
import org.example.dto.response.AccountWithBalancesResponse;
import org.example.dto.response.BalanceResponse;
import org.example.exception.AccountNotFoundException;
import org.example.exception.ConstantErrorMessages;
import org.example.mapper.AccountMapper;
import org.example.model.Account;
import org.example.model.Balance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final BalanceService balanceService;

    @Transactional
    public CreateAccountRabbitMQResponse createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setCustomerId(request.getCustomerId());
        account.setCountry(request.getCountry());
        accountMapper.insertAccount(account);

        CreateAccountRabbitMQResponse response = new CreateAccountRabbitMQResponse();
        response.setAccountId(account.getId());
        response.setCustomerId(account.getCustomerId());

        List<Balance> createdBalances = balanceService.createBalances(account.getId(), request.getCurrencies());

        createdBalances.forEach(createdBalance -> {
            BalanceResponse balanceResponse = new BalanceResponse();
            balanceResponse.setCurrency(createdBalance.getCurrency());
            balanceResponse.setAvailableAmount(createdBalance.getAvailableAmount());
            response.getBalances().add(balanceResponse);
        });

        return response;
    }

    public AccountWithBalancesResponse getAccountWithBalances(String accountId) {
        Account account = accountMapper.findById(accountId).orElseThrow(() -> new AccountNotFoundException(ConstantErrorMessages.ACCOUNT_NOT_FOUND));

        List<Balance> balances = balanceService.retrieveBalancesByAccountId(accountId);

        return mapToAccountAndBalancesResponse(account, balances);
    }

    public void getAccount(String accountId) {
        accountMapper.findById(accountId).orElseThrow(() -> new AccountNotFoundException(ConstantErrorMessages.ACCOUNT_NOT_FOUND));
    }

    private AccountWithBalancesResponse mapToAccountAndBalancesResponse(Account account, List<Balance> balances) {
        AccountWithBalancesResponse response = new AccountWithBalancesResponse();
        response.setAccountId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setBalances(balances.stream().map(balance -> {
            BalanceResponse balanceResponse = new BalanceResponse();
            balanceResponse.setCurrency(balance.getCurrency());
            balanceResponse.setAvailableAmount(balance.getAvailableAmount());
            return balanceResponse;
        }).collect(Collectors.toList()));

        return response;
    }
}
