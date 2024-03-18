package org.example.controller;

import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountWithBalancesResponse;
import org.example.enumtypes.Currency;
import org.example.exception.ConstantErrorMessages;
import org.example.mapper.AccountMapper;
import org.example.model.Account;
import org.example.properties.RabbitMQProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringRabbitTest
@Testcontainers
@ActiveProfiles("test")
public class AccountControllerIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:latest");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AmqpAdmin amqpAdmin;

    private String baseUrl;

    @DynamicPropertySource
    static void rabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getAmqpPort());
    }

    @BeforeEach
    void setUp() {
        this.baseUrl = "http://localhost:" + port + "/api/v1/accounts";
    }

    @AfterEach
    void tearDown() {
        amqpAdmin.purgeQueue(RabbitMQProperties.CREATE_ACCOUNT_QUEUE, true);
    }

    private String createTestAccount() {
        String accountId = UUID.randomUUID().toString();
        Account account = new Account();
        account.setId(accountId);
        account.setCustomerId("123");
        account.setCountry("US");
        accountMapper.insertAccount(account);
        return accountId;
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_create_account_with_single_currency() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("123")
                .country("EE")
                .currencies(Collections.singletonList("EUR"))
                .build();

        ResponseEntity<AccountWithBalancesResponse> response = restTemplate.postForEntity(baseUrl, request, AccountWithBalancesResponse.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccountId()).isNotNull();
        assertThat(response.getBody().getBalances()).isNotEmpty().hasSize(1);
        assertThat(response.getBody().getBalances().get(0).getCurrency()).isEqualTo(Currency.EUR);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_create_account_with_multiple_currency() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("123")
                .country("EE")
                .currencies(List.of("EUR", "USD"))
                .build();

        ResponseEntity<AccountWithBalancesResponse> response = restTemplate.postForEntity(baseUrl, request, AccountWithBalancesResponse.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccountId()).isNotNull();
        assertThat(response.getBody().getBalances()).isNotEmpty().hasSize(2);
        assertThat(response.getBody().getBalances().get(0).getCurrency()).isEqualTo(Currency.EUR);
        assertThat(response.getBody().getBalances().get(1).getCurrency()).isEqualTo(Currency.USD);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_account_with_invalid_currency() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId("123")
                .country("EE")
                .currencies(List.of("test"))
                .build();

        ResponseEntity<AccountWithBalancesResponse> response = restTemplate.postForEntity(baseUrl, request, AccountWithBalancesResponse.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.INVALID_CURRENCY);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_get_account_by_id() {
        String accountId = createTestAccount();

        ResponseEntity<AccountWithBalancesResponse> getResponse = restTemplate.getForEntity(baseUrl + "/" + accountId, AccountWithBalancesResponse.class);

        assertThat(getResponse.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getAccountId()).isEqualTo(accountId);
        assertThat(getResponse.getBody().getCustomerId()).isEqualTo("123");
        assertThat(getResponse.getBody().getBalances()).hasSize(0);
    }

    @Test
    public void should_not_get_account_by_id() {
        ResponseEntity<AccountWithBalancesResponse> response = restTemplate.getForEntity(baseUrl + "/" + "123", AccountWithBalancesResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.ACCOUNT_NOT_FOUND);
    }
}
