//package com.kerwin.gallery.crawler;
//
//import cn.hutool.core.io.FileUtil;
//import com.kerwin.gallery.service.JsoupFactory;
//import com.microsoft.playwright.*;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//
///**
// * ==============================================================================
// * Author:       Kerwin
// * Created:      2023/11/23
// * Description:  WallHere壁纸
// * Playwright for Java: https://playwright.dev/java/docs/intro
// *
// * Playwright downloads Chromium, WebKit and Firefox browsers into the OS-specific cache folders:
// *
// * %USERPROFILE%\AppData\Local\ms-playwright on Windows
// * ~/Library/Caches/ms-playwright on MacOS
// * ~/.cache/ms-playwright on Linux
// * ==============================================================================
// */
//
//@Component
//public class WallHereCrawlerComponent implements JsoupFactory {
//
//    private final Playwright playwright = Playwright.create();
//    private final Browser browser = playwright.firefox().launch(
//            new BrowserType.LaunchOptions().setHeadless(false)
//                    .setSlowMo(100)
//                    .setDevtools(true)
//                    .setTimeout(0)
//    );
//
//    private final Map<String, String> headers = new HashMap<String, String>() {{
//        put("Accept-Encoding", "gzip, deflate, br");
//        put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
//        put("Content-Type", "application/x-www-form-urlencoded");
//        put("Origin", "https://wallhere.com");
//        put("Referer", "https://wallhere.com/zh/wallpapers");
//        put("Sec-Fetch-Mode", "navigate");
//        put("Sec-Fetch-Site", "same-origin");
//        put("Dnt", "1");
//        put("Upgrade-Insecure-Requests", "1");
//    }};
//
//    private final BrowserContext context = browser.newContext(
//            new Browser.NewContextOptions()
//                    .setIgnoreHTTPSErrors(true)
//                    .setJavaScriptEnabled(true)
//                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0")
//                    .setExtraHTTPHeaders(headers)
//    );
//
//    @Override
//    public void crawler() throws IOException {
//        String url = "https://wallhere.com/zh/wallpapers?order=popular&format=json";
//        try {
//            // 创建标签
//            Page page = this.context.newPage();
//            page.evaluate("Object.defineProperties(navigator, {webdriver: {get:() => undefined}})");
//            // 隐藏Playwright浏览器本身属性
//            String stealthJsPath = "D:\\Demos\\javaWorkSpace\\WallpaperGallery\\lib\\stealth.min.js";
//            String stealthJsContent = FileUtil.readString(stealthJsPath, StandardCharsets.UTF_8);
//            page.evaluate(stealthJsContent);
//            // 打开URL
//            page.navigate(url);
//            // 等待iframe并点击验证
//            page.frameLocator("//*[@tabindex=0]").locator("input[type=\"checkbox\"]");
//
//            // 再次打开URL
//            page.navigate(url);
//            // 等待10秒页面加载
//            page.waitForTimeout(10 * 1000);
//
//            System.out.println(page.content());
//        } catch (Exception ioException) {
//            ioException.printStackTrace();
//        }
//    }
//}
