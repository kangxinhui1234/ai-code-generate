package com.codegenerate.aicodegenerate.common;

import lombok.Data;

/**
 * 分页
 */
@Data
public class PageRequest {

    long pageSize;
    long pageNum;

    String sortField;
    String sortOrder;
}
