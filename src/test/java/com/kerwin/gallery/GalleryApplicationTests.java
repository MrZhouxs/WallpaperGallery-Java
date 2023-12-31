package com.kerwin.gallery;

import com.kerwin.gallery.component.SearchTableByThread;
import com.kerwin.gallery.mydog.MyDogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootTest
class GalleryApplicationTests {

    @Autowired
    private SearchTableByThread searchTableByThread;

    @Test
    void contextLoads() throws IOException {
        List<Future<Integer>> res = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future<Integer> r = searchTableByThread.thread(i + 1);
            res.add(r);
        }
        res.forEach( r -> {
            try {
                System.out.println("线程执行的结果： " + r.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

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
