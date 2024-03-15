package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.AccountController;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountRabbitMQResponse;
import org.example.dto.response.AccountResponse;
import org.example.manager.AccountManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {
    private final AccountManager accountManager;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        AccountRabbitMQResponse accountRabbitMQResponse = accountManager.createAccount(request);

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setAccountId(accountRabbitMQResponse.getAccountId());
        accountResponse.setCustomerId(accountRabbitMQResponse.getCustomerId());
        accountResponse.setBalances(accountRabbitMQResponse.getBalances());

        return accountResponse;
    }

    @Override
    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public AccountResponse getAccount(@PathVariable String accountId) {
        return accountManager.getAccount(accountId);
    }
}
