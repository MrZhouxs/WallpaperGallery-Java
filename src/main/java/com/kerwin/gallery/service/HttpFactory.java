package com.kerwin.gallery.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.*;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public interface HttpFactory extends JsoupFactory {

    /**
     * GET请求，json格式
     *
     * @param url     请求的URL
     * @param json    body数据
     * @param headers 自定义请求头
     * @return HttpResponse类型的返回数据
     */
    default HttpResponse get(String url, String json, Map<String, String> headers, List<HttpCookie> cookies) {
        return request(Method.GET, url, json, headers, cookies).execute();
    }

    /**
     * GET请求，form格式
     *
     * @param url     请求的URL
     * @param form    form数据
     * @param headers 自定义请求头
     * @return HttpResponse类型的返回数据
     */
    default HttpResponse get(String url, Map<String, Object> form, Map<String, String> headers, List<HttpCookie> cookies) {
        return request(Method.GET, url, form, headers, cookies).execute();
    }

    /**
     * POST请求，json格式
     *
     * @param url     请求的URL
     * @param json    body数据
     * @param headers 自定义请求头
     * @return HttpResponse类型的返回数据
     */
    default HttpResponse post(String url, String json, Map<String, String> headers, List<HttpCookie> cookies) {
        return request(Method.POST, url, json, headers, cookies).execute();
    }

    /**
     * POST请求，form格式
     *
     * @param url     请求的URL
     * @param form    form数据
     * @param headers 自定义请求头
     * @return HttpResponse类型的返回数据
     */
    default HttpResponse post(String url, Map<String, Object> form, Map<String, String> headers, List<HttpCookie> cookies) {
        return request(Method.POST, url, form, headers, cookies).execute();
    }

    /**
     * 创建HttpRequest请求体
     *
     * @param method  请求方法
     * @param url     请求的URL
     * @param json    body数据
     * @param headers 自定义请求头
     * @return HttpRequest类型的返回数据
     */
    default HttpRequest request(Method method, String url, String json, Map<String, String> headers, List<HttpCookie> cookies) {
        return HttpRequest.of(url).method(method).addHeaders(headers).cookie(cookies).body(json).timeout(0);
    }

    /**
     * 创建HttpRequest请求体
     *
     * @param method  请求方法
     * @param url     请求的URL
     * @param form    form数据
     * @param headers 自定义请求头
     * @return HttpRequest类型的返回数据
     */
    default HttpRequest request(Method method, String url, Map<String, Object> form, Map<String, String> headers, List<HttpCookie> cookies) {
        return HttpRequest.of(url).method(method).addHeaders(headers).cookie(cookies).form(form).timeout(0);
    }

    /**
     * 自定义请求体请求数据
     *
     * @param request 自定义请求体
     * @return HttpResponse类型的返回数据
     */
    default HttpResponse request(HttpRequest request) {
        return request.execute();
    }

    /**
     * 上传文件
     * <p>
     * Example:
     * HashMap<String, Object> params = new HashMap<>();
     * params.put("file", new File("D:/123.png");
     * uploadFile(url, params);
     *
     * @param url    请求的URL
     * @param params 文件请求体
     * @return 返回的string类型数据信息
     */
    default String uploadFile(String url, HashMap<String, Object> params) {
        return HttpUtil.post(url, params);
    }

    /**
     * 下载文件（此方法采用流的方式读写，内存中只是保留一定量的缓存，然后分块写入硬盘，因此大文件情况下不会对内存有压力）
     *
     * @param url     请求的URL
     * @param dstFile 文件保存的路径
     * @return 下载的文件大小
     */
    default Long download(String url, File dstFile) {
        return HttpUtil.downloadFile(url, dstFile, -1);
    }

    /**
     * 下载文件（此方法采用流的方式读写，内存中只是保留一定量的缓存，然后分块写入硬盘，因此大文件情况下不会对内存有压力）
     * Example:
     * <p>
     * download(url, new File("D:/"), new StreamProgress(){
     *
     * @param url      请求的URL
     * @param dstFile  文件保存的路径
     * @param progress 自定义下载进度接口
     * @return 下载的文件大小
     * @Override public void start() {
     * Console.log("开始下载。。。。");
     * }
     * @Override public void progress(long progressSize) {
     * Console.log("已下载：{}", FileUtil.readableFileSize(progressSize));
     * }
     * @Override public void finish() {
     * Console.log("下载完成！");
     * }
     * });
     */
    default Long download(String url, File dstFile, StreamProgress progress) {
        return HttpUtil.downloadFile(url, dstFile, progress);
    }

    /**
     * 通过Headers中的Content-Disposition获取文件名
     */
    default String getFileNameFromDisposition(HttpResponse response) {
        String fileName = null;
        final String disposition = response.header(Header.CONTENT_DISPOSITION);
        if (StrUtil.isNotBlank(disposition)) {
            fileName = ReUtil.get("filename=\"(.*?)\"", disposition, 1);
            if (StrUtil.isBlank(fileName)) {
                fileName = StrUtil.subAfter(disposition, "filename=", true);
            }
        }
        return fileName;
    }

    /**
     * 从响应头补全下载文件名
     * 如果response返回头没有 Header.CONTENT_DISPOSITION，则默认从URL中获取文件名
     */
    default String completeFileNameFromHeader(String url, HttpResponse response) throws MalformedURLException {
        String fileName = this.getFileNameFromDisposition(response);
        if (StrUtil.isBlank(fileName)) {
            URL u = new URL(url);
            final String path = u.getPath();
            // 从路径中获取文件名
            fileName = StrUtil.subSuf(path, path.lastIndexOf('/') + 1);
            if (StrUtil.isBlank(fileName)) {
                // 编码后的路径做为文件名
                fileName = URLUtil.encodeQuery(path, CharsetUtil.CHARSET_UTF_8);
            } else {
                // issue#I4K0FS@Gitee
                fileName = URLUtil.decode(fileName, CharsetUtil.CHARSET_UTF_8);
            }
        }
        return fileName;
    }

    default File completeFilepath(File savePath, String imageName) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return FileUtil.file(savePath, String.format("%d-%d", year, month), String.valueOf(day), imageName);
    }

    default File downloadImage(String url, File savePath) throws IOException {
        HttpResponse response = this.get(url, new HashMap<>(), new HashMap<>(), new ArrayList<>());
        return this.downloadImage(url, response, savePath);
    }

    default File downloadImage(String url, HttpResponse response, File savePath) throws IOException {
        if (response.getStatus() == 403) {
            return null;
        }
        File imageSave;
        if (savePath.isFile()) {
            imageSave = savePath;
        } else {
            String imageName = this.completeFileNameFromHeader(url, response);
            imageSave = this.completeFilepath(savePath, imageName);
        }
        if (!imageSave.getParentFile().exists()) {
            imageSave.getParentFile().mkdirs();
        }
        // 二进制文件落地，图片如果很大可能存在内存溢出问题
        FileCopyUtils.copy(response.bodyStream(), new FileOutputStream(imageSave));
        return imageSave;
    }
}
