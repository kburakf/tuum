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
import org.example.exception.TransactionNotFoundException;
import org.example.mapper.TransactionMapper;
import org.example.model.Balance;
import org.example.model.Transaction;
import org.example.model.vo.TransactionVo;
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

    @Transactional
    public CreateTransactionRabbitMQResponse createTransaction(TransactionVo transactionVo) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccountId(transactionVo.getAccountId());
        transaction.setAmount(transactionVo.getAmount());
        transaction.setCurrency(transactionVo.getCurrency());
        transaction.setDirection(transactionVo.getDirection());
        transaction.setDescription(transactionVo.getDescription());
        transactionMapper.insert(transaction);

        BigDecimal calculatedBalance = calculateBalance(transactionVo);
        balanceService.updateBalanceById(transactionVo.getBalanceId(), calculatedBalance);

        return mapToCreateTransactionResponse(transaction, calculatedBalance);
    }

    public TransactionVo getAccount(CreateTransactionRequest request) {
        Balance balance = balanceService.retrieveBalanceByAccountIdAndCurrency(request.getAccountId(), request.getCurrency());

        return preprocessTransaction(balance, request);
    }

    private TransactionVo preprocessTransaction(Balance balance, CreateTransactionRequest request) {
        if (TransactionDirection.valueOf(request.getDirection()) == TransactionDirection.OUT && balance.getAvailableAmount().compareTo(request.getAmount()) <= 0) {
            throw new InsufficientFundsException(ConstantErrorMessages.INSUFFICIENT_FUNDS);
        }

        return TransactionVo
                .builder()
                .amount(request.getAmount())
                .balanceId(balance.getId())
                .accountId(balance.getAccountId())
                .direction(EnumUtils.getEnum(TransactionDirection.class, request.getDirection()))
                .description(request.getDescription())
                .balanceAmount(balance.getAvailableAmount())
                .currency(EnumUtils.getEnum(Currency.class, request.getCurrency()))
                .build();
    }

    public List<GetTransactionResponse> getTransactions(String accountId) {
        List<Transaction> transactions = transactionMapper.findAllTransactionsByAccountId(accountId);

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(ConstantErrorMessages.TRANSACTION_NOT_FOUND);
        }

        return mapToGetTransactionResponse(transactions);
    }

    private BigDecimal calculateBalance(TransactionVo transactionVo) {
        return transactionVo.getDirection() == TransactionDirection.IN
                ? transactionVo.getBalanceAmount().add(transactionVo.getAmount())
                : transactionVo.getBalanceAmount().subtract(transactionVo.getAmount());
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
