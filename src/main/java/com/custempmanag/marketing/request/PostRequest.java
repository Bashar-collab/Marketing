package com.custempmanag.marketing.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    private Long offeringId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title of the post must be between 3 and 50 characters")
    private String title;

    private String description;

    @NotBlank(message = "Content is required")
    @Size(min = 3, message = "Content must be at least 3 characters")
    private String content;

}
