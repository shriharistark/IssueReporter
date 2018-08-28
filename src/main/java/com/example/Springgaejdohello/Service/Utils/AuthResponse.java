package com.example.Springgaejdohello.Service.Utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    String access_token;
    String refresh_token;
    String expires_in;
    String token_type;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return this.token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    @Override
    public String toString() {
        return "AuthResponse [" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", token_type='" + token_type + '\'' +
                ']';
    }
}
