# AppController 中集成 ChatHistory 功能的使用说明

## 概述

现在 `AppController.java` 中的 `chatToGenCode` 方法已经集成了完整的消息保存功能，包括：

1. **用户消息保存**：接收到用户消息时立即保存到数据库
2. **AI回复保存**：AI生成消息结束后把消息落库
3. **错误信息保存**：AI生成失败时记录错误信息
4. **消息类型区分**：使用 `MessageTypeEnum` 区分 USER、AI、ERROR 三种消息类型

## 修改后的 chatToGenCode 方法流程

### 1. 用户消息保存
```java
// 4. 保存用户消息到对话历史
try {
    chatHistoryService.saveUserMessage(appId, loginUser.getId(), message);
} catch (Exception e) {
    log.error("保存用户消息失败: {}", e.getMessage());
    // 保存失败不影响主流程，继续执行
}
```

### 2. AI回复流式保存
```java
// 6. 调用 AI 生成代码（流式）
return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId)
        .doOnNext(aiResponse -> {
            // 7. 保存AI回复到对话历史
            try {
                chatHistoryService.saveAiMessage(appId, loginUser.getId(), aiResponse);
            } catch (Exception e) {
                log.error("保存AI回复失败: {}", e.getMessage());
                // 保存失败不影响主流程
            }
        })
        .doOnError(error -> {
            // 8. 保存错误信息到对话历史
            try {
                String errorMessage = "AI生成代码失败: " + error.getMessage();
                chatHistoryService.saveErrorMessage(appId, loginUser.getId(), errorMessage);
            } catch (Exception e) {
                log.error("保存错误信息失败: {}", e.getMessage());
            }
        });
```

## 消息类型说明

### MessageTypeEnum 枚举
```java
public enum MessageTypeEnum {
    USER("用户消息", "user"),    // 用户发送的消息
    AI("AI回复", "ai"),        // AI生成的回复
    ERROR("错误信息", "error");  // 错误信息
}
```

### 数据库存储示例
```sql
-- 用户消息
INSERT INTO chat_history (message, messageType, appId, userId, createTime) 
VALUES ('请帮我生成一个登录页面', 'user', 123, 456, NOW());

-- AI回复
INSERT INTO chat_history (message, messageType, appId, userId, createTime) 
VALUES ('<html><body>登录页面代码...</body></html>', 'ai', 123, 456, NOW());

-- 错误信息
INSERT INTO chat_history (message, messageType, appId, userId, createTime) 
VALUES ('AI生成代码失败: 网络连接超时', 'error', 123, 456, NOW());
```

## 应用删除时的级联删除

### 新增的 deleteApp 方法
```java
@Override
public boolean deleteApp(Long appId, User loginUser) {
    // 1. 参数校验和权限验证
    // 2. 删除关联的对话历史
    try {
        chatHistoryService.deleteByAppId(appId);
        log.info("成功删除应用 {} 的对话历史", appId);
    } catch (Exception e) {
        log.error("删除应用 {} 的对话历史失败: {}", appId, e.getMessage());
    }
    
    // 3. 删除应用
    boolean result = this.removeById(appId);
    return result;
}
```

## 前端调用示例

### 1. 发送消息生成代码
```javascript
// 前端发送消息
const response = await fetch(`/app/chat/gen/code?appId=123&message=请生成一个登录页面`, {
    method: 'GET',
    headers: {
        'Accept': 'text/event-stream',
        'Cache-Control': 'no-cache'
    }
});

// 处理流式响应
const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    
    const chunk = decoder.decode(value);
    const lines = chunk.split('\n');
    
    for (const line of lines) {
        if (line.startsWith('data: ')) {
            const data = line.slice(6);
            if (data.trim()) {
                const jsonData = JSON.parse(data);
                // 显示AI回复内容
                displayAiResponse(jsonData.d);
            }
        } else if (line.startsWith('event: done')) {
            // AI生成完成
            console.log('AI生成完成');
        }
    }
}
```

### 2. 查询对话历史
```javascript
// 查询应用的最新对话历史
const historyResponse = await fetch(`/chatHistory/app/123/latest?limit=10`);
const historyData = await historyResponse.json();

if (historyData.code === 0) {
    const chatHistory = historyData.data;
    // 显示对话历史
    displayChatHistory(chatHistory);
}
```

## 错误处理机制

### 1. 消息保存失败处理
- 用户消息保存失败：记录日志，但不影响AI生成流程
- AI回复保存失败：记录日志，但不影响流式输出
- 错误信息保存失败：记录日志

### 2. 日志记录
```java
log.error("保存用户消息失败: {}", e.getMessage());
log.error("保存AI回复失败: {}", e.getMessage());
log.error("保存错误信息失败: {}", e.getMessage());
log.info("成功删除应用 {} 的对话历史", appId);
```

## 数据库表结构

```sql
CREATE TABLE `chat_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `message` text NOT NULL COMMENT '消息内容',
  `messageType` varchar(20) NOT NULL COMMENT '消息类型：user/ai/error',
  `appId` bigint NOT NULL COMMENT '应用id',
  `userId` bigint NOT NULL COMMENT '创建用户id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_appId` (`appId`),
  KEY `idx_userId` (`userId`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话历史表';
```

## 注意事项

1. **异步处理**：消息保存是异步的，不会阻塞AI生成流程
2. **错误容错**：消息保存失败不会影响主要功能
3. **权限控制**：只有应用创建者和管理员可以查看对话历史
4. **数据隔离**：每个应用的对话历史都是独立的
5. **级联删除**：删除应用时会自动删除相关的对话历史

这样就完成了在 `AppController` 中集成 ChatHistory 功能，实现了完整的对话历史管理！




