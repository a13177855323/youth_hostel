package com.youthhostel.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youthhostel.entity.dto.UserDTO;
import com.youthhostel.entity.query.UserQuery;
import com.youthhostel.entity.user.User;
import com.youthhostel.entity.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    UserVO getUserById(Long id);

    List<UserVO> listUsers(UserQuery query);

    boolean saveUser(UserDTO userDTO);

    boolean updateUser(UserDTO userDTO);

    boolean deleteUser(Long id);
}
