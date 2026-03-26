package com.youth.hostel.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    private String token;

    private String roleCode;
}
