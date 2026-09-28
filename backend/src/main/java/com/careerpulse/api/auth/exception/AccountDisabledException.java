package com.careerpulse.api.auth.exception;

public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException() {
        super("User account is disabled");
    }
}