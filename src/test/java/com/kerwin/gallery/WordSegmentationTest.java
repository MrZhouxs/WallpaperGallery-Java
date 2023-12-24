package com.kerwin.gallery;

import com.kerwin.common.WordStatementParserUtil;
import org.junit.jupiter.api.Test;

public class WordSegmentationTest {

    @Test
    void segTest() {
        String sen = "停车做爱枫林晚，霜叶红于二月花";
        System.out.println(WordStatementParserUtil.parser(sen));
    }
}
