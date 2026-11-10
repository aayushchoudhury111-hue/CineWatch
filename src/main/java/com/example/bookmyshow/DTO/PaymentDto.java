package com.example.bookmyshow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String transactionId;
    private double amount;
    private LocalDateTime paymentTime;
    private String paymentMethod;
    private String status;
}
