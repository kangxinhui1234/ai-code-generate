package com.codegenerate.aicodegenerate.service.impl;



import com.codegenerate.aicodegenerate.entity.ChatHistory;
import com.codegenerate.aicodegenerate.mapper.ChatHistoryMapper;
import com.codegenerate.aicodegenerate.service.ChatHistoryService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author kxh
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {


    public ChatHistoryMapper getMapper(){
        return null;
    }


}
