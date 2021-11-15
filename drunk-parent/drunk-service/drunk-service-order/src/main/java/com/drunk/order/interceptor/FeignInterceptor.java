package com.drunk.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Feign拦截器，每次微服务调用时拦截请求将Authorization令牌等信息添加到头文件中，再调用其他微服务
 */
@Configuration
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        try{
            //使用上下文获取request中相关变量
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if(requestAttributes!=null){
                HttpServletRequest request = requestAttributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if(headerNames!=null){
                    while(headerNames.hasMoreElements()){
                        //头文件key
                        String name = headerNames.nextElement();
                        //头文件value
                        String value = request.getHeader(name);
                        //将令牌等数据添加到头文件中
                        requestTemplate.header(name,value);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
