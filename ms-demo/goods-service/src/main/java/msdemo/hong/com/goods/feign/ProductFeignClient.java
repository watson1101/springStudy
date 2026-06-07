package msdemo.hong.com.goods.feign;

import msdemo.hong.com.goods.model.vo.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务Feign客户端
 *
 * <p>供订单服务调用的Feign接口，提供服务间的远程调用能力。</p>
 * <p>所有ID参数使用 {@link Long} 类型（雪花算法生成的64位ID）。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@FeignClient(
    name = "goods-service",
    path = "/product",
    fallback = ProductFeignClient.ProductFallback.class
)
public interface ProductFeignClient {

    /**
     * 批量查询商品
     *
     * @param productIds 商品ID列表（雪花算法Long型ID）
     * @return 商品列表
     */
    @PostMapping("/list")
    List<ProductVO> getProductsByIds(@RequestBody List<Long> productIds);

    /**
     * 查询单个商品
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @return 商品信息
     */
    @GetMapping("/{productId}")
    ProductVO getProductById(@PathVariable("productId") Long productId);

    /**
     * 冻结库存
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/freeze")
    Boolean freezeStock(
        @RequestParam("productId") Long productId,
        @RequestParam("quantity") Integer quantity
    );

    /**
     * 扣减库存
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/deduct")
    Boolean deductStock(
        @RequestParam("productId") Long productId,
        @RequestParam("quantity") Integer quantity
    );

    /**
     * 释放冻结库存
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/release")
    Boolean releaseFrozenStock(
        @RequestParam("productId") Long productId,
        @RequestParam("quantity") Integer quantity
    );

    /**
     * 降级处理类
     *
     * <p>当商品服务不可用时，执行降级逻辑。</p>
     */
    class ProductFallback implements ProductFeignClient {

        @Override
        public List<ProductVO> getProductsByIds(List<Long> productIds) {
            throw new RuntimeException("商品服务暂时不可用，请稍后再试");
        }

        @Override
        public ProductVO getProductById(Long productId) {
            throw new RuntimeException("商品服务暂时不可用，请稍后再试");
        }

        @Override
        public Boolean freezeStock(Long productId, Integer quantity) {
            throw new RuntimeException("商品服务暂时不可用，请稍后再试");
        }

        @Override
        public Boolean deductStock(Long productId, Integer quantity) {
            throw new RuntimeException("商品服务暂时不可用，请稍后再试");
        }

        @Override
        public Boolean releaseFrozenStock(Long productId, Integer quantity) {
            throw new RuntimeException("商品服务暂时不可用，请稍后再试");
        }
    }
}