# ChatHistory 模块使用说明

## 概述

ChatHistory 模块提供了完整的对话历史管理功能，包括消息的持久化存储、应用级别的数据隔离、对话历史查询和管理功能。

## 功能特性

### 1. 对话历史的持久化存储
- 支持保存用户消息、AI回复和错误信息
- 确保对话的完整性，即使AI回复失败也会记录错误信息

### 2. 应用级别的数据隔离
- 每个应用的对话历史都是独立的
- 删除应用时会关联删除该应用的所有对话历史

### 3. 对话历史查询
- 支持分页查看某个应用的对话历史
- 区分用户和AI消息
- 支持向前加载更多历史记录
- 仅应用创建者和管理员可见

### 4. 管理对话历史
- 管理员可以查看所有应用的对话历史
- 按照时间降序排序，便于内容监管

## API 接口

### 基础 CRUD 操作

#### 保存对话历史
```http
POST /chatHistory/save
Content-Type: application/json

{
  "message": "用户消息内容",
  "messageType": "user",
  "appId": 123,
  "userId": 456
}
```

#### 查询对话历史
```http
GET /chatHistory/getInfo/{id}
```

#### 分页查询对话历史
```http
POST /chatHistory/page
Content-Type: application/json

{
  "current": 1,
  "pageSize": 10,
  "appId": 123,
  "messageType": "user"
}
```

### 业务功能接口

#### 保存用户消息
```http
POST /chatHistory/saveUserMessage?appId=123&message=用户消息内容
```

#### 保存AI回复消息
```http
POST /chatHistory/saveAiMessage?appId=123&message=AI回复内容
```

#### 保存错误消息
```http
POST /chatHistory/saveErrorMessage?appId=123&errorMessage=错误信息
```

#### 获取应用的最新对话历史（用于初始化）
```http
GET /chatHistory/app/{appId}/latest?limit=10
```

#### 分页查询应用的对话历史
```http
GET /chatHistory/app/{appId}?current=1&pageSize=10
```

#### 管理员查询所有对话历史
```http
GET /chatHistory/admin/all?current=1&pageSize=20
```

#### 删除应用的所有对话历史
```http
DELETE /chatHistory/app/{appId}
```

## 消息类型枚举

```java
public enum MessageTypeEnum {
    USER("用户消息", "user"),
    AI("AI回复", "ai"),
    ERROR("错误信息", "error");
}
```

## 使用示例

### 在 AppService 中集成对话历史

```java
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    
    @Autowired
    private ChatHistoryService chatHistoryService;
    
    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 保存用户消息
        chatHistoryService.saveUserMessage(appId, loginUser.getId(), message);
        
        // 2. 调用AI生成代码
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId)
            .doOnNext(aiResponse -> {
                // 3. 保存AI回复
                chatHistoryService.saveAiMessage(appId, loginUser.getId(), aiResponse);
            })
            .doOnError(error -> {
                // 4. 保存错误信息
                chatHistoryService.saveErrorMessage(appId, loginUser.getId(), error.getMessage());
            });
    }
    
    @Override
    public boolean deleteApp(Long appId, User loginUser) {
        // 1. 验证权限
        App app = this.getById(appId);
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BussessException(ErrorCode.NO_AUTH_ERROR.getCode(), "无权限删除该应用");
        }
        
        // 2. 删除关联的对话历史
        chatHistoryService.deleteByAppId(appId);
        
        // 3. 删除应用
        return this.removeById(appId);
    }
}
```

### 前端初始化应用页面

```javascript
// 进入应用页面时，先加载对话历史
async function initAppPage(appId) {
    try {
        // 1. 获取最新10条对话历史
        const response = await fetch(`/chatHistory/app/${appId}/latest?limit=10`);
        const result = await response.json();
        
        if (result.code === 0 && result.data.length > 0) {
            // 2. 如果有历史记录，直接展示
            displayChatHistory(result.data);
        } else {
            // 3. 如果没有历史记录，发送初始化提示词
            sendInitPrompt(appId);
        }
    } catch (error) {
        console.error('加载对话历史失败:', error);
        // 发生错误时也发送初始化提示词
        sendInitPrompt(appId);
    }
}
```

## 权限控制

- **应用创建者**：可以查看、管理自己应用的对话历史
- **管理员**：可以查看所有应用的对话历史
- **普通用户**：只能查看自己创建的应用的对话历史

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

1. 所有接口都需要用户登录认证
2. 管理员接口需要管理员权限
3. 删除应用时会级联删除对话历史
4. 对话历史按时间降序排列，最新的消息在前
5. 支持逻辑删除，不会物理删除数据
6. 批量查询时使用 N+1 查询优化，避免性能问题
