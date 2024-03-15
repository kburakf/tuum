package org.example.controller;

import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountResponse;

public interface AccountController {
    AccountResponse createAccount(CreateAccountRequest request);
    AccountResponse getAccount(String accountId);
}
