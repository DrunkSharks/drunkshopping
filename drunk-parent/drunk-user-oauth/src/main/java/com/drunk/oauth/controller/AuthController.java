package com.drunk.oauth.controller;

import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.oauth.service.AuthService;
import com.drunk.oauth.util.AuthToken;
import com.drunk.oauth.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/oauth")
public class AuthController {

    //客户端ID
    @Value("${auth.clientId}")
    private String clientId;

    //客户端密钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    //cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result login(String username, String password, HttpServletRequest request){
        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("账号不能为空");
        }

        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空");
        }

        Map<String, String> cookies = CookieUtil.readCookie(request, "Authorization");
        System.out.println(cookies.get("Authorization"));

        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        //获取用户令牌
        String accessToken = authToken.getAccessToken();
        //将令牌保存到cookie中
        saveCookie(accessToken);
        return new Result(true, StatusCode.OK,"登录成功...");
    }


    /**
     * 将令牌存储到Cookie中
     * @param accessToken
     */
    private void saveCookie(String accessToken){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",accessToken,cookieMaxAge,false);
    }

}
