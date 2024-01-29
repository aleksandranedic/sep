package com.example.paymentcardcenter.model;

import com.example.paymentcardcenter.dto.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pcc-transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PCCTransaction {
    @Id
    String id;
    CardInfo cardInfo;
    String acquirerOrderId;
    String acquirerTimestamp;
    String issuerOrderId;
    String issuerTimestamp;

    public PCCTransaction(CardInfo cardInfo, String acquirerOrderId, String acquirerTimestamp, String issuerOrderId, String issuerTimestamp) {
        this.cardInfo = cardInfo;
        this.acquirerTimestamp = acquirerTimestamp;
        this.acquirerOrderId = acquirerOrderId;
        this.issuerTimestamp = issuerTimestamp;
        this.issuerOrderId = issuerOrderId;
    }
}
