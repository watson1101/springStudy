package com.ms.learn.hotnews.collector.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ms.learn.hotnews.collector.config.CollectProperties;
import com.ms.learn.hotnews.collector.dto.ToutiaoHotItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 今日头条热榜抓取客户端
 *
 * <p>使用 JDK17 内置 HttpClient（无额外重依赖），请求官方热榜接口并解析为条目列表。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToutiaoClient {

    private final CollectProperties props;

    /**
     * 抓取头条热榜
     *
     * @return 热榜条目列表（按接口返回顺序，即榜单排名顺序）
     */
    public List<ToutiaoHotItem> fetchHotBoard() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(props.getTimeout()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(props.getUrl()))
                .timeout(Duration.ofMillis(props.getTimeout()))
                .header("User-Agent", props.getUserAgent())
                .header("Accept", "application/json, text/plain, */*")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("抓取头条热榜失败，HTTP " + response.statusCode());
        }

        String body = response.body();
        JSONObject root = JSON.parseObject(body);
        JSONArray data = root.getJSONArray("data");
        List<ToutiaoHotItem> items = new ArrayList<>();
        if (data == null) {
            return items;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONObject o = data.getJSONObject(i);
            ToutiaoHotItem item = new ToutiaoHotItem();
            item.setClusterId(o.getString("ClusterId"));
            item.setTitle(o.getString("Title"));
            item.setHotValue(o.getString("HotValue"));
            item.setUrl(o.getString("Url"));
            items.add(item);
        }
        log.info("抓取头条热榜成功，共 {} 条", items.size());
        return items;
    }
}
