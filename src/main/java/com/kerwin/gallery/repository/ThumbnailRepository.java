package com.kerwin.gallery.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ThumbnailRepository {

    // 插入缩略图数据
    void insertThumbnail(@Param("map") Map<String, Object> map);

    // 通过md5查询图片
    List<Map<String, Object>> selectThumbnailByMd5(@Param("mdSum") String mdSum);

    // 通过ID查找缩略图
    Map<String, Object> selectThumbnailById(@Param("id") Long id);

    // 绑定缩略图与标签
    void bingThumbnailWithCategory(@Param("thumbnailId") Long thumbnailId, @Param("list") List<Map<String, Object>> list);

    // 绑定缩略图与原图
    void bingThumbnailWithWallpaper(@Param("thumbnailId") Long thumbnailId, @Param("map") Map<String, Object> map);

    // 绑定缩略图与分辨率
    void bingThumbnailWithDimensions(@Param("thumbnailId") Long thumbnailId, @Param("list") List<Map<String, Object>> list);

    // 获取缩略图列表
    List<Map<String, Object>> selectThumbnailList(@Param("map") Map<String, Object> map,
                                                  @Param("offset") Long offset,
                                                  @Param("limit") Integer limit,
                                                  @Param("sortString") String sortString);

    // 统计缩略图数量
    Map<String, Long> countThumbnailList(@Param("map") Map<String, Object> map);

    // 通过缩略图ID查找标签
    List<Map<String, Object>> selectCategoryByThumbnailId(@Param("thumbnailId") Long thumbnailId);
    // 通过缩略图ID查找分辨率
    List<Map<String, Object>> selectDimensionsByThumbnailId(@Param("thumbnailId") Long thumbnailId);

    // 通过缩略图ID查找原图数据
    List<Map<String, Object>> selectWallpaperByThumbnailId(@Param("thumbnailId") Long thumbnailId);

    // 缩略图预览加一
    void addThumbnailPreview(@Param("thumbnailId") Long thumbnailId);
}
