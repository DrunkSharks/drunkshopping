package com.drunk.order.controller;

import com.drunk.entity.Result;
import com.drunk.entity.StatusCode;
import com.drunk.entity.TokenDecode;
import com.drunk.order.pojo.OrderItem;
import com.drunk.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    /***
     * 加入购物车
     * @param num:购买的数量
     * @param id：购买的商品(SKU)ID
     * @return
     */
    @RequestMapping(value = "/addCart")
    public Result add(Integer num, Long id){
        //获取用户名
        String username = tokenDecode.getUserInfo().get("username");
        //将商品加入购物车
        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功！");
    }

    /***
     * 查询用户购物车列表
     * @return
     */
    @GetMapping(value = "/list")
    public Result list(){
        //获取用户名
        String username = tokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true,StatusCode.OK,"购物车列表查询成功！",orderItems);
    }
}
