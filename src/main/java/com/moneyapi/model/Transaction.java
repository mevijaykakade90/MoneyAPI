package com.moneyapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

	private int transcationId;
    private int senderId;
    private String receiverId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private String description;
    
   
}
