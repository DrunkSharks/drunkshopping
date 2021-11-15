package com.drunk.item.feign;

import com.drunk.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="item")
@RequestMapping("/page")
public interface PageFeign {

    /***
     * 根据SpuID生成静态页
     * @param id
     * @return
     */
    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(name="id") Long id);

    @RequestMapping("/delHtml/{id}")
    public Result delHtml(@PathVariable(name="id") Long id);
}