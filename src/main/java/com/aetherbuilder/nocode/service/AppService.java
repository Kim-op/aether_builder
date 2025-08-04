package com.aetherbuilder.nocode.service;

import com.aetherbuilder.nocode.model.dto.app.AppAddRequest;
import com.aetherbuilder.nocode.model.dto.app.AppAdminUpdateRequest;
import com.aetherbuilder.nocode.model.dto.app.AppQueryRequest;
import com.aetherbuilder.nocode.model.dto.app.AppUserUpdateRequest;
import com.aetherbuilder.nocode.model.entity.User;
import com.aetherbuilder.nocode.model.vo.AppVO;
import com.aetherbuilder.nocode.model.entity.App;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用服务接口
 */
public interface AppService extends IService<App> {

    /**
     * 通过对话生成应用代码
     *
     * @param appId 应用 ID
     * @param message 提示词
     * @param loginUser 登录用户
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 应用部署
     *
     * @param appId 应用 ID
     * @param loginUser 登录用户
     * @return 可访问的部署地址
     */
    String deployApp(Long appId, User loginUser);

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
}
