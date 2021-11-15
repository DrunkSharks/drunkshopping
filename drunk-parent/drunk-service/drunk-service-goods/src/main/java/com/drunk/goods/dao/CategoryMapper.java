package com.drunk.goods.dao;

import com.drunk.goods.pojo.Category;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface CategoryMapper extends Mapper<Category> {
}
