package com.codegenerate.aicodegenerate.service;

import com.codegenerate.aicodegenerate.entity.ChatHistory;
import com.codegenerate.aicodegenerate.entity.User;
import com.codegenerate.aicodegenerate.model.dto.chat.ChatHistoryQueryRequest;
import com.codegenerate.aicodegenerate.model.vo.ChatHistoryVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层。
 *
 * @author kxh
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 获取对话历史VO
     *
     * @param chatHistory 对话历史实体
     * @return 对话历史VO
     */
    ChatHistoryVO getChatHistoryVO(ChatHistory chatHistory);

    /**
     * 获取对话历史VO列表
     *
     * @param chatHistoryList 对话历史实体列表
     * @return 对话历史VO列表
     */
    List<ChatHistoryVO> getChatHistoryVOList(List<ChatHistory> chatHistoryList);

    /**
     * 获取查询条件构造器
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 查询条件构造器
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 保存用户消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @return 是否保存成功
     */
    boolean saveUserMessage(Long appId, Long userId, String message);

    /**
     * 保存AI回复消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @return 是否保存成功
     */
    boolean saveAiMessage(Long appId, Long userId, String message);

    /**
     * 保存错误消息
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param errorMessage 错误消息内容
     * @return 是否保存成功
     */
    boolean saveErrorMessage(Long appId, Long userId, String errorMessage);

    /**
     * 分页查询应用的对话历史（按时间降序）
     *
     * @param appId         应用ID
     * @param localDateTime 当前页
     * @param pageSize      页大小
     * @return 对话历史列表
     */
    List<ChatHistoryVO> getAppChatHistory(Long appId, LocalDateTime localDateTime, long pageSize);

    /**
     * 获取应用的最新对话历史（用于初始化）
     *
     * @param appId 应用ID
     * @param limit 限制条数
     * @return 对话历史列表
     */
    List<ChatHistoryVO> getLatestAppChatHistory(Long appId, int limit);

    /**
     * 管理员查询所有对话历史（按时间降序）
     *
     * @param current 当前页
     * @param pageSize 页大小
     * @return 对话历史列表
     */
    List<ChatHistoryVO> getAllChatHistoryForAdmin(long current, long pageSize);

    /**
     * 删除应用的所有对话历史
     *
     * @param appId 应用ID
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 验证用户是否有权限查看应用的对话历史
     *
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return 是否有权限
     */
    boolean hasPermissionToViewChatHistory(Long appId, User loginUser);

    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}
