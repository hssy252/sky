package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public interface UserService {

    /**
     * 用户微信登录
     */

    User wxLogin(UserLoginDTO userLoginDTO);

}
