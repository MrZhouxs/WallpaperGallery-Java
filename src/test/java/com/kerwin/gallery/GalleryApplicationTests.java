package com.kerwin.gallery;

import com.kerwin.gallery.component.SearchTableByThread;
import com.kerwin.gallery.mydog.MyDogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GalleryApplicationTests {

    @Autowired
    private SearchTableByThread searchTableByThread;

    @Autowired
    private MyDogService myDogService;

    @Test
    void myDogTest() {
        String tableName = "sys_upload_file";
        Map<String, Object> data = new HashMap<>();
        data.put("filename", "测试数据");
        List<Map<String, Object>> r = new ArrayList<>();
        r.add(data);
        this.myDogService.insert(tableName, r);
    }
}
