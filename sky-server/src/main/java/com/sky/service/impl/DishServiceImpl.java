package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    public SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //新增菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        //新增菜品的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            dishFlavorMapper.insertBatch(flavors);//后绪步骤实现
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.page(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);

    }

    @Override
    public void delete(List<Long> ids) {
        //判断是否在售，
        List<Dish> dishList = dishMapper.selectByIds(ids);
        for (Dish dish : dishList) {
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断是否有套餐关联
        List<Long> setmealIdList = setmealDishMapper.selectByDishIds(ids);
        if (!CollectionUtils.isEmpty(setmealIdList)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deletes(ids);
        dishFlavorMapper.deleteByDishIds(ids);
        //拿到每一个id，挨个删除
    }

    @Override
    public DishVO getById(Long id) {

        Dish dish = dishMapper.getById(id);

        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        long id = dishDTO.getId();
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(id));

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(id);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    //有bug，只能停用
    public void statusOffOn(Integer status, Long id) {
        Dish dish = new Dish();
        List<Long> setmealIds = setmealDishMapper.selectByDishIds(Collections.singletonList(id));
        if (setmealIds.isEmpty()){
            dish.setId(id);
            dish.setStatus(status);
            dishMapper.update(dish);
            return;
        }else {
            for (Long setmealId : setmealIds) {
                Setmeal setmeal = new Setmeal();
                setmeal.setId(setmealId);
                setmeal.setStatus(status);
                setmealMapper.update(setmeal);
            }
            dish.setId(id);
            dish.setStatus(status);
            dishMapper.update(dish);
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<Dish> list = dishMapper.list(dish);
        return list;
    }


}


