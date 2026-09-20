package com.ms.learn.hotnews.collector.dto;

import lombok.Data;

/**
 * 今日头条热榜原始响应条目（对应接口返回的 data 数组元素）
 */
@Data
public class ToutiaoHotItem {

    /** 榜单唯一ID */
    private String ClusterId;

    /** 标题 */
    private String Title;

    /** 热度值（字符串，可能为空） */
    private String HotValue;

    /** 详情链接 */
    private String Url;
}
