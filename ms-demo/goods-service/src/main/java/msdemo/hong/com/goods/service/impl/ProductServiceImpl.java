package msdemo.hong.com.goods.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.common.exception.BusinessException;
import msdemo.hong.com.common.model.result.ResultCode;
import msdemo.hong.com.goods.mapper.ProductMapper;
import msdemo.hong.com.goods.model.dto.*;
import msdemo.hong.com.goods.model.entity.Product;
import msdemo.hong.com.goods.model.vo.ProductVO;
import msdemo.hong.com.goods.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * <p>实现商品相关的核心业务逻辑。</p>
 *
 * <h3>ID生成说明</h3>
 * <p>商品ID使用雪花算法生成，通过 MyBatis Plus 的 {@code ASSIGN_ID} 策略自动生成。
 * 也可手动调用 {@link msdemo.hong.com.common.util.SnowflakeIdUtil#nextId()} 获取雪花ID。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /** 商品状态：下架 */
    private static final Integer STATUS_OFFLINE = 0;

    /** 商品状态：上架 */
    private static final Integer STATUS_ONLINE = 1;

    /** 商品状态：售罄 */
    private static final Integer STATUS_SOLD_OUT = 2;

    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductCreateDTO dto) {
        log.info("创建商品，商品编码: {}", dto.getProductCode());

        // 检查商品编码是否已存在
        Product existingProduct = productMapper.selectByProductCode(dto.getProductCode());
        if (existingProduct != null) {
            throw new BusinessException(ResultCode.PRODUCT_CODE_EXISTS);
        }

        // 创建商品实体
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);

        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(STATUS_ONLINE);
        }
        if (product.getFrozenStock() == null) {
            product.setFrozenStock(0);
        }
        if (product.getSalesCount() == null) {
            product.setSalesCount(0);
        }
        if (product.getCommentCount() == null) {
            product.setCommentCount(0);
        }
        if (product.getIsDeleted() == null) {
            product.setIsDeleted(0);
        }

        // 插入数据库
        // 主键ID由 MyBatis Plus 的 ASSIGN_ID 策略自动通过雪花算法生成
        // 也可显式调用: product.setId(SnowflakeIdUtil.nextId());
        int result = productMapper.insert(product);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_CREATE_ERROR);
        }

        log.info("商品创建成功，商品ID: {}, 商品编码: {}", product.getId(), dto.getProductCode());
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProduct(Long productId, ProductUpdateDTO dto) {
        log.info("更新商品，商品ID: {}", productId);

        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null || existingProduct.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 更新商品信息
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);
        product.setId(productId);

        int result = productMapper.updateById(product);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_UPDATE_ERROR);
        }

        log.info("商品更新成功，商品ID: {}", productId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProduct(Long productId) {
        log.info("删除商品，商品ID: {}", productId);

        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null || existingProduct.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 逻辑删除
        Product product = new Product();
        product.setId(productId);
        product.setIsDeleted(1);

        int result = productMapper.updateById(product);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_DELETE_ERROR);
        }

        log.info("商品删除成功，商品ID: {}", productId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteProducts(ProductBatchDeleteDTO dto) {
        log.info("批量删除商品，商品ID列表: {}", dto.getProductIds());

        // 批量逻辑删除
        int result = productMapper.deleteBatchIds(dto.getProductIds());

        log.info("批量删除商品成功，删除数量: {}", result);
        return result > 0;
    }

    @Override
    public ProductVO getProductById(Long productId) {
        log.info("查询商品，商品ID: {}", productId);

        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        return convertToVO(product);
    }

    @Override
    public ProductVO getProductByCode(String productCode) {
        log.info("查询商品，商品编码: {}", productCode);

        Product product = productMapper.selectByProductCode(productCode);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        return convertToVO(product);
    }

    @Override
    public Page<ProductVO> getProductPage(ProductQueryDTO dto) {
        log.info("分页查询商品列表，页码: {}, 每页大小: {}", dto.getCurrent(), dto.getSize());

        // 构建查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 商品名称模糊查询
        if (StrUtil.isNotBlank(dto.getProductName())) {
            wrapper.like(Product::getProductName, dto.getProductName());
        }

        // 分类ID
        if (dto.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, dto.getCategoryId());
        }

        // 品牌
        if (StrUtil.isNotBlank(dto.getBrand())) {
            wrapper.eq(Product::getBrand, dto.getBrand());
        }

        // 状态
        if (dto.getStatus() != null) {
            wrapper.eq(Product::getStatus, dto.getStatus());
        }

        // 价格区间
        if (dto.getMinPrice() != null) {
            wrapper.ge(Product::getPrice, BigDecimal.valueOf(dto.getMinPrice()));
        }
        if (dto.getMaxPrice() != null) {
            wrapper.le(Product::getPrice, BigDecimal.valueOf(dto.getMaxPrice()));
        }

        // 商品ID列表
        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
            wrapper.in(Product::getId, dto.getProductIds());
        }

        // 未删除
        wrapper.eq(Product::getIsDeleted, 0);

        // 按创建时间降序
        wrapper.orderByDesc(Product::getCreateTime);

        // 分页查询
        Page<Product> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<Product> productPage = productMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ProductVO> voPage = new Page<>();
        BeanUtil.copyProperties(productPage, voPage);
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public List<ProductVO> getProductsByIds(List<Long> productIds) {
        log.info("批量查询商品，商品ID列表: {}", productIds);

        if (productIds == null || productIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Product> products = productMapper.selectByIds(productIds);
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean freezeStock(Long productId, Integer quantity) {
        log.info("冻结库存，商品ID: {}, 数量: {}", productId, quantity);

        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 检查库存是否充足
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        // 冻结库存
        int result = productMapper.freezeStock(productId, quantity);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        log.info("库存冻结成功，商品ID: {}, 数量: {}", productId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deductStock(Long productId, Integer quantity) {
        log.info("扣减库存，商品ID: {}, 数量: {}", productId, quantity);

        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 检查冻结库存是否充足
        if (product.getFrozenStock() < quantity) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        // 扣减库存
        int result = productMapper.deductStock(productId, quantity);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        log.info("库存扣减成功，商品ID: {}, 数量: {}", productId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean releaseFrozenStock(Long productId, Integer quantity) {
        log.info("释放冻结库存，商品ID: {}, 数量: {}", productId, quantity);

        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 检查冻结库存是否充足
        if (product.getFrozenStock() < quantity) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        // 释放冻结库存
        int result = productMapper.releaseFrozenStock(productId, quantity);
        if (result <= 0) {
            throw new BusinessException(ResultCode.PRODUCT_STOCK_ERROR);
        }

        log.info("冻结库存释放成功，商品ID: {}, 数量: {}", productId, quantity);
        return true;
    }

    /**
     * 将实体转换为VO
     *
     * @param product 商品实体
     * @return 商品VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtil.copyProperties(product, vo);

        // 计算折扣
        if (product.getOriginalPrice() != null && product.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = product.getPrice()
                    .divide(product.getOriginalPrice(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.TEN);
            vo.setDiscount(discount);
        }

        // 计算可用库存
        vo.setAvailableStock(product.getStockQuantity() - product.getFrozenStock());

        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(product.getStatus()));

        // 售罄状态处理
        if (product.getStatus() == STATUS_ONLINE && vo.getAvailableStock() <= 0) {
            vo.setStatus(STATUS_SOLD_OUT);
            vo.setStatusDesc("售罄");
        }

        return vo;
    }

    /**
     * 获取商品状态描述
     *
     * @param status 状态码
     * @return 状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        if (status.equals(STATUS_OFFLINE)) {
            return "下架";
        }
        if (status.equals(STATUS_ONLINE)) {
            return "上架";
        }
        if (status.equals(STATUS_SOLD_OUT)) {
            return "售罄";
        }
        return "未知";
    }
}