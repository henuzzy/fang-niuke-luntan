package com.yue.nklt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发布评论DTO
 */
@Data
public class CommentPublishDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
}

