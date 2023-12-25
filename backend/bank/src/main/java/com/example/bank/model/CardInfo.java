package com.example.bank.model;

import com.example.bank.configuration.EncryptionUtil;
import lombok.*;

@NoArgsConstructor
public class CardInfo {
    String pan;
    String securityCode;
    String cardHolderName;
    @Getter
    @Setter
    int expiryMonth;
    @Getter
    @Setter
    int expiryYear;

    public String getPan() {
        return EncryptionUtil.decrypt(pan);
    }

    public void setPan(String pan) {
        this.pan = EncryptionUtil.encrypt(pan);
    }

    public String getSecurityCode() {
        return EncryptionUtil.decrypt(securityCode);
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = EncryptionUtil.encrypt(securityCode);
    }

    public String getCardHolderName() {
        return EncryptionUtil.decrypt(cardHolderName);
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = EncryptionUtil.encrypt(cardHolderName);
    }
}
