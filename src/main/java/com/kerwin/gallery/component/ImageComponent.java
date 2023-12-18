package com.kerwin.gallery.component;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.kerwin.common.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/23
 * Description:  图片处理组件
 * ==============================================================================
 */
@Slf4j
@Component
public class ImageComponent {

    private final RestTemplate restTemplate;

    public ImageComponent(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            sb.append(hex.length() == 2 ? hex : ("0" + hex));
        }
        return sb.toString();
    }

    /**
     * 获取图片的宽、高、真实类型
     */
    public Map<String, Object> imageDetail(File imageFile) throws IOException {
        Map<String, Object> data = new HashMap<>();
        int width = 0;
        int height = 0;
        if (imageFile != null && imageFile.exists()) {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
                data.put("fileType", this.imageType(imageFile));
            }
        }
        data.put("width", width);
        data.put("height", height);
        return data;
    }

    /**
     * 获取图片的真实文件类型
     */
    public String imageType(File imageFile) throws IOException {
        String type = "unknown";
        InputStream inputStream = new FileInputStream(imageFile);
        byte[] fileHeader = new byte[4];
        inputStream.read(fileHeader, 0, fileHeader.length);
        inputStream.close();
        String header = bytes2Hex(fileHeader);
        if (header.contains("FFD8FF")) {
            type = "jpg";
        } else if (header.contains("89504E47")) {
            type = "png";
        } else if (header.contains("47494638")) {
            type = "gif";
        } else if (header.contains("424D")) {
            type = "bmp";
        } else if (header.contains("52494646")) {
            type = "webp";
        } else if (header.contains("49492A00")) {
            type = "tif";
        }
        return type;
    }

    /**
     * 在图片的当前路径创建thumbnail文件夹并制作图片缩略图
     * @param imageFile         原始图片
     * @param thumbWidth        指定缩略图的宽度
     * @param thumbHeight       指定缩略图的高度
     * @return                  缩略图的文件对象
     * @throws IOException      文件不存在、文件操作失败
     */
    public File thumbnail(File imageFile, int thumbWidth, int thumbHeight) throws IOException {
        if (!imageFile.exists()) {
            throw new FileNotFoundException("文件不存在");
        }
        String imageType = this.imageType(imageFile);
        String filename = FileNameUtil.getPrefix(imageFile);
        String parentPath = imageFile.getParent();
        File thumbFile = FileUtil.file(parentPath, "thumbnail", filename + "." + imageType);
        if (!thumbFile.getParentFile().exists()) {
            boolean flag = thumbFile.getParentFile().mkdirs();
            if (!flag) {
                throw new IOException("创建缩略图文件夹失败");
            }
        }

        FileOutputStream outputStream = new FileOutputStream(thumbFile);
        BufferedImage image = ImageIO.read(imageFile);

        if (image == null) {
            throw new IOException("暂不支持" + imageType + "类型的图片做成缩略图");
        }
        BufferedImage thumbnailBufferedImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = thumbnailBufferedImage.getGraphics();
        g.drawImage(image.getScaledInstance(thumbWidth, thumbHeight, BufferedImage.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        ImageIO.write(thumbnailBufferedImage, imageType, outputStream);

        // 关闭文件句柄
        outputStream.close();
        return thumbFile;
    }

    public void downloadImage(String url, File saveFile) {
        int reTry = 1;
        while (true) {
            ResponseEntity<File> responseEntity = this.restTemplate.execute(url, HttpMethod.GET, null, response -> {
                FileCopyUtils.copy(response.getBody(), new FileOutputStream(saveFile));
                return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(saveFile);
            });
            if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
                break;
            }
            reTry++;
            if (reTry >= 3) {
                break;
            }
            ThreadUtil.safeSleep(1000);
        }
        log.info("成功下载{}音乐文件，url:{}", saveFile.getName(), url);
    }

}
