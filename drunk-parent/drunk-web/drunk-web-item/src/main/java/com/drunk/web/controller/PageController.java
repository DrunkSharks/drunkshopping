package com.drunk.web.controller;

import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.web.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(name="id") Long id){
        pageService.createPageHtml(id);
        return new Result(true, StatusCode.OK,"创建成功");
    }

    @RequestMapping("/delHtml/{id}")
    public Result delHtml(@PathVariable(name="id") Long id){
        pageService.delPageHtml(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }
}
