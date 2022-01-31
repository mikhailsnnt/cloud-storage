package com.sainnt.server.dto;

public enum RegistrationResult {
    success,
    username_occupied,
    email_exists,
    email_invalid,
    password_invalid,
    registration_failed
}
