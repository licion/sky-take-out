package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.CommonConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserSevice;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserSevice {

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        // 1.调用微信接口获取openId
        String openId = getOpenId(userLoginDTO.getCode());
        // 2. 判断openId是否为空 是则返回 登录失败
        if (openId == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //3.根据openID在数据库查询
        User user = userMapper.getByOpenId(openId);
        //有的话正常返回,没有的话创一个
        if (user == null){
            user = new User();
            user.setOpenid(openId);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        return user;
    }

    private String getOpenId(String code) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");

        String resultJson = HttpClientUtil.doGet(CommonConstant.WEIXIN_OPENID_URL, paramMap);
        JSONObject jsonObject = JSON.parseObject(resultJson);
        String openId = jsonObject.getString("openid");

        return openId;
    }
}
