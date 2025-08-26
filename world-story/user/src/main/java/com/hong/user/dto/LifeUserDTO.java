package com.hong.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生命用户数据传输对象
 */
@Data
public class LifeUserDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 真灵ID
     */
    private Long metaUserId;
    
    /**
     * 第几世
     */
    private Integer lifeCount;
    
    /**
     * 所属世界：haven-天界，human-人间，ghost-地府
     */
    private String worldType;
    
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    
    /**
     * 出生年（天干地支格式，如甲辰年）
     */
    private String birthYearCn;
    
    /**
     * 出生月（农历）
     */
    private String birthMonthCn;
    
    /**
     * 出生日（农历）
     */
    private String birthDayCn;
    
    /**
     * 出生时辰（如子时）
     */
    private String birthHourCn;
    
    /**
     * 物种
     */
    private String species;
    
    /**
     * 天命值
     */
    private Long destinyValue;
    
    /**
     * 厄运值
     */
    private Long misfortuneValue;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}