package org.example.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountRabbitMQResponse;
import org.example.dto.response.AccountResponse;
import org.example.messaging.publisher.MessagePublisher;
import org.example.properties.RabbitMQProperties;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountManager {
    private final MessagePublisher messagePublisher;
    private final AccountService accountService;

    public AccountRabbitMQResponse createAccount(CreateAccountRequest request) {
        request.validate();

        return messagePublisher.publishMessage(RabbitMQProperties.CREATE_ACCOUNT_ROUTING_KEY, request, AccountRabbitMQResponse.class);
    }

    public AccountResponse getAccount(String accountId) {
        return accountService.getAccount(accountId);
    }
}
