package msdemo.hong.com.goods.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * <p>对应数据库表：goods_product</p>
 *
 * <h3>ID生成说明</h3>
 * <p>主键ID使用雪花算法（Snowflake Algorithm）生成，类型为 Long 型 64 位整数。</p>
 * <ul>
 *   <li><b>ID生成策略</b>：{@link IdType#ASSIGN_ID} — MyBatis Plus 自动通过雪花算法生成</li>
 *   <li><b>数据库类型</b>：BIGINT（对应 Java 的 Long 类型）</li>
 *   <li><b>JSON序列化</b>：使用 {@link ToStringSerializer} 将 Long 转 String，防止前端 JS 精度丢失</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
@TableName("goods_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     *
     * <p>使用雪花算法生成，全局唯一且趋势递增。</p>
     * <ul>
     *   <li>MyBatis Plus 的 {@code ASSIGN_ID} 策略自动调用 {@link com.baomidou.mybatisplus.core.toolkit.IdWorker} 生成</li>
     *   <li>也可手动调用 {@link msdemo.hong.com.common.util.SnowflakeIdUtil#nextId()} 获取</li>
     * </ul>
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品编码（SKU）
     */
    private String productCode;

    /**
     * 商品分类ID
     *
     * <p>关联分类表的雪花算法ID。</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    /**
     * 商品分类名称
     */
    private String categoryName;

    /**
     * 商品品牌
     */
    private String brand;

    /**
     * 商品价格（单位：元）
     */
    private BigDecimal price;

    /**
     * 商品原价（用于显示折扣）
     */
    private BigDecimal originalPrice;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 冻结库存（下单未支付）
     */
    private Integer frozenStock;

    /**
     * 商品状态：0-下架 1-上架 2-售罄
     */
    private Integer status;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 商品详细描述
     */
    private String description;

    /**
     * 商品规格参数
     */
    private String specification;

    /**
     * 商品图片（JSON格式存储多个图片URL）
     */
    private String images;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 商品评分（0.00-5.00）
     */
    private BigDecimal rating;

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
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}