package msdemo.hong.com.goods.model.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品创建请求DTO
 *
 * <p>用于接收创建商品时的请求参数</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
@Schema(description = "商品创建请求参数")
public class ProductCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Apple iPhone 15")
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String productName;

    /**
     * 商品编码（SKU）
     */
    @NotBlank(message = "商品编码不能为空")
    @Size(max = 100, message = "商品编码长度不能超过100个字符")
    private String productCode;

    /**
     * 商品分类ID
     */
    @NotBlank(message = "商品分类ID不能为空")
    private String categoryId;

    /**
     * 商品分类名称
     */
    @NotBlank(message = "商品分类名称不能为空")
    @Size(max = 100, message = "商品分类名称长度不能超过100个字符")
    private String categoryName;

    /**
     * 商品品牌
     */
    @Size(max = 100, message = "商品品牌长度不能超过100个字符")
    private String brand;

    /**
     * 商品价格
     */
    @Schema(description = "商品价格（单位：元）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5999.00")
    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    @Schema(description = "库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "库存数量不能为空")
    private Integer stockQuantity;

    /**
     * 商品状态：0-下架 1-上架 2-售罄
     */
    private Integer status;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品规格
     */
    @Size(max = 500, message = "商品规格长度不能超过500个字符")
    private String specification;

    /**
     * 商品图片（JSON格式）
     */
    private String images;
}
