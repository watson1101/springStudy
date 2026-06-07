package msdemo.hong.com.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import msdemo.hong.com.goods.model.dto.*;
import msdemo.hong.com.goods.model.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 *
 * <p>定义商品相关的核心业务操作。</p>
 * <p>所有ID参数使用 {@link Long} 类型（雪花算法生成的64位分布式唯一ID）。</p>
 *
 * @author hong
 * @since 1.0.0
 */
public interface ProductService {

    /**
     * 创建商品
     *
     * @param dto 商品创建请求
     * @return 商品ID（雪花算法生成）
     */
    Long createProduct(ProductCreateDTO dto);

    /**
     * 更新商品信息
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param dto       商品更新请求
     * @return 是否成功
     */
    Boolean updateProduct(Long productId, ProductUpdateDTO dto);

    /**
     * 删除商品（逻辑删除）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @return 是否成功
     */
    Boolean deleteProduct(Long productId);

    /**
     * 批量删除商品
     *
     * @param dto 批量删除请求
     * @return 是否成功
     */
    Boolean batchDeleteProducts(ProductBatchDeleteDTO dto);

    /**
     * 根据ID查询商品
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @return 商品信息
     */
    ProductVO getProductById(Long productId);

    /**
     * 根据商品编码查询商品
     *
     * @param productCode 商品编码（字符串SKU）
     * @return 商品信息
     */
    ProductVO getProductByCode(String productCode);

    /**
     * 分页查询商品列表
     *
     * @param dto 查询条件
     * @return 商品分页列表
     */
    Page<ProductVO> getProductPage(ProductQueryDTO dto);

    /**
     * 批量查询商品信息（供订单服务调用）
     *
     * @param productIds 商品ID列表（雪花算法Long型ID）
     * @return 商品列表
     */
    List<ProductVO> getProductsByIds(List<Long> productIds);

    /**
     * 冻结库存（供订单服务调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    Boolean freezeStock(Long productId, Integer quantity);

    /**
     * 扣减库存（支付成功后调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    Boolean deductStock(Long productId, Integer quantity);

    /**
     * 释放冻结库存（订单取消/超时调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    Boolean releaseFrozenStock(Long productId, Integer quantity);
}