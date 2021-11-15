package com.drunk.pay.controller;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/weixin/pay")
@CrossOrigin
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.pay.exchange.order}")
    private String exchange;

    @Value("${mq.pay.queue.order}")
    private String queue;

    @Value("${mq.pay.routing.key}")
    private String rounting;

    /**
     * 关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/closePay")
    public Result closePay(@RequestParam Long orderId) throws Exception{
        Map<String, String> resultMap = weixinPayService.closePay(orderId);
        return new Result(true,StatusCode.OK,"关闭支付成功",resultMap);
    }

    /**
     * 支付回调
     * @param request
     * @return
     */
    @RequestMapping(value="/notify/url")
    public String notifyUrl(HttpServletRequest request){
        InputStream is = null;
        try{
            //读取支付回调数据
            is = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len=is.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            baos.close();
            is.close();

            //将支付回调数据转换成xml字符串
            String result = new String(baos.toByteArray(), "utf-8");
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);

            //获取商家附加信息,其中包含传送队列信息
            Map<String,String> parameters = JSON.parseObject(resultMap.get("attach"),Map.class);
            //修改订单状态，将消息发送给RabbitMQ
            rabbitTemplate.convertAndSend(parameters.get("exchange"),parameters.get("rountingkey"), JSON.toJSONString(resultMap));

            //响应数据设置
            Map respMap = new HashMap();
            respMap.put("return_code","SUCCESS");
            respMap.put("return_msg","OK");
            System.out.println("回调函数执行成功");
            return WXPayUtil.mapToXml(respMap);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 创建二维码
     * @return
     */
    /*@RequestMapping(value = "/create/native")
    public Result createNative(String outtradeno, String money){
        Map<String,String> resultMap = weixinPayService.createNative(outtradeno,money);
        return new Result(true, StatusCode.OK,"创建二维码预付订单成功！",resultMap);
    }*/

    /***
     * 创建二维码
     * @return
     */
    @RequestMapping(value = "/create/native")
    public Result createNative(@RequestParam Map<String,String> parameter){
        Map<String,String> resultMap = weixinPayService.createNative(parameter);
        return new Result(true, StatusCode.OK,"创建二维码预付订单成功！",resultMap);
    }

    /***
     * 查询支付状态
     * @param outtradeno
     * @return
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String outtradeno){
        Map<String,String> resultMap = weixinPayService.queryPayStatus(outtradeno);
        return new Result(true,StatusCode.OK,"查询状态成功！",resultMap);
    }
}
