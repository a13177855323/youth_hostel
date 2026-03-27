package com.youth.hostel.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignInResultVO implements Serializable {

    private Integer earnedPoints;

    private Integer totalPoints;

    private Integer continuousDays;
}
