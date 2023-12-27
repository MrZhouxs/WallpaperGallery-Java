package com.kerwin.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 语句解析
 * Word分词
 *
 * 将语句分词，如果单词为汉字则给出拼音；暂不支持语义分析
 */
public class WordStatementParserUtil {

    /**
     * 汉字转拼音，其它字符原封不动
     * @param chinese   待转的汉字
     * @return          拼音
     */
    public static String toPinYin(String chinese) {
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char ch : chars) {
            try {
                if (String.valueOf(ch).matches("[\\u4E00-\\u9FA5]+")) {
                    //中文
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(ch, format)[0]);
                } else {
                    sb.append(ch);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 采用Word分词组件，所有算法分出的结果合并后去重并加上分词的拼音
     * @param sentence      待分词的句子
     * @return              所有不重复的分词
     */
    public static List<String> parser(String sentence) {
        List<String> words = new ArrayList<>();
        for (SegmentationAlgorithm algorithm : SegmentationAlgorithm.values()) {
            // seg移除停用词 segWithStopWords保留停用词
            for (Word word : WordSegmenter.seg(sentence, algorithm)) {
                String text = word.getText();
                // 分词
                words.add(text);
                // 拼音
                words.add(toPinYin(text));
            }
        }
        words = words.stream().distinct().collect(Collectors.toList());
        return words;
    }
}
