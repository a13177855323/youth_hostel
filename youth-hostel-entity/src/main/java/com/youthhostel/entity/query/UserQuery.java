package com.youthhostel.entity.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
