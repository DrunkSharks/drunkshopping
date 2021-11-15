package com.drunk.content.feign;

import com.drunk.content.pojo.Content;
import com.drunk.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name="content")
@RequestMapping("/content")
public interface ContentFeign {

    /**
     * 根据分类ID查询所有广告
     * @param id
     * @return
     */
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable Long id);
}
