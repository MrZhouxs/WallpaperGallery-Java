package com.kerwin.gallery.crawler;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kerwin.common.PtCommon;
import com.kerwin.gallery.service.HttpFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.script.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/28
 * Description:  极简壁纸
 * https://bz.zzzmh.cn/index
 * 先编译JS再执行：https://www.cnblogs.com/kanyun/p/16159710.html
 * ==============================================================================
 */
@Slf4j
@Component
public class JiJianWallCrawlerComponent implements HttpFactory {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private File SaveFilePath = null;
    private File ThumbnailFilePath = null;

    private final String JSFileContent = ResourceUtil.readUtf8Str("jijian.js");
    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final ScriptEngine engine = manager.getEngineByName("nashorn");
    private final Map<String, String> HEADERS = new HashMap<String, String>() {{
        put("Referer", "https://bz.zzzmh.cn/");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
    }};

    private final CrawlerCommon crawlerCommon;

    public JiJianWallCrawlerComponent(CrawlerCommon crawlerCommon) {
        this.crawlerCommon = crawlerCommon;
    }

    public void crawler() throws IOException {
        this.SaveFilePath = new File(this.uploadDirPath, "JiJianWallPaper");
        this.ThumbnailFilePath = new File(this.SaveFilePath, "thumbnail");
        int page = 1;
        this.crawlerHelper(page);
    }

    private void crawlerHelper(int page) {
        try {
            // 页面参数请求
            String nextUrl = "https://api.zzzmh.cn/bz/v3/getData";
            Map<String, Object> params = new HashMap<>();
            params.put("category", 0);
            params.put("categoryId", 0);
            params.put("color", 0);
            params.put("current", page);
            params.put("ratio", 0);
            params.put("resolution", 0);
            params.put("size", 96);
            params.put("sort", 0);
            String data = this.post(nextUrl, PtCommon.toJsonObject(params).toJSONString(), new HashMap<>(), new ArrayList<>()).body();
            JSONObject msg = PtCommon.toJsonObject(data);
            if (msg.getInteger("code") == 0) {
                String base64Data = msg.getString("result");
                if (StrUtil.isNotBlank(base64Data)) {
                    // 接口获取到的数据需要经过JS代码处理后能可以使用
                    String tmp = this.execJavaScript(base64Data);
                    JSONArray list = PtCommon.toJsonObject(tmp).getJSONArray("list");
                    if (PtCommon.isNotEmpty(list)) {
                        for (Object item : list) {
                            try {
                                JSONObject image = PtCommon.toJsonObject(item);
                                // 通过getUrl请求后重定向到图片真实的URL
                                String thumbnailUrl = this.getRealUrl(image.getString("i"), image.getString("t"), "0");
                                String imageName = this.getImageNameFromUrl(thumbnailUrl);
                                HttpResponse thumbnailResponse = this.get(thumbnailUrl, new HashMap<>(), this.HEADERS, new ArrayList<>());
                                // 下载缩略图
                                File thumbnailFile = this.downloadImage(thumbnailUrl, thumbnailResponse, this.completeFilepath(this.ThumbnailFilePath, imageName));
                                Map<String, Object> thumbnailMap = this.crawlerCommon.insertThumbnail(thumbnailUrl, thumbnailFile);
                                if (thumbnailMap == null) {
                                    // 数据库中根据图片的md5查找，如已存在，则已存在则不再爬取原图
                                    continue;
                                }
                                // 缩略图ID
                                Long thumbnailId = PtCommon.toLong(thumbnailMap.get("id"));

                                // 下载原图
                                String imageUrl = this.getRealUrl(image.getString("i"), image.getString("t"), "9");
                                HttpResponse imageResponse = this.get(imageUrl, new HashMap<>(), this.HEADERS, new ArrayList<>());
                                File imageFile = this.downloadImage(imageUrl, imageResponse, this.completeFilepath(this.SaveFilePath, imageName));
                                int width = image.getInteger("w");
                                int height = image.getInteger("h");
                                this.crawlerCommon.insertWallpaper(thumbnailId, imageUrl, imageFile, width, height);
                                // 绑定缩略图和分辨率
                                List<Map<String, Object>> dimensions = new ArrayList<Map<String, Object>>(){{
                                    add(new HashMap<String, Object>(){{put("width", width); put("height", height);}});
                                }};
                                this.crawlerCommon.bindThumbnailWithDimensions(thumbnailId, dimensions);

                                // 图片分类(默认宽比高大则为电脑壁纸，否则为手机壁纸)
                                int categoryId = 1;
                                if (width < height) {
                                    categoryId = 2;
                                }
                                int finalCategoryId = categoryId;
                                List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(){{
                                    new HashMap<String, Object>(){{
                                        put("id", finalCategoryId);
                                    }};
                                }};
                                this.crawlerCommon.insertCategories(thumbnailId, mapList);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("'极简壁纸'爬取图片出错，原因为：{}", e.getMessage());
                            }
                        }
                        // 5秒后再爬取下一页
                        this.sleep(5);
                        page++;
                        this.crawlerHelper(page);
                    }
                }
            }

        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            log.error("'极简壁纸'爬取{}页出错，原因为：{}", page, e.getMessage());
            // 爬虫出错，尝试睡眠后重新爬取
            this.sleep(5);
        }
    }

    private String getRealUrl(String imageUuid, String t, String suffix) {
        String redirectBeforeUrl = "https://api.zzzmh.cn/bz/v3/getUrl/" + imageUuid + t + suffix;
        HttpResponse response = this.get(redirectBeforeUrl, new HashMap<>(), this.HEADERS, new ArrayList<>());
        return response.header("Location");
    }

    // 从URL获取图片名称
    private String getImageNameFromUrl(String url) throws MalformedURLException {
        URL u = new URL(url);
        String[] urlSplit = u.getPath().split("/");
        return urlSplit[urlSplit.length - 2];
    }

    // 执行JS代码
    private String execJavaScript(String data) throws ScriptException, NoSuchMethodException {
        this.engine.eval(this.JSFileContent);
        Invocable invocable = (Invocable) engine;
        Object res = invocable.invokeFunction("_0x17f5d5", data);
        return res.toString();
    }
}