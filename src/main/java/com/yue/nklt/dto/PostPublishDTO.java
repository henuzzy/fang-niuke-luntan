package com.yue.nklt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发布帖子DTO
 */
@Data
public class PostPublishDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}

