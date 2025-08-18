package com.aetherbuilder.nocode.service;

import com.aetherbuilder.nocode.model.dto.app.AppAddRequest;
import com.aetherbuilder.nocode.model.dto.app.AppQueryRequest;
import com.aetherbuilder.nocode.model.entity.App;
import com.aetherbuilder.nocode.model.entity.User;
import com.aetherbuilder.nocode.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用服务接口
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用封装类
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用封装类列表
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 应用聊天生成代码（流式）
     *
     * @param appId   应用 ID
     * @param message 用户消息
     * @param loginUser 登录用户
     * @param agent 是否启用 Agent 模式
     * @return 生成的代码流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser, boolean agent);

    /**
     * 应用部署
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    void generateAppScreenshotAsync(Long appId, String appUrl);

    /**
     * 创建应用
     * @param appAddRequest
     * @param loginUser
     * @return
     */
    Long createApp(AppAddRequest appAddRequest, User loginUser);

}
