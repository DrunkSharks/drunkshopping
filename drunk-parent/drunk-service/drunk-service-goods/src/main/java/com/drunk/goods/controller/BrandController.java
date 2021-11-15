package com.drunk.goods.controller;

import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.goods.pojo.Brand;
import com.drunk.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/brand")
@CrossOrigin
public class BrandController {
    @Autowired
    private BrandService brandService;

    /***
     * 根据分类实现品牌列表查询
     * /brand/category/{id}  分类ID
     */
    @GetMapping(value = "/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(value = "id")Integer categoryId){
        //调用Service查询品牌数据
        List<Brand> categoryList = brandService.findByCategory(categoryId);
        return new Result<List<Brand>>(true,StatusCode.OK,"查询成功！",categoryList);
    }

    /**
     * 查询全部品牌信息
     * @return
     */
    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> brands = brandService.findAll();
        return new Result<List<Brand>>(true, StatusCode.OK,"查询全部品牌信息",brands);
    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Brand> findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result<Brand>(true,StatusCode.OK,"查询成功",brand);
    }

    /**
     * 添加一条品牌信息
     * @param brand
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.addBrand(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 修改品牌信息
     * @param brand
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Brand brand,@PathVariable Integer id){
        brand.setId(id);
        brandService.updateBrand(brand);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 根据ID删除品牌信息
     * @param id
     * @return
     */
    @DeleteMapping(value="/{id}")
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 多条件查询品牌信息
     * @param brand
     * @return
     */
    @PostMapping(value="/search")
    public Result<List<Brand>> findList(@RequestBody Brand brand){
        List<Brand> brands = brandService.findList(brand);
        return new Result<List<Brand>>(true,StatusCode.OK,"查询成功",brands);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value="/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable Integer page,@PathVariable Integer size){
        int i = 1/0;
        PageInfo<Brand> pageInfo = brandService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /**
     * 多条件分页查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody Brand brand,@PathVariable Integer page,@PathVariable Integer size){
        PageInfo<Brand> pageInfo = brandService.findPage(brand,page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }
}
