package com.drunk.order.listener;

import com.alibaba.fastjson.JSON;
import com.drunk.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.order}")
public class OrderPayMessageListener {

    @Autowired
    private OrderService orderService;

    /**
     * 接收消息，监听订单支付状态
     * @param msg
     */
    @RabbitHandler
    public void consumerMessage(String msg){
        Map<String,String> result = JSON.parseObject(msg, Map.class);

        //返回结果
        String return_code = result.get("return_code");
        //业务结果
        String result_code = result.get("result_code");

        if("success".equalsIgnoreCase(return_code)){
            //获取订单号
            String out_trade_no = result.get("out_trade_no");
            if("success".equalsIgnoreCase(result_code)){
                if(out_trade_no!=null){
                    //修改订单状态
                    orderService.updateStatus(out_trade_no,result.get("transaction_id"));
                }
                else{
                    //删除订单
                    orderService.deleteOrder(out_trade_no);
                    //回滚库存
                }
            }
        }
    }
}
