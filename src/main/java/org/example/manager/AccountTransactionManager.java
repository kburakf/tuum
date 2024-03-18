package org.example.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.dto.response.GetTransactionResponse;
import org.example.messaging.publisher.MessagePublisher;
import org.example.properties.RabbitMQProperties;
import org.example.service.TransactionService;
import org.example.util.TransactionValidationUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionManager {
    private final MessagePublisher messagePublisher;
    private final TransactionService transactionService;

    public CreateTransactionRabbitMQResponse createTransaction(CreateTransactionRequest request) {
        TransactionValidationUtil.validateTransactionRequest(request);

        transactionService.preCreateTransactionCheck(request);

        CreateTransactionRequest createTransactionRequest = transactionService.prepareTransactionDetails(request);

        return messagePublisher.publishMessage(RabbitMQProperties.CREATE_TRANSACTION_ROUTING_KEY, createTransactionRequest, CreateTransactionRabbitMQResponse.class);
    }

    public List<GetTransactionResponse> getTransactions(String accountId) {
        return transactionService.getTransactions(accountId);
    }
}
