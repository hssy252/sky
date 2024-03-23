package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import java.util.List;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public interface ShoppingCartService {

    void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void clean();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
