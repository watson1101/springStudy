package msdemo.hong.com.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.common.exception.BusinessException;
import msdemo.hong.com.common.model.result.Result;
import msdemo.hong.com.common.model.result.ResultCode;
import msdemo.hong.com.goods.model.dto.*;
import msdemo.hong.com.goods.model.vo.ProductVO;
import msdemo.hong.com.goods.service.ProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商品控制器
 *
 * <p>提供商品的增删改查接口。</p>
 * <p>所有ID参数使用 {@link Long} 类型（雪花算法生成的64位分布式唯一ID）。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * 创建商品
     *
     * @param dto 商品创建请求
     * @return 商品ID（雪花算法生成的Long型ID）
     */
    @PostMapping("/create")
    public Result<Long> createProduct(@Valid @RequestBody ProductCreateDTO dto) {
        log.info("创建商品请求: {}", dto);
        try {
            Long productId = productService.createProduct(dto);
            return Result.success("商品创建成功", productId);
        } catch (BusinessException e) {
            log.error("创建商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建商品异常", e);
            return Result.error(ResultCode.PRODUCT_CREATE_ERROR);
        }
    }

    /**
     * 更新商品
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param dto       商品更新请求
     * @return 是否成功
     */
    @PutMapping("/{productId}")
    public Result<Boolean> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductUpdateDTO dto) {
        log.info("更新商品请求，商品ID: {}", productId);
        try {
            Boolean result = productService.updateProduct(productId, dto);
            return Result.success("商品更新成功", result);
        } catch (BusinessException e) {
            log.error("更新商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新商品异常", e);
            return Result.error(ResultCode.PRODUCT_UPDATE_ERROR);
        }
    }

    /**
     * 删除商品
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @return 是否成功
     */
    @DeleteMapping("/{productId}")
    public Result<Boolean> deleteProduct(@PathVariable("productId") Long productId) {
        log.info("删除商品请求，商品ID: {}", productId);
        try {
            Boolean result = productService.deleteProduct(productId);
            return Result.success("商品删除成功", result);
        } catch (BusinessException e) {
            log.error("删除商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除商品异常", e);
            return Result.error(ResultCode.PRODUCT_DELETE_ERROR);
        }
    }

    /**
     * 批量删除商品
     *
     * @param dto 批量删除请求
     * @return 是否成功
     */
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteProducts(@Valid @RequestBody ProductBatchDeleteDTO dto) {
        log.info("批量删除商品请求: {}", dto.getProductIds());
        try {
            Boolean result = productService.batchDeleteProducts(dto);
            return Result.success("批量删除成功", result);
        } catch (BusinessException e) {
            log.error("批量删除商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("批量删除商品异常", e);
            return Result.error(ResultCode.PRODUCT_DELETE_ERROR);
        }
    }

    /**
     * 查询商品详情
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @return 商品信息
     */
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductById(@PathVariable("productId") Long productId) {
        log.info("查询商品详情请求，商品ID: {}", productId);
        try {
            ProductVO productVO = productService.getProductById(productId);
            return Result.success(productVO);
        } catch (BusinessException e) {
            log.error("查询商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询商品异常", e);
            return Result.error(ResultCode.PRODUCT_NOT_FOUND);
        }
    }

    /**
     * 根据商品编码查询商品
     *
     * @param productCode 商品编码（字符串SKU）
     * @return 商品信息
     */
    @GetMapping("/code/{productCode}")
    public Result<ProductVO> getProductByCode(@PathVariable("productCode") String productCode) {
        log.info("根据编码查询商品请求，商品编码: {}", productCode);
        try {
            ProductVO productVO = productService.getProductByCode(productCode);
            return Result.success(productVO);
        } catch (BusinessException e) {
            log.error("查询商品失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询商品异常", e);
            return Result.error(ResultCode.PRODUCT_NOT_FOUND);
        }
    }

    /**
     * 分页查询商品列表
     *
     * @param dto 查询条件
     * @return 商品分页列表
     */
    @PostMapping("/page")
    public Result<Page<ProductVO>> getProductPage(@RequestBody ProductQueryDTO dto) {
        log.info("分页查询商品列表请求: {}", dto);
        try {
            Page<ProductVO> page = productService.getProductPage(dto);
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页查询商品列表异常", e);
            return Result.error(ResultCode.ERROR);
        }
    }

    /**
     * 批量查询商品（内部接口，供其他服务调用）
     *
     * @param productIds 商品ID列表（雪花算法Long型ID）
     * @return 商品列表
     */
    @PostMapping("/list")
    public Result<List<ProductVO>> getProductsByIds(@RequestBody List<Long> productIds) {
        log.info("批量查询商品请求，商品ID列表: {}", productIds);
        try {
            List<ProductVO> products = productService.getProductsByIds(productIds);
            return Result.success(products);
        } catch (Exception e) {
            log.error("批量查询商品异常", e);
            return Result.error(ResultCode.ERROR);
        }
    }

    /**
     * 冻结库存（内部接口，供订单服务调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/freeze")
    public Result<Boolean> freezeStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("冻结库存请求，商品ID: {}, 数量: {}", productId, quantity);
        try {
            Boolean result = productService.freezeStock(productId, quantity);
            return Result.success("库存冻结成功", result);
        } catch (BusinessException e) {
            log.error("冻结库存失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("冻结库存异常", e);
            return Result.error(ResultCode.PRODUCT_STOCK_ERROR);
        }
    }

    /**
     * 扣减库存（内部接口，供订单服务调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/deduct")
    public Result<Boolean> deductStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("扣减库存请求，商品ID: {}, 数量: {}", productId, quantity);
        try {
            Boolean result = productService.deductStock(productId, quantity);
            return Result.success("库存扣减成功", result);
        } catch (BusinessException e) {
            log.error("扣减库存失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("扣减库存异常", e);
            return Result.error(ResultCode.PRODUCT_STOCK_ERROR);
        }
    }

    /**
     * 释放冻结库存（内部接口，供订单服务调用）
     *
     * @param productId 商品ID（雪花算法生成的Long型ID）
     * @param quantity  数量
     * @return 是否成功
     */
    @PostMapping("/stock/release")
    public Result<Boolean> releaseFrozenStock(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("释放冻结库存请求，商品ID: {}, 数量: {}", productId, quantity);
        try {
            Boolean result = productService.releaseFrozenStock(productId, quantity);
            return Result.success("冻结库存释放成功", result);
        } catch (BusinessException e) {
            log.error("释放冻结库存失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("释放冻结库存异常", e);
            return Result.error(ResultCode.PRODUCT_STOCK_ERROR);
        }
    }
}