package com.ms.learn.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 */
@Data
@TableName("sys_config")
public class SysConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属分组ID */
    private Long groupId;

    /** 配置键(唯一) */
    private String configKey;

    /** 配置名称 */
    private String configName;

    /** 配置值 */
    private String configValue;

    /** 值类型 string/int/bool/json */
    private String valueType;

    /** 状态 1启用 0停用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 软删除标记 0正常 1删除 */
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
