package com.kerwin.gallery;

import com.kerwin.gallery.crawler.ComputerWallCrawlerComponent;
import com.kerwin.gallery.crawler.JiJianWallCrawlerComponent;
import com.kerwin.gallery.repository.WallpaperRepository;
import com.kerwin.gallery.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GalleryApplicationTests {

    @Autowired
    private JiJianWallCrawlerComponent jianWallCrawlerComponent;

    @Autowired
    private ComputerWallCrawlerComponent computerWallCrawlerComponent;


    @Autowired
    private MailService mailService;

    @Autowired
    private WallpaperRepository wallpaperRepository;

    @Test
    void contextLoads() throws IOException {
        this.jianWallCrawlerComponent.crawler();
    }

    @Test
    void mailTest() {
        this.mailService.sendSimpleMail("发送邮件测试以下");
    }

    @Test
    void wallpaperTest() {
        this.computerWallCrawlerComponent.crawler();
    }
}
