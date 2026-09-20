package com.ms.learn.hotnews.consumer.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消费端接收的热榜消息体（与采集端 HotNewsMessage 结构一致）
 */
@Data
public class HotNewsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clusterId;
    private String title;
    private Long hotValue;
    private Integer rankNo;
    private String source;
    private String url;
    private String batchId;
    private LocalDateTime collectTime;
}
