package com.custempmanag.marketing.response;

import lombok.Data;

@Data
public class ImageResponse {
    private Long id;
    private String url;

    public ImageResponse() {
    }

    public ImageResponse(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
