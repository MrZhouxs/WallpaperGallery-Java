package com.kerwin.common;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseData<T> {

    public static ResponseEntity<List<Map<String, Object>>> addPageHeader(Page<Map<String, Object>> data) {
        HttpHeaders headers = new HttpHeaders();
        // headers中添加数据总数
        headers.add("X-Total-Count", PtCommon.toString(data.getTotalElements()));
        return new ResponseEntity<>(data.getContent(), headers, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> ajaxJson(Map<String, Object> data) {
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> ajaxJson(Boolean data) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", data);
        return ajaxJson(res);
    }
}
