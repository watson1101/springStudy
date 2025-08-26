package com.hong.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生命实体类
 * 记录每个真灵的每一世的信息
 */
@Data
@TableName("t_user")
public class LifeUser {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 真灵ID，关联meta_user表
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
    
    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}