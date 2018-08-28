package com.example.Springgaejdohello.Service.Utils;

public class AuthRequestObj {

    private String authCode="";
    private String clientId="126208571601-qoufn463ar3hd0hke7a3ql8q9uhehqpv.apps.googleusercontent.com";
    private String clientSecret="Csc753n5yPOjLVceLcoEc4Sr";
    private String grantType = "authorization_code";
    private String headers="";
    private String redirect_uri="http://localhost:8080/auth/google";

    public AuthRequestObj(){

    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }
}
