package com.drunk.oauth.service;

import com.drunk.oauth.util.AuthToken;

public interface AuthService {

    /**
     * 认证授权方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    AuthToken login(String username,String password,String clientId,String clientSecret);
}
