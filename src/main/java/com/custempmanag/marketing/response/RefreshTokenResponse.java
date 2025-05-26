package com.custempmanag.marketing.response;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;

    public RefreshTokenResponse() {
    }

    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
