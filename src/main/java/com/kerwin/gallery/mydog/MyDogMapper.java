package com.kerwin.gallery.mydog;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyDogMapper {

    // 通过DDL创建表
    Map<String, Object> createTableByDDL(@Param("ddl") String ddl);

    // 拼接SQL并插入表中
    void insertTable(@Param("tableName") String tableName,
                     @Param("fields") List<String> fields,
                     @Param("list") List<Map<String, Object>> list);

    // 获取某张表的DDL
    Map<String, Object> selectTableDDL(@Param("tableName") String tableName);

    // 统计某张表的总数据量
    Map<String, Long> countSingleTable(@Param("tableName") String tableName);

    // 修改表名(alter语句成功执行后返回的数据值为0)
    void alterTableName(@Param("oldTableName") String oldTableName, @Param("newTableName") String newTableName);

    // 记录分表信息(insert语句成功执行后会返回影响的行数)
    Long recordTable(@Param("tableName") String tableName,
                     @Param("fromDate") String fromDate,
                     @Param("toDate") String toDate,
                     @Param("total") Long total);

    // 获取最小记录
    String selectMinDate(@Param("tableName") String tableName, @Param("key") String key);

    // 获取最大记录
    String selectMaxDate(@Param("tableName") String tableName, @Param("key") String key);
}
