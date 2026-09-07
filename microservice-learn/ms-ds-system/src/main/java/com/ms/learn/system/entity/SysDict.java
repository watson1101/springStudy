package com.ms.learn.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典类型实体
 */
@Data
@TableName("sys_dict")
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型编码(唯一), 如 GOODS_CATEGORY */
    private String dictType;

    /** 字典名称, 如 商品分类 */
    private String dictName;

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
