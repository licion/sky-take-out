package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //得到扩展后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成文件名
        String objectName = UUID.randomUUID() + substring;


        try {
            String filePass = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePass);
        } catch (IOException e) {
            log.error("文件上传失败：{}" ,e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
