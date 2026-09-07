package com.ms.learn.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体（对应表 ms_ds_goods.ms_ds_goods）
 * <p>分类 category_id 对应 ms-ds-system 字典 GOODS_CATEGORY 的三级字典项ID。</p>
 */
@Data
@TableName("ms_ds_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    private String name;

    /** 三级分类ID（对应 sys_dict_item.id，dict_type=GOODS_CATEGORY, level=3） */
    private Long categoryId;

    /** 价格（元） */
    private BigDecimal price;

    /** 主图 URL */
    private String mainImage;

    /** 多图列表（JSON 数组字符串，如 ["url1","url2"]） */
    private String images;

    /** 规格（JSON 字符串，如 {"颜色":"红色","尺寸":"L"}） */
    private String specs;

    /** 商品描述 */
    private String description;

    /** 状态：0=草稿 1=上架 2=下架 */
    private Integer status;

    /** 库存数量 */
    private Integer stock;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
