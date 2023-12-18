package com.kerwin.gallery.crawler;

import com.kerwin.common.PtCommon;
import com.kerwin.gallery.service.HttpFactory;
import org.jsoup.nodes.Document;
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
 * Created:      2023/12/1
 * Description:  Wallpapercave壁纸
 * https://wallpapercave.com/categories
 * ==============================================================================
 */
@Component
public class WallpapercaveCrawlerComponent implements HttpFactory {
    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private File SaveFilePath = null;
    private File ThumbnailFilePath = null;

    private final String BaseUrl = "https://wallpapercave.com";
    private final Map<String, String> HEADERS = new HashMap<String, String>() {{
        put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
    }};

    private final CrawlerCommon crawlerCommon;

    public WallpapercaveCrawlerComponent(CrawlerCommon crawlerCommon) {
        this.crawlerCommon = crawlerCommon;
    }

    public void crawler() throws IOException {
        this.SaveFilePath = new File(this.uploadDirPath, "WallpaperCave");
        this.ThumbnailFilePath = new File(this.SaveFilePath, "thumbnail");

        Document doc = this.documentWithHeaders(this.BaseUrl + "/categories", this.HEADERS);
        JXDocument document = this.jXDocument(doc);
        List<JXNode> categories = document.selN("//div[@id='content']/ul/li/a");
        for (JXNode category : categories) {
            // 大分类
            String bigCategory = category.selOne("./text()").asString();
            // 分类的URL
            String categoryUrl = category.selOne(".//@href").asString();
            this.parseCategory(bigCategory, categoryUrl);
        }
    }

    // 具体分类
    private void parseCategory(String bigCategory, String categoryUrl) throws IOException {
        JXDocument document = this.jXDocument(this.documentWithHeaders(this.BaseUrl + categoryUrl, this.HEADERS));
        List<JXNode> items = document.selN("//div[@id='popular']/a");
        for (JXNode item : items) {
            String subCategoryUrl = item.selOne("./@href").asString();
            String subCategoryName = item.selOne("./div/div[2]/div/p[1]/text()").asString();
            this.imageDetail(subCategoryUrl, bigCategory, subCategoryName);
        }
    }

    private void imageDetail(String url, String bigCategory, String subCategory) throws IOException {
        JXDocument document = this.jXDocument(this.documentWithHeaders(this.BaseUrl + url, this.HEADERS));
        List<JXNode> items = document.selN("//div[@id='albumwp']/div/a");

        for (JXNode item : items) {
            // 下载缩略图
            String thumbnailUrl = item.selOne("./picture/img/@src").asString();
            thumbnailUrl = this.BaseUrl + thumbnailUrl;
            File thumbnailFile = this.downloadImage(thumbnailUrl, this.ThumbnailFilePath);
            Map<String, Object> thumbnailMap = this.crawlerCommon.insertThumbnail(thumbnailUrl, thumbnailFile);
            if (thumbnailMap == null) {
                // 数据库中根据图片的md5查找，如已存在，则已存在则不再爬取原图
                continue;
            }
            // 缩略图ID
            Long thumbnailId = PtCommon.toLong(thumbnailMap.get("id"));
            // 图片分类
            String[] categories = new String[]{bigCategory, subCategory};
            this.crawlerCommon.insertCategories(thumbnailId, categories);
            // 下载真实尺寸图片
            String imageUrl = item.selOne("./@href").asString();
            JXDocument image = this.jXDocument(this.documentWithHeaders(this.BaseUrl + imageUrl, this.HEADERS));
            // 图片尺寸
            String imageSize = image.selNOne("//div[@id='albumwp']/div[1]/p/text()").asString();
            String[] dimensionsTmp = imageSize.split("x");
            int width = PtCommon.toInteger(dimensionsTmp[0]);
            int height = PtCommon.toInteger(dimensionsTmp[1]);
            // 真实图片的下载地址
            imageUrl = image.selNOne("//a[@id='tdownload']/@href").asString();
            imageUrl = this.BaseUrl + imageUrl;
            File imageFile = this.downloadImage(imageUrl, this.SaveFilePath);
            this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, width, height);
            // 绑定缩略图和分辨率
            List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>(){{
                add(new HashMap<String, Object>(){{put("width", width); put("height", height);}});
            }};
            this.crawlerCommon.bindThumbnailWithDimensions(thumbnailId, dimensions);

            // 暂时只下载一个图片
            break;
        }
    }
}
