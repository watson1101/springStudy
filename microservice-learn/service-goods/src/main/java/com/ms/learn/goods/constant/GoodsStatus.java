package com.ms.learn.goods.constant;

/**
 * 商品状态常量
 */
public final class GoodsStatus {

    private GoodsStatus() {
    }

    /** 草稿 */
    public static final int DRAFT = 0;

    /** 上架 */
    public static final int ON_SHELF = 1;

    /** 下架 */
    public static final int OFF_SHELF = 2;
}
