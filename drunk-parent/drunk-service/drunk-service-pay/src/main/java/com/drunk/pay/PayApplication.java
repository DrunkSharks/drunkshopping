package com.drunk.pay;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class PayApplication {
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
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
     * 队列
     * @return
     */
    @Bean
    public Queue queueOrder(){
        return new Queue(environment.getProperty("mq.pay.queue.order"),true);
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
     * 将队列绑定到交换机
     * @return
     */
    @Bean
    public Binding basicBinding(){
        return BindingBuilder
                .bind(queueOrder())
                .to(basicExchange())
                .with(environment.getProperty("mq.pay.routing.key"));
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
}
