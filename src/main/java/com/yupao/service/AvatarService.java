package com.yupao.service;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    /**
     * 阿里云OSS文件上传
     *
     * @param file 文件流
     * @return
     */
    String upload(MultipartFile file);

}
