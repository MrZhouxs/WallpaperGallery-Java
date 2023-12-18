package com.kerwin.gallery.crawler;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * 所有网站爬取的公共接口
 */
@EnableAsync
@Service
public class CrawlAllWebSiteAsync {

    private final ThreeWallCrawlerComponent threeWallCrawlerComponent;
    private final JiJianWallCrawlerComponent jiJianWallCrawlerComponent;
    private final ComputerWallCrawlerComponent computerWallCrawlerComponent;
    private final WallpapercaveCrawlerComponent wallpapercaveCrawlerComponent;
    private final WallpapersCraftCrawlerComponent wallpapersCraftCrawlerComponent;

    public CrawlAllWebSiteAsync(ThreeWallCrawlerComponent threeWallCrawlerComponent, JiJianWallCrawlerComponent jiJianWallCrawlerComponent, ComputerWallCrawlerComponent computerWallCrawlerComponent, WallpapercaveCrawlerComponent wallpapercaveCrawlerComponent, WallpapersCraftCrawlerComponent wallpapersCraftCrawlerComponent) {
        this.threeWallCrawlerComponent = threeWallCrawlerComponent;
        this.jiJianWallCrawlerComponent = jiJianWallCrawlerComponent;
        this.computerWallCrawlerComponent = computerWallCrawlerComponent;
        this.wallpapercaveCrawlerComponent = wallpapercaveCrawlerComponent;
        this.wallpapersCraftCrawlerComponent = wallpapersCraftCrawlerComponent;
    }

    @Async
    public void crawlThreeWall() {
        try {
            this.threeWallCrawlerComponent.crawler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void crawlJiJianWall() {
        try {
            this.jiJianWallCrawlerComponent.crawler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void crawlComputerWall() {
        try {
            this.threeWallCrawlerComponent.crawler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void crawlWallpaperCave() {
        try {
            this.threeWallCrawlerComponent.crawler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void crawlWallpapersCraft() {
        try {
            this.threeWallCrawlerComponent.crawler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
