package msdemo.hong.com.goods.model.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * 商品查询请求DTO
 *
 * <p>用于接收查询商品列表时的请求参数，支持多条件筛选和分页。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
@Schema(description = "商品分页查询请求参数")
public class ProductQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称（模糊查询）
     */
    @Schema(description = "商品名称（模糊查询）", example = "iPhone")
    private String productName;

    /**
     * 商品分类ID（雪花算法生成的Long型ID）
     */
    @Schema(description = "商品分类ID", example = "20000000001")
    private Long categoryId;

    /**
     * 商品品牌
     */
    private String brand;

    /**
     * 商品状态：0-下架 1-上架 2-售罄
     */
    private Integer status;

    /**
     * 最低价格（单位：分，用于价格区间筛选）
     */
    private Long minPrice;

    /**
     * 最高价格（单位：分，用于价格区间筛选）
     */
    private Long maxPrice;

    /**
     * 商品ID列表（批量查询时使用）
     *
     * <p>如果传入了此参数，则按ID列表精确查询，忽略其他筛选条件。</p>
     */
    private List<Long> productIds;

    /**
     * 当前页码（默认第1页）
     */
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    /**
     * 每页大小（默认10条）
     */
    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;
}