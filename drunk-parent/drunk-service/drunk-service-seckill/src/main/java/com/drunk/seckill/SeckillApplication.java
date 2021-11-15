package com.drunk.seckill;

import com.drunk.entity.IdWorker;
import com.drunk.entity.TokenDecode;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.drunk.pay.feign")
@MapperScan("com.drunk.seckill.dao")
@EnableScheduling
@EnableAsync
public class SeckillApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class,args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }

    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"),true,false);
    }

    /**
     * 秒杀队列
     * @return
     */
    @Bean(name="queueSeckillOrder")
    public Queue queueSeckillOrder(){
        return new Queue(environment.getProperty("mq,pay.queue.seckillorder"),true);
    }

    /**
     * 秒杀队列绑定到交换机
     * @return
     */
    @Bean
    public Binding basicBindingOrder(){
        return BindingBuilder
                .bind(queueSeckillOrder())
                .to(basicExchange())
                .with(environment.getProperty("mq.pay.rounting.seckillorderkey"));
    }

    /**
     * 到期数据队列
     * @return
     */
    @Bean
    public Queue seckillOrderTimerQueue(){
        return new Queue(environment.getProperty("mq.pay.queue.seckillordertimer"),true);
    }

    /**
     * 超时数据队列
     * @return
     */
    @Bean
    public Queue delaySeckillOrderTimerQueue(){
        return QueueBuilder.durable(environment.getProperty("mq.pay.queue.seckillordertimerdelay"))
                .withArgument("x-dead-letter-exchange",environment.getProperty("mq.pay.exchange.order"))    //消息超时进入死信队列，绑定指定的死信队列
                .withArgument("x-dead-letter-routing-key",environment.getProperty("mq.pay.queue.seckillordertimer"))    //绑定指定的rounting-key
                .build();
    }

    /**
     * 交换机与队列绑定
     * @return
     */
    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(seckillOrderTimerQueue())
                .to(basicExchange())
                .with(environment.getProperty("mq.pay.queue.seckillordertimer"));
    }
}
