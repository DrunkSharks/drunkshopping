package com.drunk.gateway.filter;

import com.drunk.gateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    //用户登录地址
    private static final String USER_LOGIN_URL = "http://localhost:8001/drunk/login";

    /**
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求URI
        String path = request.getURI().getPath();

        //登录服务直接放行
        if(URLFilter.hasAuthorize(path)){
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }

        //获取头文件中的令牌信息
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);

        //如果头文件中没有，则从请求参数中获取
        if(StringUtils.isEmpty(token)){
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        //如果请求中没有，从cookie中获取
        if(StringUtils.isEmpty(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if(cookie!=null){
                token = cookie.getValue();
            }
        }
        System.out.println(token);

        //如果为空，跳转到登录界面
        if(StringUtils.isEmpty(token)){
            /*response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();*/
            return needAuthroization(USER_LOGIN_URL+"?from="+request.getURI().toString(),exchange);
        }

        //解析令牌信息
        try{
            /*Claims claims = JwtUtils.parseSecretKey(token);
            //将令牌添加到请求头中
            request.mutate().header(AUTHORIZE_TOKEN,claims.toString());*/

            //将使用oauth2.0生成的令牌添加到请求头中
            request.mutate().header(AUTHORIZE_TOKEN,"bearer "+token);
        }catch(Exception e){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //token验证成功，则放行
        return chain.filter(exchange);
    }

    /**
     * 响应设置
     * @param url
     * @param exchange
     * @return
     */
    public Mono<Void> needAuthroization(String url,ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",url);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
