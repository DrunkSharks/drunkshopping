package com.drunk.seckill.timer;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.IdWorker;
import com.drunk.seckill.dao.SeckillGoodsMapper;
import com.drunk.seckill.pojo.SeckillGoods;
import com.drunk.seckill.pojo.SeckillOrder;
import com.drunk.seckill.pojo.SeckillStatus;
import org.junit.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment environment;

    /**
     * 发送延时消息到RabbitMQ中
     * @param seckillStatus
     */
    public void sendTimerMessage(SeckillStatus seckillStatus){
        rabbitTemplate.convertAndSend(environment.getProperty("mq.pay.queue.seckillordertimerdelay"), (Object) JSON.toJSONString(seckillStatus),new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //延时30分钟
                message.getMessageProperties().setExpiration(String.valueOf(1000*60*30));
                return message;
            }
        });
    }

    /**
     * 异步从Redis List队列中获取订单并下单
     */
    @Async
    public void createOrder(){
        //从队列中获取下单信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();

        //从队列中抢单，如果队列中还有商品则抢单成功，下单，否则说明抢购完了
        Object goodsId = redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillStatus.getGoodsId()).rightPop();
        if(goodsId==null){
            //清理当前用户排队信息
            clearQueue(seckillStatus);
            return;
        }

        String time = seckillStatus.getTime();
        String username = seckillStatus.getUsername();
        Long id = seckillStatus.getGoodsId();

        //获取商品信息
        SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(String.valueOf(id));
        //获取商品数量
        Integer seckillGoodsCount = (Integer) redisTemplate.boundHashOps("SeckillGoodsCount").get(id);

        //如果没有库存，则提示已售罄，使用redis缓存中的商品数量队列判断
        if(goods==null || seckillGoodsCount<=0){
            redisTemplate.boundHashOps("SeckillStatus").delete(username);
            throw new RuntimeException("商品已售罄！");
        }

        //如果有库存，则创建秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(goods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");

        //将秒杀订单存入到Redis中
        redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

        //抢单成功，更新抢单状态
        seckillStatus.setStatus(2);
        seckillStatus.setOrderId(seckillOrder.getId());
        seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));
        redisTemplate.boundHashOps("SeckillStatus").put(username,seckillStatus);

        //库存减少
        goods.setStockCount(goods.getStockCount()-1);

        //判断当前商品是否还有库存
        if(goods.getStockCount()<=0){
            //将商品数据同步到MySQL中
            seckillGoodsMapper.updateByPrimaryKeySelective(goods);
            //删除redis中的商品记录
            redisTemplate.boundHashOps("SeckillGoods_"+time).delete(id);
        }
        else{
            //如果还有库存，则更新redis缓存中的商品数据
            redisTemplate.boundHashOps("SeckillGoods_"+time).put(id,goods);
            //同步更新商品数量
            redisTemplate.boundHashOps("SeckillGoodsCount").put(id,goods.getStockCount());
        }

        //发送延时消息，30分钟后检查订单是否超时未支付
        sendTimerMessage(seckillStatus);
    }

    /**
     * 清理用户排队信息
     * @param seckillStatus
     */
    private void clearQueue(SeckillStatus seckillStatus) {
        //清理排队标识
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
        //清理抢单状态标识
        redisTemplate.boundHashOps("SeckillStatus").delete(seckillStatus.getUsername());
    }
}
