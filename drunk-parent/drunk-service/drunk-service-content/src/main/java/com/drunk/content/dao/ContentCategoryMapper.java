package com.drunk.content.dao;
import com.drunk.content.pojo.ContentCategory;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:ContentCategory的Dao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface ContentCategoryMapper extends Mapper<ContentCategory> {
}
