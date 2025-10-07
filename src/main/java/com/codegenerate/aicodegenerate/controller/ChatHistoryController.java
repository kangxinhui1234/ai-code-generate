package com.codegenerate.aicodegenerate.controller;

import com.codegenerate.aicodegenerate.annotation.AuthCheck;
import com.codegenerate.aicodegenerate.constants.UserConstant;
import com.codegenerate.aicodegenerate.entity.ChatHistory;
import com.codegenerate.aicodegenerate.entity.User;
import com.codegenerate.aicodegenerate.exception.ErrorCode;
import com.codegenerate.aicodegenerate.model.dto.chat.ChatHistoryQueryRequest;
import com.codegenerate.aicodegenerate.model.user_enum.UserRoleEnum;
import com.codegenerate.aicodegenerate.model.vo.ChatHistoryVO;
import com.codegenerate.aicodegenerate.response.BaseResponse;
import com.codegenerate.aicodegenerate.response.ResultUtil;
import com.codegenerate.aicodegenerate.service.ChatHistoryService;
import com.codegenerate.aicodegenerate.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author kxh
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private UserService userService;

    /**
     * 保存对话历史。
     *
     * @param chatHistory 对话历史
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/save")
    public BaseResponse<Boolean> save(@RequestBody ChatHistory chatHistory) {
        boolean result = chatHistoryService.save(chatHistory);
        return ResultUtil.sucess(result);
    }

    /**
     * 根据主键删除对话历史。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/remove/{id}")
    public BaseResponse<Boolean> remove(@PathVariable Long id) {
        boolean result = chatHistoryService.removeById(id);
        return ResultUtil.sucess(result);
    }

    /**
     * 根据主键更新对话历史。
     *
     * @param chatHistory 对话历史
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody ChatHistory chatHistory) {
        boolean result = chatHistoryService.updateById(chatHistory);
        return ResultUtil.sucess(result);
    }

    /**
     * 查询所有对话历史。
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    public BaseResponse<List<ChatHistoryVO>> list() {
        List<ChatHistory> chatHistoryList = chatHistoryService.list();
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getChatHistoryVOList(chatHistoryList);
        return ResultUtil.sucess(chatHistoryVOList);
    }

    /**
     * 根据主键获取对话历史。
     *
     * @param id 对话历史主键
     * @return 对话历史详情
     */
    @GetMapping("/getInfo/{id}")
    public BaseResponse<ChatHistoryVO> getInfo(@PathVariable Long id) {
        ChatHistory chatHistory = chatHistoryService.getById(id);
        ChatHistoryVO chatHistoryVO = chatHistoryService.getChatHistoryVO(chatHistory);
        return ResultUtil.sucess(chatHistoryVO);
    }

    /**
     * 分页查询对话历史。
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页对象
     */
    @PostMapping("/page")
    public BaseResponse<Page<ChatHistoryVO>> page(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> page = chatHistoryService.page(Page.of(chatHistoryQueryRequest.getPageNum(), chatHistoryQueryRequest.getPageSize()), queryWrapper);
        Page<ChatHistoryVO> chatHistoryVOPage = new Page<>();
        chatHistoryVOPage.setPageNumber(page.getPageNumber());
        chatHistoryVOPage.setPageSize(page.getPageSize());
        chatHistoryVOPage.setTotalRow(page.getTotalRow());
        chatHistoryVOPage.setRecords(chatHistoryService.getChatHistoryVOList(page.getRecords()));
        return ResultUtil.sucess(chatHistoryVOPage);
    }

    /**
     * 保存用户消息
     *
     * @param appId 应用ID
     * @param message 消息内容
     * @param request HTTP请求
     * @return 是否保存成功
     */
    @PostMapping("/saveUserMessage")
    public BaseResponse<Boolean> saveUserMessage(@RequestParam Long appId, 
                                               @RequestParam String message, 
                                               HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = chatHistoryService.saveUserMessage(appId, loginUser.getId(), message);
        return ResultUtil.sucess(result);
    }

    /**
     * 保存AI回复消息
     *
     * @param appId 应用ID
     * @param message 消息内容
     * @param request HTTP请求
     * @return 是否保存成功
     */
    @PostMapping("/saveAiMessage")
    public BaseResponse<Boolean> saveAiMessage(@RequestParam Long appId, 
                                             @RequestParam String message, 
                                             HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = chatHistoryService.saveAiMessage(appId, loginUser.getId(), message);
        return ResultUtil.sucess(result);
    }

    /**
     * 保存错误消息
     *
     * @param appId 应用ID
     * @param errorMessage 错误消息内容
     * @param request HTTP请求
     * @return 是否保存成功
     */
    @PostMapping("/saveErrorMessage")
    public BaseResponse<Boolean> saveErrorMessage(@RequestParam Long appId, 
                                                 @RequestParam String errorMessage, 
                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = chatHistoryService.saveErrorMessage(appId, loginUser.getId(), errorMessage);
        return ResultUtil.sucess(result);
    }

    /**
     * 分页查询应用的对话历史
     *
     * @param appId 应用ID
     * @param current 当前页
     * @param pageSize 页大小
     * @param request HTTP请求
     * @return 对话历史列表
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<List<ChatHistoryVO>> getAppChatHistory(@PathVariable Long appId,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                               LocalDateTime localDateTime,
                                                               @RequestParam(defaultValue = "10") long pageSize,
                                                               HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

////        if(StringUtils.isNotEmpty(localDateTime)){
////            LocalDateTime localDateTime1 = LocalDateTime.parse(localDateTime);
////        }
//        // 验证权限
        boolean hasPermission = chatHistoryService.hasPermissionToViewChatHistory(appId, loginUser);
        if (!hasPermission) {
            return ResultUtil.error(ErrorCode.CHATHISTORY_QUERY_ERROR);
        }
        
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getAppChatHistory(appId, localDateTime, pageSize);
        return ResultUtil.sucess(chatHistoryVOList);
    }

    /**
     * 获取应用的最新对话历史（用于初始化）
     *
     * @param appId 应用ID
     * @param limit 限制条数
     * @param request HTTP请求
     * @return 对话历史列表
     */
    @GetMapping("/app/{appId}/latest")
    public BaseResponse<List<ChatHistoryVO>> getLatestAppChatHistory(@PathVariable Long appId,
                                                                    @RequestParam(defaultValue = "10") int limit,
                                                                    HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 验证权限
        boolean hasPermission = chatHistoryService.hasPermissionToViewChatHistory(appId, loginUser);
        if (!hasPermission) {
            return ResultUtil.error(ErrorCode.CHATHISTORY_QUERY_ERROR);
        }
        
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getLatestAppChatHistory(appId, limit);
        return ResultUtil.sucess(chatHistoryVOList);
    }

    /**
     * 管理员查询所有对话历史
     *
     * @param current 当前页
     * @param pageSize 页大小
     * @param request HTTP请求
     * @return 对话历史列表
     */
    @GetMapping("/admin/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<ChatHistoryVO>> getAllChatHistoryForAdmin(@RequestParam(defaultValue = "1") long current,
                                                                     @RequestParam(defaultValue = "20") long pageSize,
                                                                     HttpServletRequest request) {
        List<ChatHistoryVO> chatHistoryVOList = chatHistoryService.getAllChatHistoryForAdmin(current, pageSize);
        return ResultUtil.sucess(chatHistoryVOList);
    }

    /**
     * 删除应用的所有对话历史
     *
     * @param appId 应用ID
     * @param request HTTP请求
     * @return 是否删除成功
     */
    @DeleteMapping("/app/{appId}")
    public BaseResponse<Boolean> deleteByAppId(@PathVariable Long appId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 验证权限 - 只有应用创建者或管理员可以删除
        boolean hasPermission = chatHistoryService.hasPermissionToViewChatHistory(appId, loginUser);
        if (!hasPermission) {
            return ResultUtil.error(ErrorCode.CHATHISTORY_QUERY_ERROR);
        }
        
        boolean result = chatHistoryService.deleteByAppId(appId);
        return ResultUtil.sucess(result);
    }
}
