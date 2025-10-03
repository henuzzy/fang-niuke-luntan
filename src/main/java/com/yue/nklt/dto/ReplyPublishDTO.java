package com.yue.nklt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发布回复DTO
 */
@Data
public class ReplyPublishDTO {
    @NotBlank(message = "回复内容不能为空")
    private String content;

    /**
     * 被回复的目标用户，可为空
     */
    private Integer targetId;
}


