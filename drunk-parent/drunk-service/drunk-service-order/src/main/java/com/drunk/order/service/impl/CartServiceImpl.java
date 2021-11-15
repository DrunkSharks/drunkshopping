package com.drunk.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.drunk.entity.Result;
import com.drunk.goods.feign.SkuFeign;
import com.drunk.goods.feign.SpuFeign;
import com.drunk.goods.pojo.Sku;
import com.drunk.goods.pojo.Spu;
import com.drunk.order.pojo.OrderItem;
import com.drunk.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;


    /**
     * 获取购物车列表
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        return redisTemplate.boundHashOps("Cart_"+username).values();
    }

    /**
     * 将商品添加到购物车中
     * @param num:购买商品数量
     * @param id：购买ID
     * @param username：购买用户
     */
    @Override
    public void add(Integer num, Long id, String username) {
        //删除购物车中的商品
        if(num<=0){
            Object o = redisTemplate.boundHashOps("Cart_"+username).delete(id);
            return;
        }

        //查询Sku
        Result<Sku> skuResult = skuFeign.findById(id);
        if(skuResult!=null && skuResult.isFlag()){
            //获取sku
            Sku sku = skuResult.getData();
            //获取spu
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());

            //将Sku转换成OrderItem
            OrderItem orderItem = sku2OrderItem(sku,spuResult.getData(),num);

            //将购物车数据存入到redis中
            redisTemplate.boundHashOps("Cart_"+username).put(id, orderItem);

        }
    }

    /**
     * 分装购物车订单列表类
     * @param sku
     * @param spu
     * @param num
     * @return
     */
    private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*orderItem.getPrice());    //总价=单价*数量
        orderItem.setPayMoney(num*orderItem.getPrice()); //实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight()*num);       //重量=单个重量*数量

        //分类ID
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());

        return orderItem;
    }
}
