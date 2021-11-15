package com.drunk.seckill;


import com.drunk.entity.DateUtil;
import com.drunk.seckill.dao.SeckillGoodsMapper;
import com.drunk.seckill.pojo.SeckillGoods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=SeckillApplication.class)
public class demo {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Test
    public void test()throws Exception{
        //Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-05-23 18:00:00");
        Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-09-05 22:00:00");
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
        //将日期转为字符
        String extName = new SimpleDateFormat("yyyyMMddHH").format(startTime);

        Example example = new Example(SeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //审核通过商品
        criteria.andEqualTo("status","1");
        //库存>0
        criteria.andGreaterThan("stockCount",0);
        //开始时间<=活动开始时间
        criteria.andGreaterThanOrEqualTo("startTime","2021-09-05 22:00:00");
        //活动结束时间<开始时间+2小时
        criteria.andLessThan("endTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.addDateHour(startTime,2)));
        //排除已经加载到Redis缓存中的商品数据
        Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
        /*if(keys!=null && keys.size()>0){
            criteria.andNotIn("id",keys);
        }*/

        //查询数据
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

        //将秒杀商品数据存入Redis缓存中
        for (SeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("SeckillGoods_"+extName).put(String.valueOf(seckillGood.getId()),seckillGood);
            //设置过期时间
            redisTemplate.expireAt("SeckillGoods_"+extName, DateUtil.addDateHour(startTime,2));
        }
    }
}
