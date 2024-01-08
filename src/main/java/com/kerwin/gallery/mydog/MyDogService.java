package com.kerwin.gallery.mydog;

import cn.hutool.core.util.StrUtil;
import com.kerwin.common.PtCommon;
import com.kerwin.common.PtDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义分表功能
 * 当某张表的数据达到分表阈值时自动分表
 */
@Slf4j
@Service
public class MyDogService {

    @Value("${mydog.enable: true}")
    private Boolean mydogEnable;

    @Value("${mydog.threshold: 1000000}")
    private Long mydogThreshold;

    private final MyDogMapper myDogMapper;

    public MyDogService(MyDogMapper myDogMapper) {
        this.myDogMapper = myDogMapper;
    }

    // 获取某张表的DDL
    private String selectTableDDL(String tableName) {
        // 如果表不存在，mybatis执行SQL语句时会包错
        Map<String, Object> ddl = this.myDogMapper.selectTableDDL(tableName);
        return PtCommon.toString(ddl.get("Create Table"));
    }

    // 统计某张表的总数据量
    private Long countSingleTable(String tableName) {
        Map<String, Long> count = this.myDogMapper.countSingleTable(tableName);
        return count == null ? 0L : count.get("number");
    }

    // 修改表名
    private void alterTableName(String oldTableName, String newTableName) {
        // alter语句成功执行后返回的数据值为0
        this.myDogMapper.alterTableName(oldTableName, newTableName);
    }

    // 将当前表的信息数据记入到分表记录表中
    private Boolean recordTable(String tableName, Long total, String key) {
        String fromDate = this.myDogMapper.selectMinDate(tableName, key);
        String toDate = this.myDogMapper.selectMaxDate(tableName, key);
        Long affectRows = this.myDogMapper.recordTable(tableName, fromDate, toDate, total);
        return affectRows > 0L;
    }

    // 自定义逻辑分表功能
    public Map<String, Object> logicShard(String tableName, Map<String, Object> data) {
        List<Map<String, Object>> temp = new ArrayList<>();
        temp.add(data);
        return this.logicShard(tableName, temp).get(0);
    }

    // 自定义逻辑分表功能
    public List<Map<String, Object>> logicShard(String tableName, List<Map<String, Object>> data) {
        // 插入数据之前检测是否需要分表
        if (this.mydogEnable) {
            // 统计当前表的总数据量
            Long currentTableCount = this.countSingleTable(tableName);
            // 当前需要分表的表记录总数大于自定义的阈值，则需要分表
            if (currentTableCount >= this.mydogThreshold) {
                String currentDate = PtDateTime.getDateTimeString("yyyyMMddHHmmss");
                // 将当前表修改名称：原表名+当前时间(年月日时分秒)
                String shardTableName = tableName + currentDate;
                // 将当前表的数据量记入到分表记录表中
                this.recordTable(tableName, currentTableCount,"create_time");
                // 修改表名
                this.alterTableName(tableName, shardTableName);
                // 获取表的DDL
                String ddl = this.selectTableDDL(tableName);
                // 根据DDL重新创建表
                this.myDogMapper.createTableByDDL(ddl);
            }
        }
        // 插入数据
        return this.insert(tableName, data);
    }

    // 根据表名和Map结构的数据拼接SQL语句
    public List<Map<String, Object>> insert(String tableName, List<Map<String, Object>> data) {
        if (PtCommon.isNotEmpty(data)) {
            List<String> fields = new ArrayList<>(data.get(0).keySet());
            this.myDogMapper.insertTable(tableName, fields, data);
            return data;
        } else {
            log.error("待插入的数据不可为空");
            throw new NullPointerException("待插入的数据为空");
        }
    }
}
