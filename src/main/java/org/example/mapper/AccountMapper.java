package org.example.mapper;

import org.apache.ibatis.annotations.*;

import org.example.model.Account;

import java.util.Optional;

@Mapper
public interface AccountMapper {

    @Select("SELECT * FROM accounts WHERE id = #{id}")
    Optional<Account> findById(@Param("id") String id);

    @Insert("INSERT INTO accounts (id, customer_id, country) VALUES (#{id}, #{customerId}, #{country})")
    void insertAccount(Account account);
}
