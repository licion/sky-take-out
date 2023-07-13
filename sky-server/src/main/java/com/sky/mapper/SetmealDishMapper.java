package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> selectByDishIds(List<Long> ids);

    void insertBatch(List<SetmealDish> setmealDishes);

    List<SetmealDish> getById(Long id);

    void deletes(List<Long> ids);

    List<Long> getBySetmealId(Long id);
}
