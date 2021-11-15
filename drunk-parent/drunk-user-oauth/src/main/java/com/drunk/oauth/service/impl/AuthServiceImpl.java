package com.drunk.oauth.service.impl;

import com.drunk.oauth.service.AuthService;
import com.drunk.oauth.util.AuthToken;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 认证授权方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //申请令牌
        AuthToken authToken = applyToken(username,password,clientId,clientSecret);
        if(authToken==null){
            throw new RuntimeException("申请令牌失败");
        }
        return authToken;
    }

    /**
     * 用户认证
     * @param username  用户名称
     * @param password  用户密码
     * @param clientId  客户端ID
     * @param clientSecret  客户端密钥
     * @return
     */
    private AuthToken applyToken(String username,String password,String clientId,String clientSecret){
        //选中认证服务地址
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        if(serviceInstance==null){
            throw new RuntimeException("未找到对应的服务");
        }
        //获取令牌认证url
        String path = serviceInstance.getUri().toString()+"/oauth/token";
        System.out.println(path);
        //定义body
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type","password");
        formData.add("username",username);
        formData.add("password",password);
        //定义头 传输格式Basic ZHJ1bms6ZHJ1bms=  (Basic 客户端ID(Base64加密):客户端密钥(Base64加密))
        MultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        header.add("Authorization",httpbasic(clientId,clientSecret));

        //指定restTemplate当遇到400或401相应时候不抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当相应的值为400或401时候也要正常相应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });

        Map map = null;
        try{
            //restTemplate支持服务器内302重定向
            /*final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            final HttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();
            factory.setHttpClient(httpClient);
            restTemplate.setRequestFactory(factory);*/
            //http请求spring security的申请令牌接口
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            //获取相应数据
            map = mapResponseEntity.getBody();
        }catch(RestClientException e){
            throw new RuntimeException(e);
        }
        if(map==null || map.get("access_token")==null || map.get("refresh_token")==null || map.get("jti")==null){
            //jti是jwt令牌的唯一标识作为用户身份令牌
            throw new RuntimeException("创建令牌失败");
        }

        //将影响数据封装成AuthToken对象
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String accessToken = (String)map.get("access_token");
        System.out.println(accessToken);
        //刷新令牌(jwt)
        String refreshToken = (String) map.get("refresh_token");
        //jti，用户唯一身份标识
        String jti = (String) map.get("jti");
        authToken.setAccessToken(accessToken);
        authToken.setRefreshToken(refreshToken);
        authToken.setJti(jti);

        return authToken;
    }

    /**
     * base64加密
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpbasic(String clientId,String clientSecret){
        //将客户端Id和客户端密码拼接，“客户端id:客户端密码”
        String string = clientId + ":" + clientSecret;
        //base64加密
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }

}
