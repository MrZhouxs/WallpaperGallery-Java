package com.kerwin.gallery.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kerwin.common.PtCommon;
import com.kerwin.common.WordStatementParserUtil;
import com.kerwin.gallery.component.SearchTableByThread;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WallpaperServiceImpl implements WallpaperService {

    @Value("${app.word-segments-enable: false}")
    private Boolean wordSegmentsEnable;
    @Value("${mydog.enable: true}")
    private Boolean mydogEnable;

    private final ThumbnailRepository thumbnailRepository;
    private final WallpaperRepository wallpaperRepository;
    private final SearchTableByThread searchTableByThread;

    public WallpaperServiceImpl(ThumbnailRepository thumbnailRepository, WallpaperRepository wallpaperRepository, SearchTableByThread searchTableByThread) {
        this.thumbnailRepository = thumbnailRepository;
        this.wallpaperRepository = wallpaperRepository;
        this.searchTableByThread = searchTableByThread;
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

    // 获取所有图片缩略图分表的表名
    private List<String> selectAllShardTableName() {
        List<String> tableNames = new ArrayList<>();
        // 添加上本身表记录(本身的数据记录是最新的数据，应该放在最前面展示)
        tableNames.add("thumbnail");
        List<Map<String, Object>> records = this.thumbnailRepository.selectSequenceRecord();
        tableNames.addAll(records.stream().map(record -> PtCommon.toString(record.get("table_name"))).collect(Collectors.toList()));
        return tableNames;
    }

    /**
     * 分页查询图片缩略图
     * 第一次查询：通过分表记录查询符合条件的所有分表具体信息
     * 第二次查询：根据分页调整各个分表的查询语句
     * @param words         智能语义分析后的词语
     * @param pageable      分页参数
     * @return              返回符合条件的所有分表数据
     */
    private Page<Map<String, Object>> searchAllThumbnailTables(List<String> words, Pageable pageable, String sortString) {
        List<String> tableNames = this.selectAllShardTableName();
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        tableNames.forEach(tableName -> {
            Future<Map<String, Object>> thread = this.searchTableByThread.countThumbnailShard(tableName, words);
            futureList.add(thread);
        });
        List<Map<String, Object>> details = new ArrayList<>();
        futureList.forEach(future -> {
            try {
                details.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        // 符合条件的同数据量
        Long total = 0L;
        // 在分页条件下各个分表符合的临时记录
        List<Map<String, Object>> cond = new ArrayList<>();
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();
        boolean isFirst = true;
        // 再根据分表的详细信息调整查询语句
        for (Map<String, Object> detail : details) {
            detail.put("sortString", sortString);
            Long oneTableTotal = PtCommon.toLong(detail.get("number"));
            total += oneTableTotal;
            if (offset >= oneTableTotal) {
                // 当前分表的数据不在此次查询中
                offset -= oneTableTotal;
            } else {
                // 计算offset
                if (isFirst) {
                    // 判断是否是第一次记录
                    detail.put("offset", offset);
                    isFirst = false;
                } else {
                    detail.put("offset", 0);
                }
                // 计算pageSize
                if (oneTableTotal - offset >= pageSize) {
                    // 当前分表的总量 - 偏移量后 >= 每页的数量：当前分表已满足分页查询条件
                    detail.put("size", pageSize);
                    break;
                } else {
                    // 当前分表剩余的数据量全部需要查询，且继续查找下一个分表的数据
                    long size = oneTableTotal - offset;
                    detail.put("size", size);
                    // 减去pageSize的值
                    pageSize -= size;
                }
                cond.add(detail);
                // 判断pageSize的值是否大于0
                if (pageSize <= 0) {
                    break;
                }
            }
        }
        // 查询各个分表的数据
        List<Future<List<Map<String, Object>>>> shardList = new ArrayList<>();
        cond.forEach(params -> shardList.add(this.searchTableByThread.searchThumbnailShard(params, words)));
        List<Map<String, Object>> content = new ArrayList<>();
        shardList.forEach(future -> {
            try {
                List<Map<String, Object>> tmp = future.get();
                // 通过缩略图ID查找标签
                tmp = this.searchThumbnailById(tmp);
                content.addAll(tmp);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Map<String, Object>> searchThumbnailListByIntelligentSemantics(Map<String, Object> params, Pageable pageable) {
        List<Map<String, Object>> data = new ArrayList<>();
        long total = 0L;
        String keyWords = PtCommon.toString(params.get("key"));
        if (StrUtil.isNotBlank(keyWords)) {
            // 智能语义分词
            List<String> words = WordStatementParserUtil.parser(keyWords);
            if (PtCommon.isNotEmpty(words)) {
                // 拼接排序字段
                String sortString = PtCommon.getSortString(pageable);
                if (this.mydogEnable) {
                    // 线程查找各个分表的缩略图数据
                    return this.searchAllThumbnailTables(words, pageable, sortString);
                } else {
                    // 以下是单表查询: 查找缩略图
                    data = this.thumbnailRepository.searchThumbnailListByIntelligentSemantics(words, pageable.getOffset(), pageable.getPageSize(), sortString);
                    // 通过缩略图ID查找标签
                    data = this.searchThumbnailById(data);
                    // 统计数量
                    Map<String, Long> count = this.thumbnailRepository.countThumbnailListByIntelligentSemantics(words);
                    total = count == null ? 0L : count.get("number");
                }
            }
        }

        return new PageImpl<>(data, pageable, total);
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
