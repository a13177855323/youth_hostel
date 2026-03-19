package com.youthhostel.web.controller.user;

import com.youthhostel.common.result.Result;
import com.youthhostel.entity.dto.UserDTO;
import com.youthhostel.entity.query.UserQuery;
import com.youthhostel.entity.vo.UserVO;
import com.youthhostel.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }

    @GetMapping("/list")
    public Result<List<UserVO>> listUsers(UserQuery query) {
        List<UserVO> list = userService.listUsers(query);
        return Result.success(list);
    }

    @PostMapping
    public Result<Boolean> saveUser(@Valid @RequestBody UserDTO userDTO) {
        boolean result = userService.saveUser(userDTO);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> updateUser(@Valid @RequestBody UserDTO userDTO) {
        boolean result = userService.updateUser(userDTO);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        return Result.success(result);
    }
}
