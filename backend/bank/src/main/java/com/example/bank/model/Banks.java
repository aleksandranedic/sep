package com.example.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("banks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banks {
    @Id
    String id;
    String bankId;
    String pan;
}
