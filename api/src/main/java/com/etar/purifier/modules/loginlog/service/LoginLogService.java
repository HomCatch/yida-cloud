package com.etar.purifier.modules.loginlog.service;

import com.etar.purifier.base.IBaseService;
import com.etar.purifier.modules.loginlog.entity.LoginLog;
import com.etar.purifier.modules.loginlog.entity.QueryLoginLog;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * <p>
 *  LoginLog接口
 * </p>
 *
 * @author gmq
 * @since 2018-12-25
 */

public interface LoginLogService  extends IBaseService<LoginLog,Integer> {

    /**
     * 按条件查询
     * @param page 页数
     * @param pageSize 数量
     * @param queryLoginLog
     * @return Page
     */
    Page<LoginLog> findAll(int page, int pageSize, QueryLoginLog queryLoginLog);

    /**
     * 根据Id查询list
     * @param ids id集合
     * @return list
     */
    List<LoginLog> findAllById(List<Integer> ids);

    /**
     * 批量删除
     * @param integers id集合
     * @return int
     */
    int deleteInBatch(List<Integer> integers);

}


