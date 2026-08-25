package com.ms.learn.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置分组实体
 */
@Data
@TableName("sys_config_group")
public class SysConfigGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分组编码(唯一) */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 排序 */
    private Integer sort;

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
