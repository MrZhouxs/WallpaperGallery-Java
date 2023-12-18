package com.kerwin.gallery.crawler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kerwin.common.PtCommon;
import com.kerwin.gallery.service.HttpFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/30
 * Description:  电脑壁纸
 * https://wallpaper.ur1.fun/
 * ==============================================================================
 */
@Slf4j
@Component
public class ComputerWallCrawlerComponent implements HttpFactory {
    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private final String DownApi = "https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=";
    private File SaveFilePath = null;
    private File ThumbnailFilePath = null;

    private final CrawlerCommon crawlerCommon;

    public ComputerWallCrawlerComponent(CrawlerCommon crawlerCommon) {
        this.crawlerCommon = crawlerCommon;
    }

    public void crawler() {
        this.SaveFilePath = new File(this.uploadDirPath, "ComputerWallPaper");
        this.ThumbnailFilePath = new File(this.SaveFilePath, "thumbnail");
        // 获取所有分类
        String startUrl = "https://wallpaper.ur1.fun/api/?cid=360tags";
        String string = this.get(startUrl, new HashMap<>(), new HashMap<>(), new ArrayList<>()).body();
        if (StrUtil.isBlank(string)) {
            log.error("'电脑壁纸'在获取所有分类时接口{}返回为空，当前爬虫结束，请查看", startUrl);
            return;
        }
        JSONObject json = PtCommon.toJsonObject(string);
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject category = data.getJSONObject(i);
            String id = category.getString("id");
            this.crawlerHelper(id, 0, 100);
        }
    }

    private String decode360Url(String oldUrl, int width, int height) {
        String newUrl = oldUrl.replace("r/__85", "m/" + width + "_" + height + "_" + 100);
        newUrl = newUrl.replace("http", "https");
        return this.DownApi + newUrl;
    }

    private void crawlerHelper(String cid, int start, int count) {
        String url = String.format("https://wallpaper.ur1.fun/api/?cid=%s&start=%d&count=%d", cid, start, count);
        String string = this.get(url, new HashMap<>(), new HashMap<>(), new ArrayList<>()).body();
        if (StrUtil.isBlank(string)) {
            log.error("'电脑壁纸'在获取图片信息时接口{}返回为空，舍弃当前URL，请查看", url);
            return;
        }
        JSONArray data = PtCommon.toJsonObject(string).getJSONArray("data");
        if (data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.getJSONObject(i);
                try {
                    // 下载缩略图
                    String thumbnailUrl = item.getString("url");
                    if (StrUtil.isBlank(thumbnailUrl)) {
                        return;
                    }
                    File thumbnailFile = this.downloadImage(thumbnailUrl, this.ThumbnailFilePath);
                    Map<String, Object> thumbnailMap = this.crawlerCommon.insertThumbnail(thumbnailUrl, thumbnailFile);
                    if (thumbnailMap == null) {
                        // 数据库中根据图片的md5查找，如已存在，则已存在则不再爬取原图
                        continue;
                    }
                    // 缩略图ID
                    Long thumbnailId = PtCommon.toLong(thumbnailMap.get("id"));
                    // 图片分类
                    String categories = item.getString("utag");
                    this.crawlerCommon.insertCategories(thumbnailId, categories, " ");
                    // 下载2560*1600
                    String imageUrl = this.decode360Url(thumbnailUrl, 2560, 1600);
                    File imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, 2560, 1600);
                    // 下载1440*900
                    imageUrl = this.decode360Url(thumbnailUrl, 1440, 900);
                    imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, 1440, 900);
                    // 下载1024*768
                    imageUrl = this.decode360Url(thumbnailUrl, 1024, 768);
                    imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, 1024, 768);
                    // 下载800*600
                    imageUrl = this.decode360Url(thumbnailUrl, 800, 600);
                    imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, 800, 600);
                    // 下载原始图片
                    imageUrl = this.decode360Url(thumbnailUrl, 0, 0);
                    imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, 0, 0);
                    // 绑定缩略图和分辨率
                    List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{put("width", 2560); put("height", 1600);}});
                        add(new HashMap<String, Object>(){{put("width", 1440); put("height", 900);}});
                        add(new HashMap<String, Object>(){{put("width", 1024); put("height", 768);}});
                        add(new HashMap<String, Object>(){{put("width", 800); put("height", 600);}});
                    }};
                    Map<String, Object> srcMap = new HashMap<>();
                    srcMap.put("width", 0);
                    srcMap.put("height", 0);
                    srcMap.put("imageFilepath", imageFile.getCanonicalPath());
                    dimensions.add(srcMap);
                    this.crawlerCommon.bindThumbnailWithDimensions(thumbnailId, dimensions);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("'电脑壁纸'爬取{}图片出错，原因为：{}", url, e.getMessage());
                    // 爬虫出错，尝试睡眠后重新爬取
                    this.sleep(5);
                }
            }
            // 5秒后再爬取下一页
            this.sleep(5);
            start += count;
            this.crawlerHelper(cid, start, count);
        }
    }
}
