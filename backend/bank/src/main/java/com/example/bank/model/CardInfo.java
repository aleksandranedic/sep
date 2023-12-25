package com.example.bank.model;

import com.example.bank.configuration.Encrypt;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    @Convert(converter = Encrypt.class)
    String pan;
    @Convert(converter = Encrypt.class)
    String securityCode;
    @Convert(converter = Encrypt.class)
    String cardHolderName;
    @Convert(converter = Encrypt.class)
    int expiryMonth;
    @Convert(converter = Encrypt.class)
    int expiryYear;
}
