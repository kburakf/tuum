package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.TransactionController;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.dto.response.CreateTransactionResponse;
import org.example.dto.response.GetTransactionResponse;
import org.example.manager.AccountTransactionManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionControllerImpl implements TransactionController {
    private final AccountTransactionManager accountTransactionManager;

    @Override
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
    public List<GetTransactionResponse> getTransactions(@PathVariable @NotBlank(message = "Account ID cannot be blank") String accountId) {
        return accountTransactionManager.getTransactions(accountId);
    }
}
