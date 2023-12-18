package com.kerwin.gallery.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WallpaperRepository {

    // 获取壁纸列表
    List<Map<String, Object>> selectWallpaperList(@Param("offset") Long offset,
                                                  @Param("limit") Integer limit,
                                                  @Param("sortString") String sortString);
    // 统计壁纸数量
    Map<String, Long> countWallpaperList();

    // 插入原图数据
    void insertWallpaper(@Param("map")Map<String, Object> map);

    // 原图下载量加一
    void addWallpaperDownloadCount(@Param("wallpaperId") Long wallpaperId);
}
