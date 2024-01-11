package com.kerwin.gallery.component;

import com.kerwin.common.PtCommon;
import com.kerwin.gallery.repository.ThumbnailRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 通过线程的方式查询多表数据
 * 整个计算过程如下：
 * 多线程查询各个分表中满足条件的数据数量
 * 将各个表数量按照分表的先后顺序累加
 * 判断第一条数据和最后一条数据所在的表
 * 除第一条和最后一条数据所在表外，其他表offset=0，pageSize=总数量
 * 计算第一条数据的offset，pageSize
 * 计算最后一条数据的pageSize，同时将该表查询条件的offset设置为0
 *
 */
@Component
public class SearchTableByThread {

    private final ThumbnailRepository thumbnailRepository;

    public SearchTableByThread(ThumbnailRepository thumbnailRepository) {
        this.thumbnailRepository = thumbnailRepository;
    }

    @Async
    public Future<Map<String, Object>> countThumbnailShard(String tableName, List<String> words) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("tableName", tableName);
        Map<String, Long> count = this.thumbnailRepository.countThumbnailShardDetail(tableName, words);
        detail.put("number", count == null ? 0L : count.get("number"));
        return new AsyncResult<>(detail);
    }

    @Async
    public Future<List<Map<String, Object>>> searchThumbnailShard(Map<String, Object> params, List<String> words) {
        return new AsyncResult<>(this.thumbnailRepository.searchThumbnailShard(words, params));
    }
}
