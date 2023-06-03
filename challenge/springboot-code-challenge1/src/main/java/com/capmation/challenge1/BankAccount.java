package com.capmation.challenge1;

import org.springframework.data.annotation.Id;


public record BankAccount(@Id Long id, Double amount, String accountType, String owner) {

}