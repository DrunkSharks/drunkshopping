package com.drunk.pay.feign;

import com.drunk.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("pay")
@RequestMapping(value = "/weixin/pay")
public interface WeixinPayFeign {

    /**
     * 关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/closePay")
    public Result closePay(@RequestParam Long orderId) throws Exception;
}
