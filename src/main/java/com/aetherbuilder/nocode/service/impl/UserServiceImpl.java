package com.aetherbuilder.nocode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aetherbuilder.nocode.exception.BusinessException;
import com.aetherbuilder.nocode.exception.ErrorCode;
import com.aetherbuilder.nocode.model.enums.UserRoleEnum;
import com.aetherbuilder.nocode.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.aetherbuilder.nocode.model.entity.User;
import com.aetherbuilder.nocode.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/Kim-op">程序员cammy</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4个字符");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8个字符");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 2.检查是否重复
        QueryWrapper queryWrapper = new QueryWrapper()
                .eq(User::getUserAccount, userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3.加密
        String encryptPassword = this.getEncryptPassword(userPassword);
        // 4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(this.generateAbstractUsername());
        user.setUserAvatar("https://cammy-1327275726.cos.ap-guangzhou.myqcloud.com/logo.png");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        final String slat = "cammy";
        return DigestUtils.md5DigestAsHex((slat + userPassword).getBytes());
    }

    // 抽象形容词集合
    private static final List<String> ADJECTIVES = Arrays.asList(
            "量子", "混沌", "熵增", "递归", "异步",
            "多态", "抽象", "元", "高阶", "柯里化"
    );

    // 抽象名词集合
    private static final List<String> NOUNS = Arrays.asList(
            "观察者", "迭代器", "闭包", "单子", "函子",
            "协程", "原型", "组合子", "λ", "Y算子"
    );

    // 抽象后缀集合
    private static final List<String> SUFFIXES = Arrays.asList(
            "42", "π", "e", "∞", "√2",
            "i", "φ", "ℵ", "∅", "∇"
    );

    /**
     * 生成抽象风格用户名
     * 组合方式: 形容词 + 名词 + 后缀
     */
    private String generateAbstractUsername() {
        Collections.shuffle(ADJECTIVES);
        Collections.shuffle(NOUNS);
        Collections.shuffle(SUFFIXES);

        return ADJECTIVES.get(0) + NOUNS.get(0) + SUFFIXES.get(0);
    }
}
