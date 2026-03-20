package com.youthhostel.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youthhostel.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
