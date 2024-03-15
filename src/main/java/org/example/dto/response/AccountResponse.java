package org.example.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountResponse extends BaseResponse {
    private String accountId;
    private String customerId;
    private List<BalanceResponse> balances = new ArrayList<>();
}
