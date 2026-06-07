package msdemo.hong.com.goods.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品批量删除DTO
 *
 * <p>用于接收批量删除商品的请求参数。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
public class ProductBatchDeleteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID列表
     *
     * <p>使用雪花算法生成的Long型ID列表，非空且至少包含一个元素。</p>
     */
    @NotNull(message = "商品ID列表不能为空")
    @NotEmpty(message = "请至少选择一个商品")
    private List<Long> productIds;
}