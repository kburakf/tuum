package org.example.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.CreateAccountRabbitMQResponse;
import org.example.dto.response.AccountWithBalancesResponse;
import org.example.messaging.publisher.MessagePublisher;
import org.example.properties.RabbitMQProperties;
import org.example.service.AccountService;
import org.example.util.AccountValidationUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountManager {
    private final MessagePublisher messagePublisher;
    private final AccountService accountService;

    public CreateAccountRabbitMQResponse createAccount(CreateAccountRequest request) {
        AccountValidationUtil.validateAccountRequest(request);

        return messagePublisher.publishMessage(RabbitMQProperties.CREATE_ACCOUNT_ROUTING_KEY, request, CreateAccountRabbitMQResponse.class);
    }

    public AccountWithBalancesResponse getAccountWithBalances(String accountId) {
        return accountService.getAccountWithBalances(accountId);
    }
}
