package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.TransactionController;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.dto.response.CreateTransactionResponse;
import org.example.dto.response.GetTransactionResponse;
import org.example.manager.AccountTransactionManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {
    private final AccountTransactionManager accountTransactionManager;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTransactionResponse createTransaction(@RequestBody CreateTransactionRequest request) {
        CreateTransactionRabbitMQResponse transactionRabbitMQResponse = accountTransactionManager.createTransaction(request);

        CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();

        createTransactionResponse.setTransactionId(transactionRabbitMQResponse.getTransactionId());
        createTransactionResponse.setAccountId(transactionRabbitMQResponse.getAccountId());
        createTransactionResponse.setAmount(transactionRabbitMQResponse.getAmount());
        createTransactionResponse.setCurrency(transactionRabbitMQResponse.getCurrency());
        createTransactionResponse.setDescription(transactionRabbitMQResponse.getDescription());
        createTransactionResponse.setDirection(transactionRabbitMQResponse.getDirection());

        return createTransactionResponse;
    }

    @Override
    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GetTransactionResponse> getTransactions(@PathVariable String accountId) {
        return accountTransactionManager.getTransactions(accountId);
    }
}
