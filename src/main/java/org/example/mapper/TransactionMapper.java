package org.example.mapper;

import org.apache.ibatis.annotations.*;

import org.example.model.Transaction;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Select("SELECT * FROM transactions WHERE account_id = #{accountId}")
    List<Transaction> findAllTransactionsByAccountId(@Param("accountId") String accountId);

    @Insert("INSERT INTO transactions (id, account_id, amount, currency, direction, description) " +
            "VALUES (#{id}, #{accountId}, #{amount}, #{currency}, #{direction}, #{description})")
    void insert(Transaction transaction);
}
