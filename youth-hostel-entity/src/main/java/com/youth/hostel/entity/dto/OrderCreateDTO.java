package com.youth.hostel.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderCreateDTO implements Serializable {

    private Long userId;

    private BigDecimal totalAmount;
}
