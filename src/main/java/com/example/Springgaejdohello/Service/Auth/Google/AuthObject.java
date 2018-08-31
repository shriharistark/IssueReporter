package com.example.Springgaejdohello.Service.Auth.Google;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true )
public class AuthObject {

    String client_id;
    String application_name;
    String client_secret;
    String redirect_uri;
    String scope;
    String state;
    String response_type;
    String login_hint;
    String code;
    String grant_type;

    String access_token;
    String id_token;            //id_token is jwt - parse it later
    String refresh_token;
    String token_type;
    String expires_in;

//    public AuthObject(String client_id, String redirect_uri, String scope){
//        this.client_id = client_id;
//        this.redirect_uri = redirect_uri;
//        this.scope = scope;
//    }

    public AuthObject(String client_id, String application_name, String client_secret,
                      String redirect_uri, String scope, String state, String response_type,
                      String login_hint, String code, String grant_type, String access_token,
                      String id_token, String refresh_token, String token_type,String expires_in) {

        this.client_id = client_id;
        this.application_name = application_name;
        this.client_secret = client_secret;
        this.redirect_uri = redirect_uri;
        this.scope = scope;
        this.state = state;
        this.response_type = response_type;
        this.login_hint = login_hint;
        this.code = code;
        this.grant_type = grant_type;
        this.access_token = access_token;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    public AuthObject() {
    }

    /* Auth response Mapper */
    @JsonCreator
    public AuthObject(
            @JsonProperty("access_token") String access_token,
            @JsonProperty(value = "refresh_token", required = false) String refresh_token,
            @JsonProperty("id_token") String id_token,
            @JsonProperty("expires_in") String expires_in,
            @JsonProperty("token_type") String token_type,
            @JsonProperty("scope") String scope
    ){
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.id_token = id_token;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    public String getClient_id() {
        return client_id;
    }
    @JsonSetter
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getApplication_name() {
        return application_name;
    }
    @JsonSetter
    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getClient_secret() {
        return client_secret;
    }
    @JsonSetter
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }
    @JsonSetter
    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getScope() {
        return scope;
    }
    @JsonSetter
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }
    @JsonSetter
    public void setState(String state) {
        this.state = state;
    }

    public String getResponse_type() {
        return response_type;
    }
    @JsonSetter
    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getLogin_hint() {
        return login_hint;
    }
    @JsonSetter
    public void setLogin_hint(String login_hint) {
        this.login_hint = login_hint;
    }

    public String getCode() {
        return code;
    }

    @JsonSetter
    public void setCode(String code) {
        this.code = code;
    }

    public String getGrant_type() {
        return grant_type;
    }
    @JsonSetter
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getAccess_token() {
        return access_token;
    }
    @JsonSetter
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getId_token() {
        return id_token;
    }
    @JsonSetter
    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
    @JsonSetter
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }
    @JsonSetter
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }
    @JsonSetter
    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

}
