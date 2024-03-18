package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountWithBalancesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface AccountController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create an account",
            description = "This service creates an account and returns it"
    )
    AccountWithBalancesResponse createAccount(CreateAccountRequest request);

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Get account with balances",
            description = "This service returns account with balances"
    )
    @ResponseStatus(HttpStatus.OK)
    AccountWithBalancesResponse getAccountWithBalances(String accountId);
}
