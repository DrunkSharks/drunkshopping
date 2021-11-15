package com.drunk.search.controller;


import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SearchController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result importSkuToES(){
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入成功");
    }

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap){
        return  skuService.search(searchMap);
    }
}
