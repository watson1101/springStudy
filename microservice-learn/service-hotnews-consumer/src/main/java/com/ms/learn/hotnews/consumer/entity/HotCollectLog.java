package com.ms.learn.hotnews.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采集批次日志（对应 ms_ds_hotnews.hot_collect_log）
 */
@Data
@TableName("hot_collect_log")
public class HotCollectLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 采集批次ID */
    private String batchId;

    /** 来源 */
    private String source;

    /** 本次采集条数 */
    private Integer totalCount;

    /** 是否成功 1成功 0失败 */
    private Integer success;

    /** 错误信息 */
    private String errorMsg;

    /** 耗时(毫秒) */
    private Long costMs;

    /** 采集时间 */
    private LocalDateTime collectTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
