package msdemo.hong.com.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import msdemo.hong.com.goods.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 *
 * <p>用于商品表的数据访问操作。继承 {@link BaseMapper} 获得基础的 CRUD 方法。</p>
 * <p>自定义查询方法使用雪花算法生成的 Long 型 ID 作为参数。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据商品编码查询商品
     *
     * @param productCode 商品编码（SKU，全局唯一）
     * @return 商品实体
     */
    Product selectByProductCode(@Param("productCode") String productCode);

    /**
     * 批量查询商品信息
     *
     * @param productIds 商品ID列表（雪花算法Long型ID）
     * @return 商品列表
     */
    List<Product> selectByIds(@Param("productIds") List<Long> productIds);

    /**
     * 扣减库存
     *
     * <p>SQL: {@code UPDATE goods_product SET stock_quantity = stock_quantity - #{quantity}
     * WHERE id = #{productId} AND stock_quantity >= #{quantity}}</p>
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  扣减数量
     * @return 影响行数（0表示库存不足或商品不存在）
     */
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 释放冻结库存
     *
     * <p>SQL: {@code UPDATE goods_product SET frozen_stock = frozen_stock - #{quantity},
     * stock_quantity = stock_quantity + #{quantity} WHERE id = #{productId} AND frozen_stock >= #{quantity}}</p>
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  释放数量
     * @return 影响行数
     */
    int releaseFrozenStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 冻结库存
     *
     * <p>SQL: {@code UPDATE goods_product SET frozen_stock = frozen_stock + #{quantity},
     * stock_quantity = stock_quantity - #{quantity} WHERE id = #{productId} AND stock_quantity >= #{quantity}}</p>
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  冻结数量
     * @return 影响行数
     */
    int freezeStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}