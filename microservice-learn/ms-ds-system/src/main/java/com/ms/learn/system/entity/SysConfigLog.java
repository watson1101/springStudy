package com.ms.learn.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置变更日志实体(审计)
 */
@Data
@TableName("sys_config_log")
public class SysConfigLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置ID */
    private Long configId;

    /** 修改前值 */
    private String oldValue;

    /** 修改后值 */
    private String newValue;

    /** 操作人 */
    private String operator;

    /** 操作类型 add/update/delete */
    private String opType;

    private LocalDateTime createTime;
}
