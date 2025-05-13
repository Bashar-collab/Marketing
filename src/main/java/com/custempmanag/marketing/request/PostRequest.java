package com.custempmanag.marketing.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    private Long offeringId;

    @NotBlank(message = "{post.title.notblank}")
    @Size(min = 3, max = 50, message = "{post.title.size}")
    private String title;

    private String description;

    @NotBlank(message = "{post.content.notblank}")
    @Size(min = 3, message = "{post.content.size}")
    private String content;

}
