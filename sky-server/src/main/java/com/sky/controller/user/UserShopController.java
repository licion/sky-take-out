package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺管理
 */
@RestController
@RequestMapping("/user/shop")
@Api(tags = "店铺管理接口")
@Slf4j
public class UserShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺营业状态
     */
    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result getStatus(){
        // 从redis中获取店铺营业状态
        Integer status = (Integer)redisTemplate.opsForValue().get("shop_status");
        return Result.success(status);
    }

}



















