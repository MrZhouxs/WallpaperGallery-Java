package com.kerwin.gallery.crawler;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import com.kerwin.common.PtCommon;
import com.kerwin.common.WordStatementParserUtil;
import com.kerwin.gallery.component.ImageComponent;
import com.kerwin.gallery.repository.CategoryRepository;
import com.kerwin.gallery.repository.DimensionsRepository;
import com.kerwin.gallery.repository.ThumbnailRepository;
import com.kerwin.gallery.repository.WallpaperRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 爬虫需要用到的公共方法
 */
@Service
public class CrawlerCommon {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;
    @Value("${app.check-duplicate}")
    private Boolean checkDuplicate;

    private final ImageComponent imageComponent;
    private final CategoryRepository categoryRepository;
    private final ThumbnailRepository thumbnailRepository;
    private final WallpaperRepository wallpaperRepository;
    private final DimensionsRepository dimensionsRepository;

    public CrawlerCommon(ImageComponent imageComponent, CategoryRepository categoryRepository,
                         ThumbnailRepository thumbnailRepository, WallpaperRepository wallpaperRepository,
                         DimensionsRepository dimensionsRepository) {
        this.imageComponent = imageComponent;
        this.categoryRepository = categoryRepository;
        this.thumbnailRepository = thumbnailRepository;
        this.wallpaperRepository = wallpaperRepository;
        this.dimensionsRepository = dimensionsRepository;
    }

    /**
     * 计算文件的相对路径
     */
    private String relativeFilepath(File file) throws IOException {
        Path parentPath = Paths.get(this.uploadDirPath);
        Path childPath = Paths.get(file.getCanonicalPath());
        return parentPath.relativize(childPath).toString();
    }

    /**
     * 缩略图信息入库
     * @param thumbnailUrl      缩略图的网络URL
     * @param thumbnailFile     缩略图存储在本地的文件对象
     */
    public Map<String, Object> insertThumbnail(String thumbnailUrl, File thumbnailFile) throws IOException {
        if (thumbnailFile == null) {
            return null;
        }
        String mdSum = PtCommon.md5Hex(thumbnailFile);
        if (this.checkDuplicate) {
            List<Map<String, Object>> cache = this.thumbnailRepository.selectThumbnailByMd5(mdSum);
            if (PtCommon.isNotEmpty(cache)) {
                // 存在则不在入库
                return null;
            }
        }
        Map<String, Object> thumbnailMap = new HashMap<String, Object>(){{
            put("url", thumbnailUrl);
            put("mdSum", mdSum);
            put("filename", thumbnailFile.getName());
            put("filenameServer", thumbnailFile.getName());
            put("filepathServer", thumbnailFile.getCanonicalPath());
            put("relativeFilepathServer", "/api/static/" + relativeFilepath(thumbnailFile));
        }};
        thumbnailMap.putAll(this.imageComponent.imageDetail(thumbnailFile));
        this.thumbnailRepository.insertThumbnail(thumbnailMap);
        return thumbnailMap;
    }

    /**
     * 检查标签是否存在
     * @param category      标签中文名
     * @return              存在返回true，否则返回false
     */
    private Boolean checkExistCategory(String category) {
        return PtCommon.isNotEmpty(this.categoryRepository.selectCategoryByName(category));
    }

    /**
     * 绑定缩略图与标签
     * @param thumbnailId       缩略图ID
     * @param categories        标签列表
     */
    public void insertCategories(Long thumbnailId, List<Map<String, Object>> categories) {
        if (categories.size() > 0) {
            this.thumbnailRepository.bingThumbnailWithCategory(thumbnailId, categories);
        }
    }

    public void insertCategories(Long thumbnailId, String[] categories) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String cat : categories) {
            Map<String, Object> map = new HashMap<String, Object>(){{
                put("name", cat);
                put("href", "/category/" + WordStatementParserUtil.toPinYin(cat));
            }};
            list.add(map);
        }
        // 过滤已存在的标签
        List<Map<String, Object>> listFilter = list.stream()
                // 去重
                .distinct()
                .filter(map -> !this.checkExistCategory(PtCommon.toString(map.get("name"))))
                // 过滤标签为空的
                .filter(map -> StrUtil.isNotBlank(PtCommon.toString(map.get("name"))))
                .collect(Collectors.toList());
        if (listFilter.size() > 0) {
            // 标签入库
            this.categoryRepository.insertMultiple(listFilter);
        }
        // 先查找所有标签的ID
        List<Map<String, Object>> theLeft = list.stream().distinct()
                .map(item -> {
                    if (item.get("id") == null) {
                        List<Map<String, Object>> cacheList = this.categoryRepository.selectCategoryByName(PtCommon.toString(item.get("name")));
                        if (PtCommon.isNotEmpty(cacheList)) {
                            return cacheList.get(0);
                        }
                        return null;
                    }
                    return item;
                })
                .filter(PtCommon::isNotNull)
                .collect(Collectors.toList());
        // 再绑定缩略图ID
        if (theLeft.size() > 0) {
            this.thumbnailRepository.bingThumbnailWithCategory(thumbnailId, theLeft);
        }
    }

    /**
     * 标签信息入库
     * @param thumbnailId       缩略图ID
     * @param category          标签列表
     * @param regex             分割标签的正则表达式
     */
    public void insertCategories(Long thumbnailId, String category, String regex) {
        if (category == null) {
            return;
        }
        String[] categories = category.split(regex);
        this.insertCategories(thumbnailId, categories);
    }

    /**
     * 原图信息入库，并与缩略图进行绑定
     * @param thumbnailId       缩略图ID
     * @param imageUrl          原图的URL
     * @param imageFile         原图存储在本地的文件对象
     * @param width             图片的宽度
     * @param height            图片的高度
     */
    public void insertWallpaper(Long thumbnailId, String imageUrl, File imageFile, int width, int height) throws IOException {
        if (imageFile == null) {
            return;
        }
        String mdSum = PtCommon.md5Hex(imageFile);
        Map<String, Object> wallpaperMap = new HashMap<String, Object>(){{
            put("url", imageUrl);
            put("width", width);
            put("height", height);
            put("mdSum", mdSum);
            put("filename", imageFile.getName());
            put("filenameServer", imageFile.getName());
            put("filepathServer", imageFile.getCanonicalPath());
            put("relativeFilepathServer", relativeFilepath(imageFile));
            put("fileType", imageComponent.imageType(imageFile));
            put("size", imageFile.length());
            put("size_ch", DataSizeUtil.format(imageFile.length()));
        }};

        if (width == 0 && height == 0) {
            // 如果宽和高为0，则重新计算图片的真实宽和高
            wallpaperMap.putAll(this.imageComponent.imageDetail(imageFile));
        }
        // 图片数据入库
        this.wallpaperRepository.insertWallpaper(wallpaperMap);
        // 绑定缩略图和原图
        this.thumbnailRepository.bingThumbnailWithWallpaper(thumbnailId, wallpaperMap);
    }

    /**
     * 检查标签是否存在
     * @param dimension     分辨率大小
     * @return              存在返回true，否则返回false
     */
    private Boolean checkExistDimension(String dimension) {
        return PtCommon.isNotEmpty(this.dimensionsRepository.selectDimensionBySize(dimension));
    }

    /**
     * 绑定缩略图和分辨率
     */
    public void bindThumbnailWithDimensions(Long thumbnailId, List<Map<String, Object>> dimensions) throws IOException {
        for (Map<String, Object> dimension : dimensions) {
            int width = PtCommon.toInteger(dimension.get("width"));
            int height = PtCommon.toInteger(dimension.get("height"));
            if (width == 0 || height == 0) {
                File imageFile = new File(PtCommon.toString(dimension.get("imageFilepath")));
                Map<String, Object> detail = this.imageComponent.imageDetail(imageFile);
                width = PtCommon.toInteger(detail.get("width"));
                height = PtCommon.toInteger(detail.get("height"));
                dimension.putAll(detail);
            }
            dimension.put("size", String.format("%dx%d", width, height));
        }
        // 过滤已存在的分辨率
        List<Map<String, Object>> filterList = dimensions.stream()
                // 去重
                .distinct()
                .filter(dimension -> !this.checkExistDimension(PtCommon.toString(dimension.get("size"))))
                // 过滤掉分辨率为0的
                .filter(dimension -> PtCommon.toInteger(dimension.get("width")) != 0 && PtCommon.toInteger(dimension.get("height")) != 0)
                .collect(Collectors.toList());
        if (filterList.size() > 0) {
            // 分辨率数据入库
            this.dimensionsRepository.insertMultiple(filterList);
        }
        // 先查找分辨率的ID
        List<Map<String, Object>> theLeft = dimensions.stream()
                .map(item -> {
                    if (item.get("id") == null) {
                        List<Map<String, Object>> cacheList = this.dimensionsRepository.selectDimensionBySize(PtCommon.toString(item.get("size")));
                        if (PtCommon.isNotEmpty(cacheList)) {
                            return cacheList.get(0);
                        }
                        return null;
                    }
                    return item;
                })
                .filter(PtCommon::isNotNull)
                .collect(Collectors.toList());
        // 再绑定缩略图和分辨率
        if (theLeft.size() > 0) {
            this.thumbnailRepository.bingThumbnailWithDimensions(thumbnailId, theLeft);
        }
    }
}
