package com.drunk.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.Result;
import com.drunk.pay.feign.WeixinPayFeign;
import com.drunk.seckill.pojo.SeckillOrder;
import com.drunk.seckill.pojo.SeckillStatus;
import com.drunk.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.seckillordertimer}")
/**
 * 监听队列，检查超时未支付订单
 */
public class SeckillOrderDelayMessageListener {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private WeixinPayFeign weixinPayFeign;

    @Autowired
    private SeckillOrderService seckillOrderService;

    /***
     * 读取消息
     * 判断Redis中是否存在对应的订单
     * 如果存在，则关闭支付，再关闭订单
     * @param message
     */
    @RabbitHandler
    public void consumeMessage(@Payload String message)throws Exception{
        //读取秒杀订单状态信息
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);

        //获取缓存中的订单信息
        String username = seckillStatus.getUsername();
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        //如果缓存中有订单信息，说明用户未支付
        if(seckillOrder!=null){
            //关闭支付
            Result closePayResult = weixinPayFeign.closePay(seckillStatus.getOrderId());
            Map<String,String> closePayMap = (Map<String, String>) closePayResult.getData();

            if(closePayMap!=null && closePayMap.get("return_code").equalsIgnoreCase("success")
                    && closePayMap.get("result_code").equalsIgnoreCase("success")){
                //关闭订单
                seckillOrderService.closeOrder(seckillStatus.getUsername());
            }
        }
    }
}
