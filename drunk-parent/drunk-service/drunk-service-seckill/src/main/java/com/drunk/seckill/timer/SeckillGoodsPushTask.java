package com.drunk.seckill.timer;

import com.drunk.entity.DateUtil;
import com.drunk.seckill.dao.SeckillGoodsMapper;
import com.drunk.seckill.pojo.SeckillGoods;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0/30 * * * * ?")
    public void loadGoods(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()));
        //获取时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        //循环时间段
        for (Date startTime : dateMenus) {
            System.out.println(sdf.format(startTime));
            //将日期转为字符
            String extName = sdf.format(startTime);

            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();

            //审核通过商品
            criteria.andEqualTo("status","1");
            //库存>0
            criteria.andGreaterThan("stockCount",0);
            //开始时间<=活动开始时间
            criteria.andGreaterThanOrEqualTo("startTime",sdf.format(startTime));
            //活动结束时间<开始时间+2小时
            criteria.andLessThan("endTime",sdf.format(DateUtil.addDateHour(startTime,2)));
            //排除已经加载到Redis缓存中的商品数据
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
            if(keys!=null && keys.size()>0){
                criteria.andNotIn("id",keys);
            }

            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

            //将秒杀商品数据存入Redis缓存中
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("SeckillGoods_"+extName).put(String.valueOf(seckillGood.getId()),seckillGood);
                //设置过期时间
                redisTemplate.expireAt("SeckillGoods_"+extName,DateUtil.addDateHour(startTime,2));

                //将商品存入一个redis队列中，用户每次从队列中抢单，解决超卖问题
                Long[] ids = pushIds(seckillGood.getStockCount(), seckillGood.getId());
                redisTemplate.boundListOps("SeckillGoodsCountList_"+seckillGood.getId()).leftPushAll(ids);
                //维护一个自增队列，避免高并发场景下商品数量安全问题
                redisTemplate.boundHashOps("SeckillGoodsCount").put(seckillGood.getId(),seckillGood.getStockCount());
            }
        }
    }

    public Long[] pushIds(int len,Long id){
        Long[] ids = new Long[len];
        for(int i = 0;i<len;i++){
            ids[i] = id;
        }
        return ids;
    }



    public static void main(String[] args) {
        ExecutorService executorService1 = Executors.newFixedThreadPool(5);
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        ExecutorService executorService3 = Executors.newCachedThreadPool();
        ExecutorService executorService4 = Executors.newScheduledThreadPool(10);
    }
}
