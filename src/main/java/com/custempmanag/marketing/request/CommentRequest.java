package com.custempmanag.marketing.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "{comment.notblank}")
    private String content;
}
