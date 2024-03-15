package org.example.controller;

import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionResponse;
import org.example.dto.response.GetTransactionResponse;

import java.util.List;

public interface TransactionController {
    CreateTransactionResponse createTransaction(CreateTransactionRequest request);
    List<GetTransactionResponse> getTransactions(String accountId);
}
