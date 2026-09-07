package com.ms.learn.goods.feign.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 字典项 VO（对应 ms-ds-system 的 SysDictItem）
 */
@Data
public class DictItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 所属字典类型ID */
    private Long dictId;

    /** 字典项值(业务键) */
    private String itemValue;

    /** 字典项显示名称 */
    private String itemLabel;

    /** 父级ID, 0=一级 */
    private Long parentId;

    /** 层级 1/2/3 */
    private Integer level;

    /** 排序 */
    private Integer sort;

    /** 状态 1启用 0停用 */
    private Integer status;

    /** 子级列表（树结构时填充） */
    private List<DictItemVO> children;
}
