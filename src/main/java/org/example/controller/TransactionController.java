package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionResponse;
import org.example.dto.response.GetTransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface TransactionController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a transaction",
            description = "This service creates a transaction and returns it"
    )
    CreateTransactionResponse createTransaction(CreateTransactionRequest request);

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Get account's transactions",
            description = "This service returns account's transaction list"
    )
    @ResponseStatus(HttpStatus.OK)
    List<GetTransactionResponse> getTransactions(String accountId);
}
