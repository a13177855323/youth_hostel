package com.youth.hostel.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RoomBookDTO implements Serializable {

    private Long roomId;

    private Long userId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer quantity;
}
