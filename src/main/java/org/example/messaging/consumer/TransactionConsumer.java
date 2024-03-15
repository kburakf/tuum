package org.example.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.model.vo.TransactionVo;
import org.example.properties.RabbitMQProperties;
import org.example.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionConsumer {
    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQProperties.CREATE_TRANSACTION_QUEUE)
    public CreateTransactionRabbitMQResponse receiveMessage(TransactionVo transactionVo) {
        return transactionService.createTransaction(transactionVo);
    }
}
