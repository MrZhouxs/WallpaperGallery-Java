package com.kerwin.gallery.service;

import cn.hutool.core.net.SSLUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface JsoupFactory {

    default JXDocument jXDocument(String url) throws IOException {
        Document doc = document(url);
        return JXDocument.create(doc);
    }

    default JXDocument jXDocument(Document doc) throws IOException {
        return JXDocument.create(doc);
    }

    default Document document(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        connection.timeout(0);
        return connection.get();
    }

    default Document documentPost(String url, Map<String, String> data, Map<String, String> headers) throws IOException {
        Connection connection = Jsoup.connect(url);
        connection.timeout(0);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            connection.data(entry.getKey(), entry.getValue());
        }
        connection.headers(headers);
        return connection.post();
    }

    default Document document(String url, String headerKey, String headerVal) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(headerKey, headerVal);
        return documentWithHeaders(url, headers);
    }

    default Document documentWithHeaders(String url, Map<String, String> headers) throws IOException {
        return document(url, headers, new HashMap<>());
    }

    default Document documentWithCookies(String url, Map<String, String> cookies) throws IOException {
        return document(url, new HashMap<>(), cookies);
    }

    default Document document(String url, Map<String, String> headers, Map<String, String> cookies) throws IOException {
        Connection connection = Jsoup.connect(url)
                .timeout(0)
                // 解决Cloudflare TLS指纹问题
                .sslSocketFactory(SSLUtil.createSSLContext("TLSv1.1").getSocketFactory())
                .cookies(cookies)
                .headers(headers)
                .followRedirects(true);
        return connection.get();
    }

    /**
     * 睡眠
     * @param sleep     睡眠时间，以秒为单位
     */
    default void sleep(long sleep) {
        ThreadUtil.safeSleep(sleep * 1000);
    }

    default void sleep(float sleep) {
        ThreadUtil.safeSleep(sleep * 1000);
    }
}
