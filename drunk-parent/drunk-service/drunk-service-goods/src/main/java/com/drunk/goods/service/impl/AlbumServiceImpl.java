package com.drunk.goods.service.impl;

import com.drunk.goods.dao.AlbumMapper;
import com.drunk.goods.pojo.Album;
import com.drunk.goods.service.AlbumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public PageInfo<Album> findPage(Album album, int page, int size) {
        PageHelper.startPage(page,size);
        List<Album> albumList = findList(album);

        return new PageInfo<Album>(albumList);
    }

    @Override
    public PageInfo<Album> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        List<Album> albumList = albumMapper.selectAll();
        return new PageInfo<Album>(albumList);
    }

    @Override
    public List<Album> findList(Album album) {
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if(album!=null){
            // 编号
            if(!StringUtils.isEmpty(album.getId())){
                criteria.andEqualTo("id",album.getId());
            }
            // 相册名称
            if(!StringUtils.isEmpty(album.getTitle())){
                criteria.andLike("title","%"+album.getTitle()+"%");
            }
            // 相册封面
            if(!StringUtils.isEmpty(album.getImage())){
                criteria.andEqualTo("image",album.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(album.getImageItems())){
                criteria.andEqualTo("imageItems",album.getImageItems());
            }
        }
        return albumMapper.selectByExample(example);
    }

    @Override
    public void delete(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKeySelective(album);
    }

    @Override
    public void add(Album album) {
        albumMapper.insertSelective(album);
    }

    @Override
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }
}
