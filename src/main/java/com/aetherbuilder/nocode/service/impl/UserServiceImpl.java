package com.aetherbuilder.nocode.service.impl;

import com.aetherbuilder.nocode.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.aetherbuilder.nocode.model.entity.User;
import com.aetherbuilder.nocode.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/Kim-op">程序员cammy</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

}
