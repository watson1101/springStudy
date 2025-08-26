package com.hong.heavenwill.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 随机事件实体类
 * 对应数据库表：t_random_event
 */
@Data
@TableName("t_random_event")
public class RandomEvent {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型
     * 1: 天灾
     * 2: 宝物现世
     * 3: 仙人降临
     * 4: 神兽出没
     * 5: 灵气潮汐
     */
    private Integer eventType;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 事件发生地点
     */
    private String location;

    /**
     * 影响范围
     */
    private String influenceRange;

    /**
     * 发生时间
     */
    private LocalDateTime occurTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 事件状态
     * 0: 未发生
     * 1: 活跃中
     * 2: 已结束
     */
    private Integer status;

    /**
     * 事件概率
     */
    private Double probability;

    /**
     * 影响因子（JSON格式存储）
     * 包含各种影响事件结果的参数
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> influenceFactors;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}