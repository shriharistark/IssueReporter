package com.example.Springgaejdohello.Service;

import com.example.Springgaejdohello.Service.Utils.AuthRequestObj;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class GAuth {

    //API key = AIzaSyDne8TCsPFCKbex6omfrB-NUA-r6Y5NtkQ
    //https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/calendar&access_type=offline&redirect_uri=http://localhost:8080/auth/google&response_type=code&client_id=126208571601-5hnok0ie3hhr7mmcn0hshgl3kvhdd25h.apps.googleusercontent.com

    //https://accounts.google.com/o/oauth2/v2/auth?client_id=126208571601-5hnok0ie3hhr7mmcn0hshgl3kvhdd25h.apps.googleusercontent.com&response_type=code&scope=https://www.googleapis.com/auth/calendar&redirect_uri=http://localhost:8080/auth/google&access_type=offline

    //calendar v3
    //https://accounts.google.com/o/oauth2/v2/auth?client_id=126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com&response_type=code&scope=https://www.googleapis.com/auth/calendar&redirect_uri=http://localhost:8080/auth/google&access_type=offline&approval_prompt=force

    //authCode = 4/SABVziz4kvOgqHmiCFxp7DQvXrs471iWp_4vmoLmJDKQZIGkSdDRmsmibrbyKN7yiyyowimX7Bunga0J6hDJq8A
    private static final String client_secret = "fwMZPTYbTp-9hDjuaBrETEdO";
    private static final String client_id = "126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com";

    //Request container
    //clientID
    //response_type - code
    //scope - gmail/calendar etc..
    //redirect_uri - callback uri which appends the authorizationCode
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public @ResponseBody String makeAuth(@RequestParam("code") String authCode){

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("code",authCode);
        params.add("client_id",client_id);
        params.add("client_secret",client_secret);
        params.add("grant_type","authorization_code");
        params.add("redirect_uri","http://localhost:8080/auth/google");

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String, String>>(params,headers);
        System.out.print("\nAuthcode: "+authCode+"\n");

        AuthResponse responseBody = restTemplate
                .postForObject(
                "https://www.googleapis.com/oauth2/v4/token"
                ,request
                ,AuthResponse.class);

        System.out.print("\nResponse: "+responseBody.toString()+"\n");


        ObjectMapper resp = new ObjectMapper();
        HashMap<String,String> responseMap = new HashMap<>();
        String response = "";
        try {
            if(responseBody != null) {
                responseMap.put("refreshToken", responseBody.getRefresh_token());
                responseMap.put("accessToken", responseBody.getAccess_token());
                responseMap.put("expires in", responseBody.getExpires_in());
            }
            response = resp.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            response = "Some error occured during parsing";
            e.printStackTrace();
        } catch (NullPointerException n){
            response = "Some error occured NPE";
            n.printStackTrace();
        }

        return response;
    }

    //refresh token = 1/U7lGGt2dLzmZqm5uXW6Zw9RjEVLZ3Ljsvqze57HxPO7piOy7-5CxgmBRYztKz0ew

    //calendar v3
    //refresh token = 1/rHPPG4SqmVfSdwCGj5s1RhstcwLkDHOelAZcU_xQzB4
    //harihaan.anand refresh token = 1/ujeT-q-2ec1b8MxcIrpTOVnw7yNhhVp76kjDdMoAPvw


    //finally done - refresh token -> 1/H23d9dZiTnJEhyySKU0_P12LKh-UIC0m_HV3FdkB24jwVdLdEu4oDzW40sfTHTYp
    // | access token -> ya29.GlsGBsgu4ovVQgNWKyhjSagU8l2WKeJHfwl8iv02qkvBWByaJQa-QcsR6y_CH0LShGiNh3waKQuPmAnXgLfzFoDsOz1gR5IEMBgSBhPoeZlBPR1UtGApDlywYxE5

    @RequestMapping(value = "/google/getaccesstoken",method = RequestMethod.GET)
    public @ResponseBody String getAccessToken(@RequestParam(value = "refresh_token") String refresh_token) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String,Object> requestObj = mapper.readValue(body,new TypeReference<Map<String, Object>>() {});
//
//        String refresh_token = requestObj.get("refresh_token").toString();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id",client_id);
        params.add("client_secret",client_secret);
        params.add("refresh_token",refresh_token);
        params.add("grant_type","refresh_token");

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String, String>>(params,headers);

        AuthResponse responseBody = restTemplate.postForObject(
                "https://www.googleapis.com/oauth2/v4/token"
                ,request
                ,AuthResponse.class);

        //writing back the response

        ObjectMapper resp = new ObjectMapper();
        HashMap<String,String> responseMap = new HashMap<>();
        String response = "";
        System.out.println(responseBody.toString());

        try {
            if(responseBody != null) {
                responseMap.put("access_token", responseBody.getAccess_token());
                responseMap.put("expires_in", responseBody.getExpires_in());
                responseMap.put("token_type",responseBody.getToken_type());
            }
            response = resp.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            response = "Some error occured during parsing";
            e.printStackTrace();
        } catch (NullPointerException n){
            response = "Some error occured NPE";
            n.printStackTrace();
        }

        return response;

    }

}
