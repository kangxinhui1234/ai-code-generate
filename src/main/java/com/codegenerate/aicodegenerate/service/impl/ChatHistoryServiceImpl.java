package com.codegenerate.aicodegenerate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.codegenerate.aicodegenerate.entity.App;
import com.codegenerate.aicodegenerate.entity.ChatHistory;
import com.codegenerate.aicodegenerate.entity.User;
import com.codegenerate.aicodegenerate.exception.BussessException;
import com.codegenerate.aicodegenerate.exception.ErrorCode;
import com.codegenerate.aicodegenerate.mapper.ChatHistoryMapper;

import com.codegenerate.aicodegenerate.model.dto.chat.ChatHistoryQueryRequest;
import com.codegenerate.aicodegenerate.model.enums.MessageTypeEnum;
import com.codegenerate.aicodegenerate.service.AppService;
import com.codegenerate.aicodegenerate.service.ChatHistoryService;
import com.codegenerate.aicodegenerate.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codegenerate.aicodegenerate.model.user_enum.UserRoleEnum;
import com.codegenerate.aicodegenerate.model.vo.AppVO;
import com.codegenerate.aicodegenerate.model.vo.ChatHistoryVO;
import com.codegenerate.aicodegenerate.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对话历史 服务层实现。
 *
 * @author kxh
 */
@Service
@Transactional
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;

    @Override
    public ChatHistoryVO getChatHistoryVO(ChatHistory chatHistory) {
        if (chatHistory == null) {
            return null;
        }
        ChatHistoryVO chatHistoryVO = new ChatHistoryVO();
        BeanUtil.copyProperties(chatHistory, chatHistoryVO);
        
        // 关联查询用户信息
        Long userId = chatHistory.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            chatHistoryVO.setUser(userVO);
        }
        
        // 关联查询应用信息
        Long appId = chatHistory.getAppId();
        if (appId != null) {
            App app = appService.getById(appId);
            AppVO appVO = appService.getAppVO(app);
            chatHistoryVO.setApp(appVO);
        }
        
        return chatHistoryVO;
    }

    @Override
    public List<ChatHistoryVO> getChatHistoryVOList(List<ChatHistory> chatHistoryList) {
        if (CollUtil.isEmpty(chatHistoryList)) {
            return new ArrayList<>();
        }
        
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = chatHistoryList.stream()
                .map(ChatHistory::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        
        // 批量获取应用信息，避免 N+1 查询问题
        Set<Long> appIds = chatHistoryList.stream()
                .map(ChatHistory::getAppId)
                .collect(Collectors.toSet());
        Map<Long, AppVO> appVOMap = appService.listByIds(appIds).stream()
                .collect(Collectors.toMap(App::getId, appService::getAppVO));
        
        return chatHistoryList.stream().map(chatHistory -> {
            ChatHistoryVO chatHistoryVO = getChatHistoryVO(chatHistory);
            UserVO userVO = userVOMap.get(chatHistory.getUserId());
            AppVO appVO = appVOMap.get(chatHistory.getAppId());
            chatHistoryVO.setUser(userVO);
            chatHistoryVO.setApp(appVO);
            return chatHistoryVO;
        }).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        if (chatHistoryQueryRequest == null) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "请求参数为空");
        }
        
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();

        QueryWrapper queryWrapper =  QueryWrapper.create()
                .eq("id", id)
                .like("message", message)
                .eq("messageType", messageType)
                .eq("appId", appId)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
        if(chatHistoryQueryRequest.getCreateTime() != null){
            queryWrapper.lt("createTime",chatHistoryQueryRequest.getCreateTime());
        }

        return queryWrapper;
    }

    @Override
    public boolean saveUserMessage(Long appId, Long userId, String message) {
        return saveMessage(appId, userId, message, MessageTypeEnum.USER.getValue());
    }

    @Override
    public boolean saveAiMessage(Long appId, Long userId, String message) {
        return saveMessage(appId, userId, message, MessageTypeEnum.AI.getValue());
    }

    @Override
    public boolean saveErrorMessage(Long appId, Long userId, String errorMessage) {
        return saveMessage(appId, userId, errorMessage, MessageTypeEnum.ERROR.getValue());
    }

    /**
     * 保存消息的通用方法
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param message 消息内容
     * @param messageType 消息类型
     * @return 是否保存成功
     */
    private boolean saveMessage(Long appId, Long userId, String message, String messageType) {
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "用户ID不能为空");
        }
        if (StrUtil.isBlank(message)) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "消息内容不能为空");
        }
        
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        return this.save(chatHistory);
    }

    @Override
    public List<ChatHistoryVO> getAppChatHistory(Long appId, LocalDateTime localDateTime, long pageSize) {
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("createTime", false)
                .limit(pageSize); // 按时间降序
        if(localDateTime != null){
            queryWrapper.lt("createTime",localDateTime);
        }
        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        List<ChatHistory> sortedList = chatHistoryList.stream()
                .sorted(Comparator.comparing(ChatHistory::getCreateTime))
                .collect(Collectors.toList());
        return getChatHistoryVOList(sortedList);
    }

    @Override
    public List<ChatHistoryVO> getLatestAppChatHistory(Long appId, int limit) {
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("createTime", false) // 按时间降序
                .limit(limit);
        
        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        return getChatHistoryVOList(chatHistoryList);
    }

    @Override
    public List<ChatHistoryVO> getAllChatHistoryForAdmin(long current, long pageSize) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .orderBy("createTime", false); // 按时间降序
        
        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        return getChatHistoryVOList(chatHistoryList);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId);
        
        return this.remove(queryWrapper);
    }

    @Override
    public boolean hasPermissionToViewChatHistory(Long appId, User loginUser) {
        if (appId == null || appId <= 0) {
            throw new BussessException(ErrorCode.PARAMS_ERROR.getCode(), "应用ID不能为空");
        }
        if (loginUser == null) {
            throw new BussessException(ErrorCode.NOT_LOGIN_ERROR.getCode(), "用户未登录");
        }
        
        // 管理员可以查看所有对话历史
        if (UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            return true;
        }
        
        // 查询应用信息
        App app = appService.getById(appId);
        if (app == null) {
            throw new BussessException(ErrorCode.NOT_FOUND_ERROR.getCode(), "应用不存在");
        }
        
        // 只有应用创建者可以查看对话历史
        return app.getUserId().equals(loginUser.getId());
    }


    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 直接构造查询条件，起始点为 1 而不是 0，用于排除最新的用户消息
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount);
            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }
            // 反转列表，确保按时间正序（老的在前，新的在后）
          //  historyList = historyList.reversed();
            Collections.reverse(historyList);
            // 按时间顺序添加到记忆中
            int loadedCount = 0;
            // 先清理历史缓存，防止重复加载
            chatMemory.clear();
            for (ChatHistory history : historyList) {
                if (MessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadedCount++;
                } else if (MessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadedCount++;
                }
            }
            log.info("成功为 appId: {} 加载了 {} 条历史对话", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            // 加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }

}
