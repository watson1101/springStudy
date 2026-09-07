package com.ms.learn.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典项实体（支持三级层级：level 1/2/3，parent_id=0 表示一级）
 */
@Data
@TableName("sys_dict_item")
public class SysDictItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属字典类型ID(sys_dict.id) */
    private Long dictId;

    /** 字典项值(业务键) */
    private String itemValue;

    /** 字典项显示名称 */
    private String itemLabel;

    /** 父级ID, 0=一级 */
    private Long parentId;

    /** 层级 1/2/3 */
    private Integer level;

    /** 排序,越小越靠前 */
    private Integer sort;

    /** 状态 1启用 0停用 */
    private Integer status;

    /** 软删除标记 0正常 1删除 */
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 非表字段：子级列表（构建树时使用） */
    @TableField(exist = false)
    private java.util.List<SysDictItem> children;
}
