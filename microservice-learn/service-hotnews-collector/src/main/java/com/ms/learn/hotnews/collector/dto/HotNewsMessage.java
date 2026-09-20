package com.ms.learn.hotnews.collector.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投递到 RocketMQ 的热榜消息体（采集端 → 消费端）
 */
@Data
public class HotNewsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 头条唯一ID */
    private String clusterId;

    /** 标题 */
    private String title;

    /** 热度值 */
    private Long hotValue;

    /** 榜单排名（从 1 开始） */
    private Integer rankNo;

    /** 来源 */
    private String source;

    /** 详情链接 */
    private String url;

    /** 采集批次ID */
    private String batchId;

    /** 采集时间 */
    private LocalDateTime collectTime;
}
