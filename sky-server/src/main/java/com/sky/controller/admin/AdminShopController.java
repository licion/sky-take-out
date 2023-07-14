package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(tags = "店铺管理接口")
@RequestMapping("/admin/shop")
public class AdminShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set("shop_status",status);
        return Result.success();
    }

    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result getStatus(){
        // 从redis中获取店铺营业状态
        Integer status = (Integer)redisTemplate.opsForValue().get("shop_status");
        return Result.success(status);
    }
}
