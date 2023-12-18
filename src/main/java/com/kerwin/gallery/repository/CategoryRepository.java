package com.kerwin.gallery.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryRepository {

    // 通过标签名查询标签数据
    List<Map<String, Object>> selectCategoryByName(@Param("name") String name);

    // 一次插入多条数据
    void insertMultiple(@Param("list") List<Map<String, Object>> list);
}
