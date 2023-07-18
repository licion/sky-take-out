package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> isExit(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    void updateNumberById(ShoppingCart shoppingCartDataBase);

    void clean(Long id);

    @Delete("delete from shopping_cart where id = #{id}")
    void delete(Long id);


}
