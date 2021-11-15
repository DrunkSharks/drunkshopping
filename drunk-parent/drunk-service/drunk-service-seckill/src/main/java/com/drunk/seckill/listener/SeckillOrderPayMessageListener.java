package com.drunk.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drunk.seckill.dao.SeckillOrderMapper;
import com.drunk.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues="${mq.pay.queue.seckillorder}")
/**
 * 监听队列修改订单状态
 */
public class SeckillOrderPayMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitHandler
    public void message(String message){
        Map<String,String> resultMap = JSON.parseObject(message, Map.class);
        System.out.println("监听到的消息："+resultMap);

        //返回状态码
        String returnCode = resultMap.get("return_code");
        String resultCode = resultMap.get("result_code");
        if(returnCode.equalsIgnoreCase("success")){
            //获取订单号
            String outTradeNo = resultMap.get("out_trade_no");
            //获取订单流水号
            String transactionId = resultMap.get("transaction_id");
            //获取附加信息
            Map<String,String> attachMap = JSON.parseObject(resultMap.get("attach"),Map.class);

            if(resultCode.equalsIgnoreCase("success")){
                //修改订单状态
                seckillOrderService.updatePayStatus(outTradeNo,transactionId,attachMap.get("username"));
            }
            else{
                //支付失败，删除订单并回滚库存
                seckillOrderService.closeOrder(attachMap.get("username"));
            }
        }
    }
}
