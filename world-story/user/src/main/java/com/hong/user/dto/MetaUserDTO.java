package com.hong.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 真灵数据传输对象
 */
@Data
public class MetaUserDTO {
    
    /**
     * 真灵ID
     */
    private Long id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}