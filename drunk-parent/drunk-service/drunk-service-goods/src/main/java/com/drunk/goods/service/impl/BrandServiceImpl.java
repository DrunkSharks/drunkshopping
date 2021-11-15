package com.drunk.goods.service.impl;

import com.drunk.goods.dao.BrandMapper;
import com.drunk.goods.pojo.Brand;
import com.drunk.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /***
     * 根据分类ID查询品牌集合
     * @param categoryid:分类ID
     * @return
     */
    @Override
    public List<Brand> findByCategory(Integer categoryid) {
        return brandMapper.findByCategory(categoryid);
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addBrand(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(!StringUtils.isEmpty(brand.getId())){
                criteria.andEqualTo("id",brand.getId());
            }
            if(!StringUtils.isEmpty(brand.getName())){
                criteria.andLike("name","%"+brand.getName()+"%");
            }
            if(!StringUtils.isEmpty(brand.getImage())){
                criteria.andLike("image","%"+brand.getImage()+"%");
            }
            if(!StringUtils.isEmpty(brand.getLetter())){
                criteria.andEqualTo("letter",brand.getLetter());
            }
            if(!StringUtils.isEmpty(brand.getSeq())){
                criteria.andEqualTo("seq",brand.getSeq());
            }
        }

        return brandMapper.selectByExample(example);
    }

    @Override
    public PageInfo<Brand> findPage(int page, int size) {
        //分页查询
        PageHelper.startPage(page,size);
        List<Brand> brands = findAll();
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return pageInfo;
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, int page, int size) {
        PageHelper.startPage(page,size);
        List<Brand> brands = findList(brand);
        return new PageInfo<>(brands);
    }
}
