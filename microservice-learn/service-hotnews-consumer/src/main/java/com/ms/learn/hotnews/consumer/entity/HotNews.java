package com.ms.learn.hotnews.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 热榜数据（对应 ms_ds_hotnews.hot_news）
 *
 * <p>主键使用雪花算法（IdType.ASSIGN_ID）。</p>
 */
@Data
@TableName("hot_news")
public class HotNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 头条热榜唯一ID(ClusterId) */
    private String clusterId;

    /** 热点标题 */
    private String title;

    /** 热度值 */
    private Long hotValue;

    /** 榜单排名(从1开始) */
    private Integer rankNo;

    /** 来源 */
    private String source;

    /** 详情链接 */
    private String url;

    /** 采集批次ID */
    private String batchId;

    /** 本条采集时间 */
    private LocalDateTime collectTime;

    /** 软删除标记 0正常 1删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
