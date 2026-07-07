package msdemo.hong.com.goods.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品返回VO
 *
 * <p>用于向前端返回商品详细信息，包含衍生字段（如折扣、可用库存、状态描述）。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
@Schema(description = "商品信息（包含衍生字段）")
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID（雪花算法生成）
     *
     * <p>使用 {@link ToStringSerializer} 序列化为字符串，
     * 避免前端 JavaScript 处理 Large Long 时的精度丢失问题。</p>
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "商品ID（雪花算法生成）", example = "10000000001")
    private Long id;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "Apple iPhone 15")
    private String productName;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 商品分类ID（雪花算法生成）
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
     * 商品价格
     */
    @Schema(description = "商品价格（单位：元）", example = "5999.00")
    private BigDecimal price;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 折扣（计算得出）
     */
    private BigDecimal discount;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 冻结库存
     */
    private Integer frozenStock;

    /**
     * 可用库存（库存数量 - 冻结库存）
     */
    @Schema(description = "可用库存", example = "50")
    private Integer availableStock;

    /**
     * 商品状态：0-下架 1-上架 2-售罄
     */
    @Schema(description = "商品状态：0-下架 1-上架 2-售罄", example = "1")
    private Integer status;

    /**
     * 商品状态描述
     */
    @Schema(description = "状态描述", example = "上架")
    private String statusDesc;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品规格
     */
    private String specification;

    /**
     * 商品图片
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
     * 商品评分
     */
    private BigDecimal rating;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}