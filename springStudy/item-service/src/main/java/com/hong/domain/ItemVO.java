package com.hong.domain;

import lombok.Data;

@Data
public class ItemVO {
    private Long id;
    private String name;
    private String image;
    private String price;
    private String description;
    private String stock;
}
