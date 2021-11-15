package com.drunk.goods.service;

import com.drunk.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BrandService {
    /***
     * 根据分类ID查询品牌集合
     * @param categoryid:分类ID
     */
    List<Brand> findByCategory(Integer categoryid);

    List<Brand> findAll();

    Brand findById(Integer id);

    void addBrand(Brand brand);

    void updateBrand(Brand brand);

    void delete(Integer id);

    List<Brand> findList(Brand brand);

    PageInfo<Brand> findPage(int page,int size);

    PageInfo<Brand> findPage(Brand brand,int page,int size);
}
