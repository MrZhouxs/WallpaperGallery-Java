package com.kerwin.gallery.crawler;

import cn.hutool.core.util.StrUtil;
import com.kerwin.common.PtCommon;
import com.kerwin.gallery.service.HttpFactory;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
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
 * Description: WallpapersCraft壁纸
 * https://wallpaperscraft.com/
 * ==============================================================================
 */
@Slf4j
@Component
public class WallpapersCraftCrawlerComponent implements HttpFactory {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private final String BaseUrl = "https://wallpaperscraft.com";
    private File SaveFilePath = null;
    private File ThumbnailFilePath = null;

    private final CrawlerCommon crawlerCommon;

    public WallpapersCraftCrawlerComponent(CrawlerCommon crawlerCommon) {
        this.crawlerCommon = crawlerCommon;
    }

    public void crawler() throws IOException {
        this.SaveFilePath = new File(this.uploadDirPath, "WallpapersCraft");
        this.ThumbnailFilePath = new File(this.SaveFilePath, "thumbnail");

        int page = 1;
        String startUrl = "https://wallpaperscraft.com/all/page";
        this.parseImageList(startUrl, page);
    }

    private void parseImageList(String prefixUrl, int page) throws IOException {
        String url = prefixUrl + page;
        JXDocument document = this.jXDocument(url);
        List<JXNode> images = document.selN("//ul[@class='wallpapers__list ']/li/a");
        if (PtCommon.isNotEmpty(images)) {
            for (JXNode image : images) {
                try {
                    // 下载缩略图
                    String thumbnailUrl = image.selOne("./span[1]/img/@src").asString();
                    File thumbnailFile = this.downloadImage(thumbnailUrl, this.ThumbnailFilePath);
                    Map<String, Object> thumbnailMap = this.crawlerCommon.insertThumbnail(thumbnailUrl, thumbnailFile);
                    if (thumbnailMap == null) {
                        // 数据库中根据图片的md5查找，如已存在，则已存在则不再爬取原图
                        continue;
                    }
                    // 缩略图ID
                    Long thumbnailId = PtCommon.toLong(thumbnailMap.get("id"));
                    // 图片分类
                    String category = image.selOne("./span[3]/text()").asString();
                    if (StrUtil.isNotBlank(category)) {
                        category = category.replace(" ", "");
                        this.crawlerCommon.insertCategories(thumbnailId, category, ",");
                    }
                    // 解析图片具体信息
                    String imageDetailUrl = image.selOne("./@href").asString();
                    imageDetailUrl = this.BaseUrl + imageDetailUrl;
                    this.parseImageDetail(imageDetailUrl, thumbnailId);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("'WallpapersCraft壁纸'爬取{}图片出错，原因为：{}", url, e.getMessage());
                    // 爬虫出错，尝试睡眠后重新爬取
                    this.sleep(5);
                }
                break;
            }

            // 5秒后再爬取下一页
            this.sleep(5);
            page++;
            this.parseImageList(prefixUrl, page);
        }
    }

    /**
     * 下载各种尺寸的图片
     */
    private void parseImageDetail(String imageDetailUrl, Long thumbnailId) throws IOException {
        JXDocument document = this.jXDocument(imageDetailUrl);
        List<JXNode> items = document.selN("//div[@class='resolutions gui-mobile-container']/section/div/div/ul/li/a");
        for (JXNode item : items) {
            try {
                String imageSize = item.selOne("./text()").asString();
                String[] dimensionsTmp = imageSize.split("x");
                int width = PtCommon.toInteger(dimensionsTmp[0]);
                int height = PtCommon.toInteger(dimensionsTmp[1]);
                String downloadUrl = item.selOne("./@href").asString();
                // 获取真实尺寸的图片URL
                JXDocument detailDocument = this.jXDocument(this.BaseUrl + downloadUrl);
                String imageUrl = detailDocument.selNOne("//div[@class='wallpaper__placeholder']/a/@href").asString();
                // 下载真实尺寸的图片
                File imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
                this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, width, height);
                // 绑定缩略图和分辨率
                List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>(){{
                    add(new HashMap<String, Object>(){{put("width", width); put("height", height);}});
                }};
                this.crawlerCommon.bindThumbnailWithDimensions(thumbnailId, dimensions);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("'WallpapersCraft壁纸'爬取{}图片出错，原因为：{}", imageDetailUrl, e.getMessage());
            }
            // 300ms爬取一次
            this.sleep(0.3f);
        }
    }
}
