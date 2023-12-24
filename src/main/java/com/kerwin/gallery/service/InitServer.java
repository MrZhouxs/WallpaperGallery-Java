package com.kerwin.gallery.service;

import com.kerwin.common.WordStatementParserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 系统初始化
 */

@Service
public class InitServer {
    // 是否加载word分词配置文件
    @Value("${app.word-segments-enable: false}")
    private Boolean wordSegmentsEnable;

    @PostConstruct
    public void init() {
        this.initWordSegmentation();
    }

    private void initWordSegmentation() {
        if (this.wordSegmentsEnable) {
            String sen = "停车做爱枫林晚，霜叶红于二月花";
            System.out.println(WordStatementParserUtil.parser(sen));
        }
    }

}
