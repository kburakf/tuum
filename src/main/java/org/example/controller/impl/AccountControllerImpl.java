package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.AccountController;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.CreateAccountRabbitMQResponse;
import org.example.dto.response.AccountWithBalancesResponse;
import org.example.manager.AccountManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {
    private final AccountManager accountManager;

    @Override
    public AccountWithBalancesResponse createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountRabbitMQResponse createAccountRabbitMQResponse = accountManager.createAccount(request);

        AccountWithBalancesResponse accountWithBalancesResponse = new AccountWithBalancesResponse();

        accountWithBalancesResponse.setAccountId(createAccountRabbitMQResponse.getAccountId());
        accountWithBalancesResponse.setCustomerId(createAccountRabbitMQResponse.getCustomerId());
        accountWithBalancesResponse.setBalances(createAccountRabbitMQResponse.getBalances());

        return accountWithBalancesResponse;
    }

    @Override
    public AccountWithBalancesResponse getAccountWithBalances(@PathVariable String accountId) {
        return accountManager.getAccountWithBalances(accountId);
    }
}
