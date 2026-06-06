package com.hong.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("item")
public class ItemVO {
    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private String description;
    private String stock;
}
