package com.youth.hostel.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class SignInInfoVO implements Serializable {

    private Integer continuousDays;

    private Integer totalPoints;

    private Integer availablePoints;

    private List<LocalDate> signInDates;
}
