package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.model.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BalanceMapper {

    @Select("SELECT * FROM balances WHERE account_id = #{accountId}")
    List<Balance> findByAccountId(@Param("accountId") String accountId);

    @Insert("INSERT INTO balances (id, account_id, available_amount, currency) VALUES (#{id}, #{accountId}, #{availableAmount}, #{currency})")
    void insert(Balance balance);

    @Update("UPDATE balances SET available_amount = #{calculatedBalance} WHERE id = #{balanceId}")
    void updateAmount(@Param("balanceId") String balanceId, @Param("calculatedBalance") BigDecimal calculatedBalance);

    @Select("SELECT * FROM balances WHERE account_id = #{accountId} AND currency = #{currency} FOR UPDATE")
    Optional<Balance> findByAccountIdAndCurrency(@Param("accountId") String accountId, @Param("currency") String currency);

}
