package com.drunk.web.search;

import com.drunk.entity.Page;
import com.drunk.search.feign.ElasticsearchFeign;
import com.drunk.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private ElasticsearchFeign elasticsearchFeign;

    @GetMapping("/list")
    public String search(@RequestParam(required = false) Map searchMap, Model model) {
        Map result = elasticsearchFeign.search(searchMap);
        model.addAttribute("result", result);
        model.addAttribute("searchMap", searchMap);
        //拼接请求sql
        String url = url(searchMap);
        model.addAttribute("url",url);
        //封装分页数据
        Page<SkuInfo> page = new Page<>(Long.parseLong(result.get("total").toString()),
                Integer.parseInt(result.get("pageNum").toString()),
                Integer.parseInt(result.get("pageSize").toString()));
        model.addAttribute("page", page);
        return "search";
    }

    /**
     * 拼接请求url地址
     * @param searchMap
     * @return
     */
    private String url(Map searchMap) {
        String url = "/search/list";

        if(searchMap!=null && searchMap.size()>0){
            url+="?";
            Set<String> keySet = searchMap.keySet();
            for (String key : keySet) {
                if(key.equalsIgnoreCase("sortField") || key.equalsIgnoreCase("sortRule") || key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                url=url+key+"="+searchMap.get(key)+"&";
            }
            //剔除url拼接的最后一个&
            url=url.substring(0,url.length()-1);
        }
        return url;
    }
}