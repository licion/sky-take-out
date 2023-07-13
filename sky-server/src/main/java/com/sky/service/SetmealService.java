package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void save(SetmealDTO setmealDTO);

    SetmealVO getById(Long id);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deletes(List<Long> ids);

    void update(SetmealDTO setmealDTO);

    void OffOnStatus(Integer status, Long id);
}
