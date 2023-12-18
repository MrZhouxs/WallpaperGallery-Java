package com.kerwin.gallery.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DimensionsRepository {

    // 通过size查询分辨率数据
    List<Map<String, Object>> selectDimensionBySize(@Param("size") String size);

    // 一次插入多条数据
    void insertMultiple(@Param("list") List<Map<String, Object>> list);
}
