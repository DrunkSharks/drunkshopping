package com.drunk.canal.content;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.drunk.content.feign.ContentFeign;
import com.drunk.content.pojo.Content;
import com.drunk.entity.Result;
import com.drunk.item.feign.PageFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class ContentEventListen {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private PageFeign pageFeign;

    /**
     * 监听数据库广告信息，将广告信息更新到redis
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",schema = "changgou_content",table = "tb_content",
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT})
    public void onEventChange(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("--------------将数据更新到redis中---------------");
        String category_id = getContentIdByChange(eventType, rowData);
        //根据分类Id获取最新数据
        Result<List<Content>> contentResult = contentFeign.findByCategory(Long.parseLong(category_id));
        List<Content> contentList = contentResult.getData();
        //将数据更新到redis缓存中
        redisTemplate.opsForValue().set("content_"+category_id, JSON.toJSONString(contentList));
    }

    /**
     * 监听数据改变获取广告分类ID
     * @param eventType
     * @param rowData
     * @return
     */
    private String getContentIdByChange(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        List<CanalEntry.Column> columnsList;
        if(eventType == CanalEntry.EventType.DELETE){
            columnsList = rowData.getBeforeColumnsList();
        }
        else{
            columnsList = rowData.getAfterColumnsList();
        }
        for (CanalEntry.Column column : columnsList) {
            if(column.getName().equalsIgnoreCase("category_id")){
                return column.getValue();
            }
        }
        return "";
    }


    /**
     * 监听spu生成商品详情静态页
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",
            schema = "changgou_goods",
            table = "tb_spu",
            eventType = {CanalEntry.EventType.INSERT,CanalEntry.EventType.UPDATE,CanalEntry.EventType.DELETE}
    )
    public void onEventCustomSpu(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //判断操作类型
        //删除spu
        if(eventType == CanalEntry.EventType.DELETE){
            String spuId = "";
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if(column.getName().equalsIgnoreCase("id")){
                    spuId = column.getValue();
                    break;
                }
            }
            //删除静态页
            pageFeign.delHtml(Long.valueOf(spuId));
        }
        else{
            //新增或更新
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            String spuId = "";
            for (CanalEntry.Column column : afterColumnsList) {
                if(column.getName().equalsIgnoreCase("id")){
                    spuId = column.getValue();
                    break;
                }
            }
            //更新 生成静态页
            pageFeign.createHtml(Long.valueOf(spuId));
        }
    }
}
