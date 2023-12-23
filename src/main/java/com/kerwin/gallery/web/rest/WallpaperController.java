package com.kerwin.gallery.web.rest;

import com.kerwin.common.ResponseData;
import com.kerwin.gallery.crawler.CrawlAllWebSiteAsync;
import com.kerwin.gallery.service.WallpaperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/wallpaper")
@Api(tags = "壁纸接口")
public class WallpaperController {

    private final WallpaperService wallpaperService;
    private final CrawlAllWebSiteAsync crawlAllWebSiteAsync;


    public WallpaperController(WallpaperService wallpaperService, CrawlAllWebSiteAsync crawlAllWebSiteAsync) {
        this.wallpaperService = wallpaperService;
        this.crawlAllWebSiteAsync = crawlAllWebSiteAsync;
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取缩略图壁纸列表", notes = "以分页方式获取缩略图壁纸列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "key", value = "关键词查询", dataType = "String"),
    })
    public ResponseEntity<List<Map<String, Object>>> searchThumbnailList(@ApiIgnore @RequestParam Map<String, Object> params,
                                                                      Pageable pageable) {
        log.debug("REST request to web getWallpaperList: {}, {}", params, pageable);
        return ResponseData.addPageHeader(this.wallpaperService.searchThumbnailList(params, pageable));
    }

    @GetMapping("/thumbnail/{thumbnailId}")
    @ApiOperation(value = "获取缩略图详情", notes = "根据缩略图ID获取缩略图详情")
    @ApiImplicitParam(paramType = "path", name = "thumbnailId", value = "缩略图ID", dataType = "Long")
    public ResponseEntity<Map<String, Object>> getThumbnailDetail(@PathVariable Long thumbnailId) {
        log.debug("REST request to web getWallpaperList: {}", thumbnailId);
        return ResponseData.ajaxJson(this.wallpaperService.getThumbnailDetail(thumbnailId));
    }

    @GetMapping("/category/{label}")
    @ApiOperation(value = "搜索标签", notes = "根据标签值搜索缩略图信息")
    @ApiImplicitParam(paramType = "path", name = "label", value = "标签值", dataType = "String")
    public ResponseEntity<List<Map<String, Object>>> getThumbnailByLabel(@PathVariable String label, Pageable pageable) {
        log.debug("REST request to web getThumbnailByLabel: {}", label);
        return ResponseData.addPageHeader(this.wallpaperService.getThumbnailByLabel(label, pageable));
    }

    @GetMapping("/add/download/{wallpaperId}")
    @ApiOperation(value = "原图下载量加一", notes = "根据原图ID增加下载量")
    @ApiImplicitParam(paramType = "path", name = "thumbnailId", value = "缩略图ID", dataType = "Long")
    public ResponseEntity<Map<String, Object>> addWallpaperDownloadCount(@PathVariable Long wallpaperId) {
        log.debug("REST request to web addWallpaperDownloadCount: {}", wallpaperId);
        return ResponseData.ajaxJson(this.wallpaperService.addWallpaperDownloadCount(wallpaperId));
    }

    @GetMapping("/crawl/{type}")
    @ApiOperation(value = "指定爬取壁纸", notes = "根据类型指定爬取壁纸网站")
    @ApiImplicitParam(paramType = "path", name = "type",
            value = "网站类型：\n1、电脑壁纸\n2、极简壁纸\n3、3g壁纸\n4、Wallpaper Cave壁纸\n5、Wallpaper Craft壁纸")
    public ResponseEntity<Map<String, Object>> crawlChoiseWallpaperType(@PathVariable Integer type) {
        log.debug("REST request to web crawlChoiseWallpaperType: {}", type);
        switch (type) {
            case 1:
                this.crawlAllWebSiteAsync.crawlComputerWall();
                break;
            case 2:
                this.crawlAllWebSiteAsync.crawlJiJianWall();
                break;
            case 3:
                this.crawlAllWebSiteAsync.crawlThreeWall();
                break;
            case 4:
                this.crawlAllWebSiteAsync.crawlWallpaperCave();
                break;
            case 5:
                this.crawlAllWebSiteAsync.crawlWallpapersCraft();
            default:
                throw new IllegalArgumentException("参数错误");
        }
        return ResponseData.ajaxJson(true);
    }
}
