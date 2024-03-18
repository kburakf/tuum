package org.example.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.CreateAccountRabbitMQResponse;
import org.example.properties.RabbitMQProperties;
import org.example.service.AccountService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountConsumer {
    private final AccountService accountService;

    @RabbitListener(queues = RabbitMQProperties.CREATE_ACCOUNT_QUEUE)
    public CreateAccountRabbitMQResponse receiveMessage(CreateAccountRequest request) {
        return accountService.createAccount(request);
    }
}
