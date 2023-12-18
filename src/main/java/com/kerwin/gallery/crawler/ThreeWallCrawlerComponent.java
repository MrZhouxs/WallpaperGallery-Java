package com.kerwin.gallery.crawler;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.kerwin.common.PtCommon;
import com.kerwin.gallery.service.HttpFactory;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/24
 * Description:  3g壁纸
 * https://www.3gbizhi.com
 * 网站用户名：pushtime 密码：123456789
 * ==============================================================================
 */
@Slf4j
@Component
public class ThreeWallCrawlerComponent implements HttpFactory {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private File SaveFilePath = null;
    private File ThumbnailFilePath = null;

    private final String BASEURL = "https://www.3gbizhi.com";
    // 获取图片下载地址的URL
    private final String ImageDownloadTmpUrl = "https://www.3gbizhi.com/user/getDownPic";
    private List<HttpCookie> COOKIE = new ArrayList<>();
    private final UrlBuilder REFERER_BUILDER = UrlBuilder.ofHttp(BASEURL);
    private final Map<String, String> HEADERS = new HashMap<String, String>() {{
        put("X-Requested-With", "XMLHttpRequest");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
    }};

    private final CrawlerCommon crawlerCommon;

    private final List<String> StartUrls = new ArrayList<String>(){{
        add("https://www.3gbizhi.com/sjbz/index_1.html");
        add("https://desk.3gbizhi.com/");
        add("https://www.3gbizhi.com/mingxing");
        add("https://www.3gbizhi.com/meinv");
        add("https://www.3gbizhi.com/tupian");
    }};

    public ThreeWallCrawlerComponent(CrawlerCommon crawlerCommon) {
        this.crawlerCommon = crawlerCommon;
        this.loginThreeWallPaper();
    }

    private void loginThreeWallPaper() {
        // 登陆3g壁纸并获取Cookie
        String loginUrl = "https://www.3gbizhi.com/user/login";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("__token__", "xxx");
            put("account", "pushtime");
            put("password", "123456789");
        }};
        HttpResponse response = this.post(loginUrl, params, this.HEADERS, this.COOKIE);
        JSONObject body = PtCommon.toJsonObject(response.body());
        if (body.getInteger("code") != 1) {
            // 第一次登录会报 令牌数据无效；需要用token再次登录
            String token = body.getJSONObject("data").getString("token");
            this.COOKIE = response.getCookies();
            params.put("__token__", token);
            response = this.post(loginUrl, params, this.HEADERS, this.COOKIE);
            body = PtCommon.toJsonObject(response.body());
            if (body.getInteger("code") != 1) {
                System.out.println("再次登录还是无效，请检查");
            } else {
                this.COOKIE = response.getCookies();
            }
        }
    }

    public void crawler() throws IOException {
        this.SaveFilePath = new File(this.uploadDirPath, "ThreeGWallPaper");
        this.ThumbnailFilePath = new File(this.SaveFilePath, "thumbnail");

        for (String url : this.StartUrls) {
            this.crawlerHelper(url);
        }
    }

    public void crawlerHelper(String url) throws IOException {
        JXDocument document = this.jXDocument(url);
        List<JXNode> items = document.selN("//li[@class='box_black']/a");
        for (JXNode item : items) {
            try {
                String href = item.selOne("./@href").asString();
                if (StrUtil.isBlank(href)) {
                    log.error("'3g壁纸'在获取图片信息时接口{}返回为空，舍弃当前URL，请查看", url);
                    continue;
                }
                // 图片描述
//            String imageDesc = item.selOne("./div/text()").asString();
                // 获取图片的分类和详细信息
                JXDocument imageDetailDocument = this.jXDocument(href);
                JXNode propertiesNode = imageDetailDocument.selNOne("//div[@class='properties']");
                // 图片类型：JPG
//                String imageType = propertiesNode.selOne("./div/div[1]/text()").asString();
//                // 图片大小
//                String imageSize = propertiesNode.selOne("./div/div[2]/text()").asString();
                // 图片尺寸
                String imageSize = propertiesNode.selOne("./div/div[3]/text()").asString();
                String[] dimensionsTmp = imageSize.split("x");
                int width = PtCommon.toInteger(dimensionsTmp[0]);
                int height = PtCommon.toInteger(dimensionsTmp[1]);

                // 图片标签
                List<String> labels = new ArrayList<>();
                for (JXNode node : imageDetailDocument.selN("//div[@class='showtaglistw mtw cl']/a/text()")) {
                    String label = node.asString();
                    labels.add(label);
                }
                String[] tmp = href.split("/");
                String cid = tmp[tmp.length - 1].replace(".html", "");
                // 获取图片的高清地址
                Map<String, Object> data = new HashMap<String, Object>() {{
                    put("cid", cid);
                    put("sid", "1");
                    put("picnum", "1");
                }};
                String body = this.post(this.ImageDownloadTmpUrl, data, this.HEADERS, this.COOKIE).body();
                if (StrUtil.isNotEmpty(body)) {
                    JSONObject imageData = PtCommon.toJsonObject(body);
                    if (imageData.getInteger("code") != 1) {
                        log.error("3g壁纸需要重新登录，错误原因为：{}", imageData.getString("msg"));
                        // 需要重新登陆
                        this.loginThreeWallPaper();
                        break;
                    }
                    String imageUrl = imageData.getJSONObject("data").getString("file");
                    imageUrl = this.BASEURL + imageUrl;
                    if (!imageUrl.startsWith("https")) {
                        imageUrl = UrlBuilder.create()
                                .setScheme(REFERER_BUILDER.getScheme())
                                .setHost(REFERER_BUILDER.getHost())
                                .addPath(imageUrl)
                                .build();
                    }
                    // 下载缩略图
                    String thumbnailUrl = item.selOne("./img/@lay-src").asString();
                    HttpResponse response = this.get(thumbnailUrl, new HashMap<>(), this.HEADERS, this.COOKIE);
                    // 图片数据信息入库
                    File thumbnailFile = this.downloadImage(thumbnailUrl, response, this.ThumbnailFilePath);
                    Map<String, Object> thumbnailMap = this.crawlerCommon.insertThumbnail(thumbnailUrl, thumbnailFile);
                    if (thumbnailMap == null) {
                        // 数据库中根据图片的md5查找，如已存在，则已存在则不再爬取原图
                        continue;
                    }
                    // 缩略图ID
                    Long thumbnailId = PtCommon.toLong(thumbnailMap.get("id"));
                    // 图片分类
                    String[] categories = labels.toArray(new String[0]);
                    this.crawlerCommon.insertCategories(thumbnailId, categories);
                    // 下载真实的图片
                    response = this.get(imageUrl, new HashMap<>(), this.HEADERS, this.COOKIE);
                    File imageFile = this.downloadImage(imageUrl, response, this.SaveFilePath);
                    this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, width, height);
                    // 绑定缩略图和分辨率
                    List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>(){{
                        add(new HashMap<String, Object>(){{put("width", width); put("height", height);}});
                    }};
                    this.crawlerCommon.bindThumbnailWithDimensions(thumbnailId, dimensions);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("'3g壁纸'爬取{}图片出错，原因为：{}", url, e.getMessage());
                // 爬虫出错，尝试睡眠后重新爬取
                this.sleep(5);
            }

        }
        // 获取下一页地址
        String nextUrl = document.selNOne("//a[@title='下一页']/@href").asString();
        // 5秒后再爬取下一页
        this.sleep(5);
        // 下一页地址不为空：递归调用本方法继续下载
        if (StrUtil.isNotEmpty(nextUrl)) {
            this.crawlerHelper(nextUrl);
        }
    }

}
