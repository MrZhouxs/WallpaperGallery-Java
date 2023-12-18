package com.kerwin.gallery.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/22
 * Description:
 * ==============================================================================
 */
public interface CommonService {

    /**
     * 根据年月日创建文件夹存放文件，防止一个文件夹存放太多数据
     * @param uploadFile        文件元数据
     * @param relativeDir       文件存储在服务器上的相对路径，为空则默认存储在配置文件配置的文件夹路径中
     * @param filename          文件存储在服务器上时指定文件名，为空则根据时间字符串与原文件名拼接成新文件名存储以防文件重名
     * @return                  文件存储在服务器上的相关信息
     */
    Map<String, Object> uploadFile(MultipartFile uploadFile, String relativeDir, String filename) throws IOException;

}
