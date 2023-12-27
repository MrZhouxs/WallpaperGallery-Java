package com.kerwin.gallery.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kerwin.common.PtCommon;
import com.kerwin.common.WordStatementParserUtil;
import com.kerwin.gallery.repository.ThumbnailRepository;
import com.kerwin.gallery.repository.WallpaperRepository;
import com.kerwin.gallery.service.WallpaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WallpaperServiceImpl implements WallpaperService {

    @Value("${app.word-segments-enable: false}")
    private Boolean wordSegmentsEnable;

    private final ThumbnailRepository thumbnailRepository;
    private final WallpaperRepository wallpaperRepository;

    public WallpaperServiceImpl(ThumbnailRepository thumbnailRepository, WallpaperRepository wallpaperRepository) {
        this.thumbnailRepository = thumbnailRepository;
        this.wallpaperRepository = wallpaperRepository;
    }

    private List<Map<String, Object>> searchThumbnailById(List<Map<String, Object>> data) {
        // 通过缩略图ID查找标签
        return data.stream().peek(thumbnail -> {
            Long thumbnailId = PtCommon.toLong(thumbnail.get("id"));
            List<Map<String, Object>> categories = this.thumbnailRepository.selectCategoryByThumbnailId(thumbnailId);
            thumbnail.put("categories", categories);
        }).collect(Collectors.toList());
    }

    /**
     * 获取缩略图列表：每张缩略图包含标签信息
     * 通过对分辨率、标签模糊查找缩略图
     * @param params        搜索参数: {"key": "xxx"}
     * @param pageable      分页参数
     * @return              缩略图本身信息以及相关的标签信息列表
     */
    @Override
    public Page<Map<String, Object>> searchThumbnailList(Map<String, Object> params, Pageable pageable) {
        // 查询是否启用智能语义分析
        if (this.wordSegmentsEnable) {
            return this.searchThumbnailListByIntelligentSemantics(params, pageable);
        }
        // 拼接排序字段
        String sortString = PtCommon.getSortString(pageable);
        // 查找缩略图
        List<Map<String, Object>> data = this.thumbnailRepository.selectThumbnailList(params, pageable.getOffset(), pageable.getPageSize(), sortString);
        data = this.searchThumbnailById(data);
        Map<String, Long> count = this.thumbnailRepository.countThumbnailList(params);
        return new PageImpl<>(data, pageable, count == null ? 0L : count.get("number"));
    }

    @Override
    public Page<Map<String, Object>> searchThumbnailListByIntelligentSemantics(Map<String, Object> params, Pageable pageable) {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Long> count = null;
        // 拼接排序字段
        String sortString = PtCommon.getSortString(pageable);
        String keyWords = PtCommon.toString(params.get("key"));
        if (StrUtil.isNotBlank(keyWords)) {
            // 智能语义分词
            List<String> words = WordStatementParserUtil.parser(keyWords);
            if (PtCommon.isNotEmpty(words)) {
                // 查找缩略图
                data = this.thumbnailRepository.searchThumbnailListByIntelligentSemantics(words, pageable.getOffset(), pageable.getPageSize(), sortString);
                // 通过缩略图ID查找标签
                data = this.searchThumbnailById(data);
                // 统计数量
                count = this.thumbnailRepository.countThumbnailListByIntelligentSemantics(words);
            }
        }

        return new PageImpl<>(data, pageable, count == null ? 0L : count.get("number"));
    }

    /**
     * 根据缩略图ID获取缩略图详情
     * 没查询一次则认为预览一次
     * @param thumbnailId       缩略图ID
     * @return                  缩略图详情
     */
    @Override
    public Map<String, Object> getThumbnailDetail(Long thumbnailId) {
        // 查找缩略图
        Map<String, Object> thumbnail = this.thumbnailRepository.selectThumbnailById(thumbnailId);
        // 查找标签
        List<Map<String, Object>> categories = this.thumbnailRepository.selectCategoryByThumbnailId(thumbnailId);
        thumbnail.put("categories", categories);
        // 查找原图数据
        List<Map<String, Object>> wallpapers = this.thumbnailRepository.selectWallpaperByThumbnailId(thumbnailId);
        thumbnail.put("wallpapers", wallpapers);
        // 将预览次数加一
        this.thumbnailRepository.addThumbnailPreview(thumbnailId);
        return thumbnail;
    }

    @Override
    public Page<Map<String, Object>> getThumbnailByLabel(String label, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("label", label);
        return this.searchThumbnailList(params, pageable);
    }

    @Override
    public Boolean addWallpaperDownloadCount(Long wallpaperId) {
        this.wallpaperRepository.addWallpaperDownloadCount(wallpaperId);
        return true;
    }
}
