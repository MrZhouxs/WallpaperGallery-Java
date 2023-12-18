package com.kerwin.common;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final public class PtCommon {
    private static final Pattern pattern = Pattern.compile("^\\d+\\.0+$");
    private static final SerializeConfig jsonKeyNoChangeConfig = getJsonKeyNoChangeConfig();

    public static final String CurrPath = getCurrPath();

    private static String getCurrPath() {
        String currPathTemp = System.getProperty("java.class.path");
        String pathSplit = System.getProperty("path.separator");
        if (currPathTemp.contains(pathSplit)) {
            currPathTemp = new File("").getAbsolutePath();
        }
        else {
            currPathTemp = new File(currPathTemp).getAbsoluteFile().getParent();
        }
        return currPathTemp;
    }

    public static String getSortString(Pageable pageable) {
        // 解析排序字段
        List<String> sorts = new ArrayList<>();
        pageable.getSort().forEach(sort -> {
            sorts.add(sort.getProperty() + " " + sort.getDirection());
        });
        // 拼接排序字段
        return StrUtil.join(", ", sorts);
    }

    private static SerializeConfig getJsonKeyNoChangeConfig() {
        SerializeConfig configTemp = new SerializeConfig();
        configTemp.propertyNamingStrategy = PropertyNamingStrategy.NoChange;
        return configTemp;
    }

    public static String format(String format, Object... arguments) {
        return MessageFormatter.arrayFormat(format, arguments).getMessage();
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] base64Decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public static String md5Hex(final String data) {
        if (isEmpty(data)) {
            return null;
        }
        return md5Hex(data.getBytes());
    }

    public static String md5Hex(final byte[] data) {
        return DigestUtils.md5Hex(data);
    }

    //public static String md5Hex(final String filePath) {
    //    return md5Hex(new File(filePath));
    //}

    public static String md5Hex(final File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return DigestUtils.md5Hex(inputStream);
        }
        finally {
            IOUtils.close(inputStream);
        }
    }

    public static String md5Hex(final InputStream inputStream) throws IOException {
        try {
            return DigestUtils.md5Hex(inputStream);
        }
        finally {
            IOUtils.close(inputStream);
        }
    }

    public static String toString(Object value) {
        if (value instanceof byte[]) {
            return toString((byte[]) value);
        }
        else if (value instanceof JSONObject) {
            return JSONObject.toJSONString(value, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty);
        }
        else if (value instanceof JSONArray) {
            return JSONArray.toJSONString(value, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty);
        }
        return TypeUtils.castToString(value);
    }

    public static String toJsonString(Object value, boolean jsonKeyNoChange) {
        if (jsonKeyNoChange) {
            return JSON.toJSONString(value, jsonKeyNoChangeConfig);
        }
        else {
            return toString(value);
        }
    }

    public static String toString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String toString(byte[] data, String charsetName) {
        return new String(data, Charset.forName(charsetName));
    }

    public static String findAndToString(byte[] data) {
        int length = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                length = i;
                break;
            }
        }
        return new String(data, 0, length);
    }

    public static Double toDouble(Object value) {
        if (value instanceof byte[]) {
            return ByteUtil.bytesToDouble((byte[]) value);
        }
        if (value instanceof Date) {
            return PtCommon.toDouble(((Date) value).getTime());
        }
        if (value instanceof Boolean) {
            return TypeUtils.castToDouble(TypeUtils.castToInt(value));
        }
        if (value instanceof BigDecimal) {
            return toDouble(value, 10);
        }

        Double res = TypeUtils.castToDouble(value);
        if (PtCommon.isNotEmpty(res)) {
            //转换为2位小数
            return (new BigDecimal(res)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else {
            return res;
        }
    }

    protected static Double toDoubleImpl(Object value) {
        if (value instanceof byte[]) {
            return ByteUtil.bytesToDouble((byte[]) value);
        }
        if (value instanceof Date) {
            return PtCommon.toDouble(((Date) value).getTime());
        }
        if (value instanceof Boolean) {
            return TypeUtils.castToDouble(TypeUtils.castToInt(value));
        }
        if (value instanceof BigDecimal) {
            return toDouble(value, 10);
        }

        return TypeUtils.castToDouble(value);
    }

    public static Double toDouble(Object value, int scale) {
        return toDouble(value, BigDecimal.ROUND_HALF_UP, scale);
    }

    public static Double toDouble(Object value, int roundingMode, int scale) {
        if (isNull(value)) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).setScale(scale, roundingMode).doubleValue();
        }
        else {
            Double tempDouble = toDoubleImpl(value);
            if (isNull(tempDouble)) {
                return tempDouble;
            }
            BigDecimal b = new BigDecimal(tempDouble);
            return b.setScale(scale, roundingMode).doubleValue();
        }
    }

    public static Long toLong(Object value) {
        if (value instanceof byte[]) {
            return ByteUtil.bytesToLong((byte[]) value);
        }
        if (value instanceof String) {
            //判断是否是 XX.000这种格式
            if (pattern.matcher((String) value).matches()) {
                return TypeUtils.castToDouble(value).longValue();
            }
            else if (Pattern.compile("^\\d+\\.\\d+E\\d+$").matcher((String) value).matches()) {
                return TypeUtils.castToDouble(value).longValue();
            }
        }
        return TypeUtils.castToLong(value);
    }

    public static Integer toInteger(Object value) {
        if (value instanceof byte[]) {
            return ByteUtil.bytesToInt((byte[]) value);
        }
        if (value instanceof String) {
            //判断是否是 XX.000这种格式
            if (pattern.matcher((String) value).matches()) {
                return TypeUtils.castToDouble(value).intValue();
            }
        }
        return TypeUtils.castToInt(value);
    }

    public static Date toDate(Integer year, Integer month, Integer date,
                              Integer hourOfDay, Integer minute, Integer second, Integer millSeconds) {
        if (PtCommon.isNotEmpty(year)) {
            Calendar ret = Calendar.getInstance();
            ret.set(Calendar.YEAR, year);
            ret.set(Calendar.MONTH, PtCommon.isNotEmpty(month) && month > 0 ? month - 1 : 0);
            ret.set(Calendar.DAY_OF_MONTH, PtCommon.isNotEmpty(date) ? date : 1);

            ret.set(Calendar.HOUR_OF_DAY, PtCommon.isNotEmpty(hourOfDay) ? hourOfDay : 0);
            ret.set(Calendar.MINUTE, PtCommon.isNotEmpty(minute) ? minute : 0);
            ret.set(Calendar.SECOND, PtCommon.isNotEmpty(second) ? second : 0);

            ret.set(Calendar.MILLISECOND, PtCommon.isNotEmpty(millSeconds) ? millSeconds : 0);

            return ret.getTime();
        }
        else {
            return null;
        }
    }

    public static Date toDate(Object value) {
        return TypeUtils.castToDate(value);
    }

    public static Date toDate(Object value, String format) {
        return TypeUtils.castToDate(value, format);
    }

    public static Calendar toCalendar(Object value) {
        Calendar ret = Calendar.getInstance();
        ret.setTime(TypeUtils.castToDate(value));
        return ret;
    }

    public static Boolean toBoolean(Object value) {
        if (value instanceof String) {
            //判断是否是 XX.000这种格式
            if (pattern.matcher((String) value).matches()) {
                return TypeUtils.castToBoolean(TypeUtils.castToDouble(value));
            }
        }
        return TypeUtils.castToBoolean(value);
    }

    public static <T> T jsonStringToJavaObject(String jsonString, Class<T> clazz) {
        return TypeUtils.castToJavaBean(PtCommon.toJsonObject(jsonString), clazz);
    }

    public static <T> T toJavaObject(Object value, Class<T> clazz) {
        return TypeUtils.castToJavaBean(value, clazz);
    }

    public static <T> T toJavaObject(JSONObject value, Class<T> clazz) {
        return value.toJavaObject(clazz);
    }

    public static <T> List<T> toJavaList(String jsonArrayStr, Class<T> clazz) {
        return JSON.parseArray(jsonArrayStr, clazz);
    }

    public static <T> List<T> toJavaList(JSONArray value, Class<T> clazz) {
        return value.toJavaList(clazz);
    }

    public static JSONObject toJsonObject(Object value) {
        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }
        else {
            return (JSONObject) JSONObject.toJSON(value);
        }
    }

    public static JSONObject toJsonObject(String value) {
        return JSONObject.parseObject(value);
    }

    public static JSONArray toJsonArray(Object value) {
        return (JSONArray) JSONArray.toJSON(value);
    }

    public static JSONArray toJsonArray(String value) {
        return JSONArray.parseArray(value);
    }

    public static boolean isEmpty(Collection<?> value) {
        if (value != null) {
            return value.isEmpty();
        }
        else {
            return true;
        }
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return StringUtils.isEmpty((String) value);
        }
        else if (value instanceof JSONObject) {
            return ((JSONObject) value).isEmpty();
        }
        else if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        else if (value instanceof JSONArray) {
            return ((JSONArray) value).isEmpty();
        }
        else if (value instanceof Page) {
            return ((Page<?>) value).isEmpty();
        }
        else if (value.getClass().isArray()) {
            return Array.getLength(value) < 1;
        }
        else {
            return false;
        }
    }

    public static boolean isNotEmpty(Collection<?> value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    public static boolean isNull(Object value) {
        return Objects.isNull(value);
    }

    public static boolean isNotNull(Object value) {
        return !isNull(value);
    }

    public static boolean equalsIgnoreCase(String obj1, String obj2) {
        return StringUtils.equalsIgnoreCase(obj1, obj2);
    }

    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 instanceof Collection && obj2 instanceof Collection) {
            return CollectionUtils.isEqualCollection((Collection) obj1, (Collection) obj2);
        }

        return Objects.equals(obj1, obj2);
    }

    public static boolean equalsAny(Object obj1, Object... objs) {
        if (objs == null || objs.length == 0) {
            return false;
        }
        for (Object tempObj : objs) {
            if (equals(obj1, tempObj)) {
                return true;
            }
        }
        return false;
    }

    public static boolean notEqualsIgnoreCase(String obj1, String obj2) {
        return !StringUtils.equalsIgnoreCase(obj1, obj2);
    }

    public static boolean notEquals(Object obj1, Object obj2) {
        return !Objects.equals(obj1, obj2);
    }

    public static boolean notEqualsAll(Object obj1, Object... objs) {
        if (objs == null || objs.length == 0) {
            return true;
        }
        for (Object tempObj : objs) {
            if (equals(obj1, tempObj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 普通的Trim 同时还去除space中的字符
     *
     * @param value String
     * @return String String
     */
    public static String trim(String value, String space) {
        if (value == null) {
            return "";
        }
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();    /* avoid getfield opcode */

        while ((st < len)
                && ((val[st] <= ' ') || (space.indexOf(val[st]) != -1))) {
            st++;
        }
        while ((st < len)
                && ((val[len - 1] <= ' ') || (space.indexOf(val[len - 1]) != -1))) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    public static String trimLeft(String value, String space) {
        if (value == null) {
            return "";
        }
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();    /* avoid getfield opcode */

        while ((st < len)
                && ((val[st] <= ' ') || (space.indexOf(val[st]) != -1))) {
            st++;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    public static String trimRight(String value, String space) {
        if (value == null) {
            return "";
        }
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();    /* avoid getfield opcode */

        while ((st < len)
                && ((val[len - 1] <= ' ') || (space.indexOf(val[len - 1]) != -1))) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    /**
     * 普通的Trim 默认去除前后空格
     *
     * @param value String
     * @return String String
     */
    public static String trim(String value) {
        return trim(value, " ");
    }

    public static List<String> split(String str, String separatorChars) {
        if (PtCommon.isNotEmpty(str)) {
            return Arrays.stream(StringUtils.split(str, separatorChars)).filter(PtCommon::isNotEmpty).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public static List<String> splitByWholeSeparator(String str, String separatorChars) {
        if (PtCommon.isNotEmpty(str)) {
            return Arrays.stream(StringUtils.splitByWholeSeparator(str, separatorChars)).filter(PtCommon::isNotEmpty).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public static <T> T[] subarray(final T[] array, int off, int len) {
        if (array == null) {
            return null;
        }

        int startIndexInclusive = off;
        int endIndexExclusive = off + len;
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        final Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            @SuppressWarnings("unchecked") // OK, because array is of type T
            final T[] emptyArray = (T[]) Array.newInstance(type, 0);
            return emptyArray;
        }
        @SuppressWarnings("unchecked") // OK, because array is of type T
        final T[] subarray = (T[]) Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static byte[] subarray(final byte[] array, int off, int len) {
        if (array == null) {
            return null;
        }

        int startIndexInclusive = off;
        int endIndexExclusive = off + len;
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        final byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }

    public static Integer compare(Date obj1, Date obj2) {
        if (obj1 == obj2) {
            return 0;
        }
        else if (obj1 == null) {
            return -1;
        }
        else if (obj2 == null) {
            return 1;
        }
        return obj1.compareTo(obj2);
    }

    /**
     * 深层拷贝
     *
     * @param obj 对象
     * @return 返回深度复制的对象
     * @throws Exception 异常
     */
    public static <T> T copy(T obj) {
        //是否实现了序列化接口，即使该类实现了，他拥有的对象未必也有...
        if (Serializable.class.isAssignableFrom(obj.getClass())) {
            //如果子类没有继承该接口，这一步会报错
            try {
                return copyImplSerializable(obj);
            }
            catch (Exception e) {
                //这里不处理，会运行到下面的尝试json
            }
        }
        //如果序列化失败，尝试json序列化方式
        try {
            JSONObject temp = toJsonObject(obj);
            return (T) toJavaObject(temp, obj.getClass());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T copyImplSerializable(T obj) throws Exception {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        Object o = null;
        //如果子类没有继承该接口，这一步会报错
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);

            o = ois.readObject();
            return (T) o;
        }
        catch (Exception e) {
            throw new Exception("对象中包含没有继承序列化的对象");
        }
        finally {
            IoUtil.close(baos);
            IoUtil.close(oos);
            IoUtil.close(bais);
            IoUtil.close(ois);
        }
    }


    /**
     * 正则表达式匹配
     *
     * @param string:  待被匹配的字符串
     * @param pattern: 正则表达式
     */
    public static String regex(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

}
