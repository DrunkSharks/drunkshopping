package com.drunk.search.service;

import java.util.Map;

public interface SkuService {

    /**
     * 导入Sku数据
     */
    public void importSku();

    /***
     * 搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String, String> searchMap);
}
