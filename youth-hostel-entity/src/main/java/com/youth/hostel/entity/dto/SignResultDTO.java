package com.youth.hostel.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SignResultDTO implements Serializable {

    private Boolean success;

    private Integer pointsEarned;

    private Integer totalPoints;

    private Integer consecutiveDays;

    private List<Map<String, Object>> signCalendar;
}
