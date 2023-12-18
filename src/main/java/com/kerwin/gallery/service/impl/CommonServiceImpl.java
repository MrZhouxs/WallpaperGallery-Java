package com.kerwin.gallery.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.kerwin.gallery.component.ImageComponent;
import com.kerwin.gallery.domain.auto.UploadFile;
import com.kerwin.gallery.repository.auto.UploadFileMapper;
import com.kerwin.gallery.service.CommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/22
 * Description:
 * ==============================================================================
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private final ImageComponent imageComponent;
    private final UploadFileMapper uploadFileMapper;

    public CommonServiceImpl(ImageComponent imageComponent, UploadFileMapper uploadFileMapper) {
        this.imageComponent = imageComponent;
        this.uploadFileMapper = uploadFileMapper;
    }

    /**
     * 根据年月日创建文件夹存放文件，防止一个文件夹存放太多数据
     * @param uploadFile        文件元数据
     * @param relativeDir       文件存储在服务器上的相对路径，为空则默认存储在配置文件配置的文件夹路径中
     * @param filename          文件存储在服务器上时指定文件名，为空则根据时间字符串与原文件名拼接成新文件名存储以防文件重名
     * @return                  文件存储在服务器上的相关信息
     */
    @Override
    public Map<String, Object> uploadFile(MultipartFile uploadFile, String relativeDir, String filename) throws IOException {
        // 转换文件名称 防止重名
        String originalFilename = "";
        if (StrUtil.isNotEmpty(filename) && !StrUtil.equals(filename, "")) {
            originalFilename = filename;
        } else {
            originalFilename = new Date().getTime() + "_" + uploadFile.getOriginalFilename();
        }
        // 文件存储在服务器上的路径
        String relativeFilename = "";
        if (StrUtil.isNotEmpty(relativeDir)) {
            if (StrUtil.startWithAny(relativeDir, "/", "\\")) {
                relativeFilename = relativeDir + File.separator + originalFilename;
            } else {
                relativeFilename = File.separator + relativeDir + File.separator + originalFilename;
            }
        } else {
            // 指定在当前的年月日中
            Calendar calendar = Calendar.getInstance();
            relativeFilename = File.separator + calendar.get(Calendar.YEAR) +
                    File.separator + (calendar.get(Calendar.MONTH) + 1) +
                    File.separator + calendar.get(Calendar.DAY_OF_MONTH) +
                    File.separator + originalFilename;
        }
        // 文件存储在服务器上的完整路径
        String file_path = this.uploadDirPath + relativeFilename;
        File save_file = new File(file_path);
        if (!save_file.getParentFile().exists()) {
            if (!save_file.getParentFile().mkdirs()) {
                throw new IOException("目录(" + save_file.getParentFile().getPath() + ")不存在。。。。创建目录同样失败.");
            }
        }
        if (!save_file.exists()) {
            uploadFile.transferTo(save_file);
        } else {
            throw new IOException("file " + file_path + " is exists error");
        }

        UploadFile uploadedFileInfo = new UploadFile();
        uploadedFileInfo.setFilename(uploadFile.getOriginalFilename());
        uploadedFileInfo.setFilenameServer(originalFilename);
        uploadedFileInfo.setRelativeFilenameServer(relativeFilename);
        uploadedFileInfo.setFilepathServer(file_path);
        uploadedFileInfo.setCreateTime(new Date());
        // 如果是图片，做缩略图
        String contentType = uploadFile.getContentType();
        if (StrUtil.startWithAny(contentType, "image")) {
            File thumbnailFile = imageComponent.thumbnail(save_file, 60, 60);
            Path relativePath = Paths.get(this.uploadDirPath).relativize(thumbnailFile.toPath());
            uploadedFileInfo.setThumbnail(relativePath.toString());
            uploadedFileInfo.setFileType(this.imageComponent.imageType(save_file));
        } else {
            uploadedFileInfo.setFileType(FileNameUtil.getSuffix(save_file));
        }
        this.uploadFileMapper.insert(uploadedFileInfo);

        // 返回
        Map<String, Object> mapResult = Maps.newHashMap();
        mapResult.put("result", "ok");
        // 去掉绝对路径的值
        uploadedFileInfo.setFilepathServer(null);
        mapResult.put("file", uploadedFileInfo);
        return mapResult;
    }
}
