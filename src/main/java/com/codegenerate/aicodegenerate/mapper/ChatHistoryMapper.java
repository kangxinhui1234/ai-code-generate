package com.codegenerate.aicodegenerate.mapper;

import com.codegenerate.aicodegenerate.entity.ChatHistory;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 对话历史 映射层。
 *
 * @author kxh
 */
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 根据应用ID查询对话历史（按时间降序）
     *
     * @param appId 应用ID
     * @param limit 限制条数
     * @return 对话历史列表
     */
    List<ChatHistory> selectByAppIdOrderByCreateTimeDesc(@Param("appId") Long appId, @Param("limit") Integer limit);

    /**
     * 根据应用ID分页查询对话历史（按时间降序）
     *
     * @param appId 应用ID
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 对话历史列表
     */
    List<ChatHistory> selectByAppIdWithPageOrderByCreateTimeDesc(@Param("appId") Long appId, 
                                                               @Param("offset") Long offset, 
                                                               @Param("limit") Long limit);

    /**
     * 查询所有对话历史（按时间降序）- 管理员使用
     *
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 对话历史列表
     */
    List<ChatHistory> selectAllOrderByCreateTimeDesc(@Param("offset") Long offset, @Param("limit") Long limit);

    /**
     * 根据应用ID删除对话历史
     *
     * @param appId 应用ID
     * @return 删除的记录数
     */
    int deleteByAppId(@Param("appId") Long appId);

    /**
     * 统计应用的对话历史数量
     *
     * @param appId 应用ID
     * @return 对话历史数量
     */
    long countByAppId(@Param("appId") Long appId);
}
