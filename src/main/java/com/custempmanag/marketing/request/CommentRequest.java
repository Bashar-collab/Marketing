package com.custempmanag.marketing.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment must not be blank")
    private String content;
}
