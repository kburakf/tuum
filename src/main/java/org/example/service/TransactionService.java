package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.example.dto.request.CreateTransactionRequest;
import org.example.dto.response.CreateTransactionRabbitMQResponse;
import org.example.dto.response.GetTransactionResponse;
import org.example.enumtypes.Currency;
import org.example.enumtypes.TransactionDirection;
import org.example.exception.ConstantErrorMessages;
import org.example.exception.InsufficientFundsException;
import org.example.mapper.TransactionMapper;
import org.example.model.Balance;
import org.example.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final BalanceService balanceService;
    private final AccountService accountService;

    @Transactional
    public CreateTransactionRabbitMQResponse createTransaction(CreateTransactionRequest request) {
        Balance balance = balanceService.retrieveBalanceByAccountIdAndCurrency(request.getAccountId(), String.valueOf(request.getCurrency()));

        checkBalanceAmountForTransaction(request, balance);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(EnumUtils.getEnum(Currency.class, request.getCurrency()));
        transaction.setDirection(EnumUtils.getEnum(TransactionDirection.class, request.getDirection()));
        transaction.setDescription(request.getDescription());
        transactionMapper.insert(transaction);

        BigDecimal calculatedBalance = calculateBalance(request, balance);
        balanceService.updateBalanceById(balance.getId(), calculatedBalance);

        return mapToCreateTransactionResponse(transaction, calculatedBalance);
    }

    public void preCreateTransactionCheck(CreateTransactionRequest request) {
        accountService.getAccount(request.getAccountId());
        Balance balance = balanceService.retrieveBalanceByAccountIdAndCurrency(request.getAccountId(), request.getCurrency());
        checkBalanceAmountForTransaction(request, balance);
    }

    public void checkBalanceAmountForTransaction(CreateTransactionRequest request, Balance balance){
        if (TransactionDirection.valueOf(String.valueOf(request.getDirection())) == TransactionDirection.OUT
                && balance.getAvailableAmount().compareTo(request.getAmount()) <= 0) {
            throw new InsufficientFundsException(ConstantErrorMessages.INSUFFICIENT_FUNDS);
        }
    }

    public CreateTransactionRequest prepareTransactionDetails(CreateTransactionRequest request) {
        return CreateTransactionRequest
                .builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .direction(request.getDirection())
                .description(request.getDescription())
                .currency(request.getCurrency())
                .build();
    }

    public List<GetTransactionResponse> getTransactions(String accountId) {
        List<Transaction> transactions = transactionMapper.findAllTransactionsByAccountId(accountId);

        if (transactions.isEmpty()) {
            return new ArrayList<>();
        }

        return mapToGetTransactionResponse(transactions);
    }

    private BigDecimal calculateBalance(CreateTransactionRequest request, Balance balance) {
        BigDecimal currentBalanceAmount = balance.getAvailableAmount();
        TransactionDirection direction = TransactionDirection.valueOf(request.getDirection().toUpperCase());

        return direction == TransactionDirection.IN
                ? currentBalanceAmount.add(request.getAmount())
                : currentBalanceAmount.subtract(request.getAmount());
    }

    private List<GetTransactionResponse> mapToGetTransactionResponse(List<Transaction> transactions) {
        List<GetTransactionResponse> transactionList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            GetTransactionResponse response = new GetTransactionResponse();
            response.setAccountId(transaction.getAccountId());
            response.setTransactionId(transaction.getId());
            response.setAmount(transaction.getAmount());
            response.setCurrency(EnumUtils.getEnum(Currency.class, transaction.getCurrency().name()));
            response.setDirection(transaction.getDirection());
            response.setDescription(transaction.getDescription());

            transactionList.add(response);
        }

        return transactionList;
    }

    private CreateTransactionRabbitMQResponse mapToCreateTransactionResponse(Transaction transaction, BigDecimal availableAmount) {
        CreateTransactionRabbitMQResponse response = new CreateTransactionRabbitMQResponse();
        response.setAccountId(transaction.getAccountId());
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setDirection(transaction.getDirection());
        response.setDescription(transaction.getDescription());
        response.setBalanceAfterTransaction(availableAmount);

        return response;
    }
}
