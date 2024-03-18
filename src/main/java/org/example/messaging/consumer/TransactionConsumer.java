package org.example.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.properties.RabbitMQProperties;
import org.example.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionConsumer {
    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQProperties.CREATE_TRANSACTION_QUEUE)
    public CreateTransactionRabbitMQResponse receiveMessage(CreateTransactionRequest createTransactionRequest) {
        return transactionService.createTransaction(createTransactionRequest);
    }
}
