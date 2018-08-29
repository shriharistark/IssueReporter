package com.example.Springgaejdohello.Service.Auth.Google;

public class AuthObjectBuilder {

    //mandatory attributes common to request and response objects
    String client_id;
    String redirect_uri;
    String scope;

    String application_name;
    String client_secret;
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

    //mandatory parameters
    public AuthObjectBuilder(String client_id, String redirect_uri, String scope){
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.scope = scope;
    }

    //building parameters
    public AuthObjectBuilder setApplication_name(String application_name){
        this.application_name = application_name;
        return this;
    }

    public AuthObjectBuilder setClient_secret(String client_secret){
        this.client_secret = client_secret;
        return this;
    }

    public AuthObjectBuilder setState(String stateToken){
        this.state = stateToken;
        return this;
    }

    public AuthObjectBuilder setResponse_type(String response_type){
        this.response_type = response_type;
        return this;
    }

    public AuthObjectBuilder setLogin_hint(String login_hint){
        this.login_hint = login_hint;
        return this;
    }

    public AuthObjectBuilder setCode(String code){
        this.code = code;
        return this;
    }

    public AuthObjectBuilder setGrant_type(String grant_type){
        this.grant_type = grant_type;
        return this;
    }

    public AuthObjectBuilder setAccess_token(String access_token){
        this.access_token = access_token;
        return this;
    }

    public AuthObjectBuilder setRefresh_token(String refresh_token){
        this.refresh_token = refresh_token;
        return this;
    }

    public AuthObjectBuilder setId_token(String id_token){
        this.id_token = id_token;
        return this;
    }

    public AuthObjectBuilder setToken_type(String token_type){
        this.token_type = token_type;
        return this;
    }

    public AuthObjectBuilder setExpires_in(String expires_in){
        this.expires_in = expires_in;
        return this;
    }

    public AuthObject build(){
        if(this.client_id != null && this.scope != null && this.redirect_uri != null) {
            return new AuthObject(this.client_id, this.application_name,
                    this.client_secret, this.redirect_uri, this.scope,
                    this.state, this.response_type,
                    this.login_hint, this.code, this.grant_type, this.access_token,
                    this.id_token, this.refresh_token, this.token_type,this.expires_in);
        }
        else {
            return null;
        }
    }

}
