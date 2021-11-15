package com.drunk.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.Result;
import com.drunk.goods.feign.CategoryFeign;
import com.drunk.goods.feign.GoodsFeign;
import com.drunk.goods.feign.SkuFeign;
import com.drunk.goods.feign.SpuFeign;
import com.drunk.goods.pojo.Sku;
import com.drunk.goods.pojo.Spu;
import com.drunk.web.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    //生成静态页文件路径
    @Value("${pagepath}")
    private String pathpath;

    /**
     * 构建数据模型
     * @param spuId
     * @return
     */
    private Map<String,Object> buildDataModel(Long spuId){
        //构建数据模型
        Map<String,Object> dataMap = new HashMap<>();
        Result<Spu> spuResult = spuFeign.findById(spuId);
        Spu spu = spuResult.getData();

        //获取分类信息
        dataMap.put("category1",categoryFeign.findById(spu.getCategory1Id()).getData());
        dataMap.put("category2",categoryFeign.findById(spu.getCategory2Id()).getData());
        dataMap.put("category3",categoryFeign.findById(spu.getCategory3Id()).getData());
        if(spu.getImages()!=null){
            dataMap.put("imageList",spu.getImages().split(","));
        }
        //spu所有规格
        dataMap.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));
        dataMap.put("spu",spu);

        //根据spuId查询sku集合
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        Result<List<Sku>> skuResult = skuFeign.findList(sku);
        dataMap.put("skuList",skuResult.getData());

        return dataMap;
    }

    /**
     * 生成静态页
     * @param spuId
     */
    @Override
    public void createPageHtml(Long spuId) {
        //上下文
        Context context = new Context();
        Map<String, Object> dataModel = buildDataModel(spuId);
        context.setVariables(dataModel);
        //准备文件
        File dir = new File(pathpath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dest = new File(dir,spuId+".html");
        //生成页面
        try(PrintWriter writer = new PrintWriter(dest,"UTF-8")){
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据spuId删除静态页
     * @param id
     */
    @Override
    public void delPageHtml(Long id) {
        File file = new File(pathpath+"/"+id+".html");
        if(file.exists()){
            file.delete();
        }
    }
}
