package com.kerwin.gallery.service;

import com.kerwin.gallery.crawler.CrawlAllWebSiteAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 系统初始化
 */
@Service
public class InitServer {

    private final CrawlAllWebSiteAsync crawlAllWebSiteAsync;

    public InitServer(CrawlAllWebSiteAsync crawlAllWebSiteAsync) {
        this.crawlAllWebSiteAsync = crawlAllWebSiteAsync;
    }

    @PostConstruct
    public void init() {
        this.crawlAllWebSiteAsync.crawlThreeWall();
        this.crawlAllWebSiteAsync.crawlJiJianWall();
        this.crawlAllWebSiteAsync.crawlComputerWall();
        this.crawlAllWebSiteAsync.crawlWallpaperCave();
        this.crawlAllWebSiteAsync.crawlWallpapersCraft();
    }
}
