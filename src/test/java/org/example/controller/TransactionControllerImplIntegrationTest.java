package org.example.controller;

import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionResponse;
import org.example.dto.response.GetTransactionResponse;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;
import org.example.exception.ConstantErrorMessages;
import org.example.mapper.AccountMapper;
import org.example.mapper.BalanceMapper;
import org.example.model.Account;
import org.example.model.Balance;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringRabbitTest
@Testcontainers
@ActiveProfiles("test")
public class TransactionControllerImplIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:latest");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private AmqpAdmin amqpAdmin;

    private String transactionBaseUrl;

    @DynamicPropertySource
    static void rabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getAmqpPort());
    }

    @BeforeEach
    void setUp() {
        this.transactionBaseUrl = "http://localhost:" + port + "/api/v1/transactions";
    }

    @AfterEach
    void tearDown() {
        amqpAdmin.purgeQueue(RabbitMQProperties.CREATE_TRANSACTION_QUEUE, true);
    }


    private String createTestAccount(String customerId, String country) {
        String accountId = UUID.randomUUID().toString();
        Account account = new Account();
        account.setId(accountId);
        account.setCustomerId(customerId);
        account.setCountry(country);
        accountMapper.insertAccount(account);

        return accountId;
    }

    private void createTestBalance(String accountId, BigDecimal amount) {
        Balance balance = new Balance();
        balance.setId(UUID.randomUUID().toString());
        balance.setAccountId(accountId);
        balance.setAvailableAmount(amount);
        balance.setCurrency(Currency.USD);
        balanceMapper.insert(balance);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_create_transaction() {
        String accountId = createTestAccount("456", "US");
        createTestBalance(accountId, new BigDecimal("500"));

        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .accountId(accountId)
                .amount(new BigDecimal("100"))
                .currency("USD")
                .direction("IN")
                .description("Test Transaction")
                .build();

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccountId()).isEqualTo(accountId);
        assertThat(response.getBody().getAmount()).isEqualTo(new BigDecimal("100"));
        assertThat(response.getBody().getCurrency()).isEqualTo(Currency.USD);
        assertThat(response.getBody().getDirection()).isEqualTo(TransactionDirection.IN);
        assertThat(response.getBody().getDescription()).isEqualTo("Test Transaction");
    }


    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_with_invalid_account() {
        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .accountId("non-existing-id")
                .amount(new BigDecimal("50"))
                .currency("USD")
                .direction("IN")
                .description("Test Invalid Account")
                .build();

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(transactionBaseUrl, transactionRequest, CreateTransactionResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.ACCOUNT_NOT_FOUND);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_due_to_insufficient_funds() {
        String accountId = createTestAccount("456", "US");

        createTestBalance(accountId, new BigDecimal("50"));

        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .accountId(accountId)
                .amount(new BigDecimal("150"))
                .currency("USD")
                .direction("OUT")
                .description("Insufficient funds transaction")
                .build();

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.INSUFFICIENT_FUNDS);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_with_invalid_direction() {
        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .accountId("account-id")
                .amount(new BigDecimal("50"))
                .currency("USD")
                .direction("INVALID_DIRECTION")
                .description("Invalid direction transaction")
                .build();

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.INVALID_DIRECTION);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_when_account_id_is_missing() {
        CreateTransactionRequest transactionRequest = CreateTransactionRequest.builder()
                .amount(new BigDecimal("50"))
                .currency("USD")
                .direction("IN")
                .description("Missing account ID transaction")
                .build();

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.ACCOUNT_MISSING);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_when_description_is_missing() {
        String accountId = createTestAccount("456", "US");

        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setAmount(new BigDecimal("50"));
        transactionRequest.setCurrency("USD");
        transactionRequest.setDirection("IN");

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.DESCRIPTION_MISSING);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_with_nonexistent_currency_balance() {
        String accountId = createTestAccount("123", "EE");
        createTestBalance(accountId, new BigDecimal("50"));

        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setAmount(new BigDecimal("50"));
        transactionRequest.setCurrency("EUR");
        transactionRequest.setDirection("OUT");
        transactionRequest.setDescription("Nonexistent currency transaction");

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.BALANCE_NOT_FOUND);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_create_transaction_with_negative_amount() {
        String accountId = createTestAccount("123", "EE");
        createTestBalance(accountId, new BigDecimal("100"));

        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setAccountId(accountId);
        transactionRequest.setAmount(new BigDecimal("-100"));
        transactionRequest.setCurrency("USD");
        transactionRequest.setDirection("IN");
        transactionRequest.setDescription("Negative amount transaction");

        ResponseEntity<CreateTransactionResponse> response = restTemplate.postForEntity(
                transactionBaseUrl,
                transactionRequest,
                CreateTransactionResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(ConstantErrorMessages.INVALID_AMOUNT);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_not_get_transaction() {
        ResponseEntity<List<GetTransactionResponse>> response = restTemplate.exchange(
                transactionBaseUrl + "/" + "123",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/revert-all-data.sql")
    public void should_get_two_transactions_for_account() {
        String accountId = createTestAccount("456", "US");
        createTestBalance(accountId, new BigDecimal("300"));

        for (int i = 0; i < 2; i++) {
            CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
            transactionRequest.setAccountId(accountId);
            transactionRequest.setAmount(new BigDecimal("50"));
            transactionRequest.setCurrency("USD");
            transactionRequest.setDirection("IN");
            transactionRequest.setDescription("Test Transaction " + i);

            ResponseEntity<CreateTransactionResponse> transactionResponse = restTemplate.postForEntity(
                    transactionBaseUrl,
                    transactionRequest,
                    CreateTransactionResponse.class);

            assertThat(transactionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        ResponseEntity<List<GetTransactionResponse>> response = restTemplate.exchange(
                transactionBaseUrl + "/" + accountId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
    }
}
