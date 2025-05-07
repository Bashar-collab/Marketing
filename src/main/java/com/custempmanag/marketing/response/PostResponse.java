package com.custempmanag.marketing.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostResponse {
    private Long id;

    private Long offeringId;

    private String title;

    private String description;

    private String content;
}
