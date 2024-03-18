package org.example.exception;

public class ConstantErrorMessages {
    public static final String INVALID_CURRENCY = "Invalid currency input. Allowed values are EUR, SEK, GBP, USD";
    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String BALANCE_NOT_FOUND = "Balance not found";
    public static final String INVALID_AMOUNT = "The provided amount is invalid. It must be a positive number";
    public static final String ACCOUNT_MISSING = "The account ID is missing or invalid";
    public static final String INVALID_DIRECTION = "Transaction direction must be \"IN\" or \"OUT\"";
    public static final String INSUFFICIENT_FUNDS = "Account has insufficient funds for outbound transaction";
    public static final String DESCRIPTION_MISSING = "The transaction description is missing or invalid ";
}
