package com.hong.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemDTO {
    private Long id;

    @ApiModelProperty("商品名称")
    private String name;

    private String image;

    private String price;

    private String description;

    private String stock;


}
