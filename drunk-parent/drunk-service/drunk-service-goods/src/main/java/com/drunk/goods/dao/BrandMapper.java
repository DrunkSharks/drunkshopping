package com.drunk.goods.dao;

import com.drunk.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BrandMapper extends Mapper<Brand> {
    /***
     * 查询分类对应的品牌集合
     */
    @Select("select b.* from tb_category_brand t,tb_brand b where t.category_id = #{categoryid} and t.brand_id = b.id")
    List<Brand> findByCategory(Integer categoryid);
}
