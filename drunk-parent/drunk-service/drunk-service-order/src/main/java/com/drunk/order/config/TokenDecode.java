package com.drunk.order.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenDecode {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    private static String publickey = "";

    /**
     * 获取公钥
     * @return
     */
    public String getPublicKey(){
        if(!StringUtils.isEmpty(publickey)){
            return publickey;
        }

        ClassPathResource resource = new ClassPathResource(PUBLIC_KEY);
        try{
            InputStreamReader isr = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            publickey = br.lines().collect(Collectors.joining("\n"));
            return publickey;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析令牌
     * @param token
     * @return
     */
    public Map<String,String> decodeToken(String token){
        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(getPublicKey()));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        return JSON.parseObject(claims,Map.class);
    }

    /**
     * 获取用户信息
     * @return
     */
    public Map<String,String> getUserInfo(){
        //获取授权信息
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        //令牌解码
        return decodeToken(details.getTokenValue());
    }
}
