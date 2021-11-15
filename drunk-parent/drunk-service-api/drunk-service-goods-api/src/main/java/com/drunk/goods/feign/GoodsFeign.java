package com.drunk.goods.feign;

import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.goods.pojo.Category;
import com.drunk.goods.pojo.Sku;
import com.drunk.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("goods")
@RequestMapping("/sku")
public interface GoodsFeign {

    /***
     * 根据审核状态查询Sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable String status);

}
