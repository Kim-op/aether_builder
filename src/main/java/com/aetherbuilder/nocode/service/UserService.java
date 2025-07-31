package com.aetherbuilder.nocode.service;

import com.aetherbuilder.nocode.model.vo.user.LoginUserVO;
import com.mybatisflex.core.service.IService;
import com.aetherbuilder.nocode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/Kim-op">程序员cammy</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取加密后的密码
     *
     * @param userPassword <PASSWORD>
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount   用户账户
     * @param userPassword  <PASSWORD>
     * @param request       请求
     * @return 登录结果
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getLoginUser(HttpServletRequest request);
}
