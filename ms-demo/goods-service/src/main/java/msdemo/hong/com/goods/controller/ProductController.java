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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "商品管理", description = "商品的增删改查、库存管理、批量查询等接口")
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
    @Operation(summary = "创建商品", description = "创建新的商品信息，返回雪花算法生成的商品ID")
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
    @Operation(summary = "更新商品", description = "根据商品ID更新商品信息，支持部分字段更新")
    @PutMapping("/{productId}")
    public Result<Boolean> updateProduct(
            @Parameter(description = "商品ID（雪花算法生成)", required = true, example = "10000000001")
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
    @Operation(summary = "删除商品", description = "逻辑删除商品，将 is_deleted 字段置为1")
    @DeleteMapping("/{productId}")
    public Result<Boolean> deleteProduct(
            @Parameter(description = "商品ID（雪花算法生成)", required = true, example = "10000000001")
            @PathVariable("productId") Long productId) {
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
    @Operation(summary = "批量删除商品", description = "根据商品ID列表批量逻辑删除商品")
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
    @Operation(summary = "查询商品详情", description = "根据商品ID查询商品详细信息，包含折扣、可用库存等衍生字段")
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductById(
            @Parameter(description = "商品ID（雪花算法生成)", required = true, example = "10000000001")
            @PathVariable("productId") Long productId) {
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
    @Operation(summary = "根据编码查询商品", description = "根据商品编码（SKU）查询商品详细信息")
    @GetMapping("/code/{productCode}")
    public Result<ProductVO> getProductByCode(
            @Parameter(description = "商品编码（SKU）", required = true, example = "IP15PM256")
            @PathVariable("productCode") String productCode) {
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
    @Operation(summary = "分页查询商品列表", description = "支持按商品名称、分类、品牌、价格区间等多维度筛选和分页查询")
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
    @Operation(summary = "批量查询商品（供内部服务调用）", description = "根据商品ID列表批量查询商品信息，供订单服务等其他微服务调用")
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
    @Operation(summary = "冻结库存", description = "下单时调用，将库存从可用库存转移到冻结库存")
    @PostMapping("/stock/freeze")
    public Result<Boolean> freezeStock(
            @Parameter(description = "商品ID", required = true) @RequestParam("productId") Long productId,
            @Parameter(description = "冻结数量", required = true) @RequestParam("quantity") Integer quantity) {
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
    @Operation(summary = "扣减库存", description = "支付成功后调用，从冻结库存中扣减")
    @PostMapping("/stock/deduct")
    public Result<Boolean> deductStock(
            @Parameter(description = "商品ID", required = true) @RequestParam("productId") Long productId,
            @Parameter(description = "扣减数量", required = true) @RequestParam("quantity") Integer quantity) {
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
    @Operation(summary = "释放冻结库存", description = "订单取消或超时时调用，将冻结库存释放回可用库存")
    @PostMapping("/stock/release")
    public Result<Boolean> releaseFrozenStock(
            @Parameter(description = "商品ID", required = true) @RequestParam("productId") Long productId,
            @Parameter(description = "释放数量", required = true) @RequestParam("quantity") Integer quantity) {
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