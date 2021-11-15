package com.drunk.goods.feign;

import com.drunk.entity.Result;
import com.drunk.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /***
     * 库存递减
     * @param username
     * @return
     */
    @PostMapping(value = "/decr/count")
    public Result decrCount(@RequestParam("username")String username);

    /**
     * 根据条件搜索
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);

    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable Long id);
}
