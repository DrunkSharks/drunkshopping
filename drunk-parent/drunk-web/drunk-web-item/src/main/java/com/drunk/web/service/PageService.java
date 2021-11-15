package com.drunk.web.service;

public interface PageService {
    /**
     * 根据商品的ID 生成静态页
     * @param spuId
     */
    public void createPageHtml(Long spuId) ;

    void delPageHtml(Long id);
}
