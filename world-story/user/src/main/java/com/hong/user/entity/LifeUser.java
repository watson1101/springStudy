package com.hong.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生命实体类
 * 记录每个真灵的每一世的信息
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetaUserId() {
        return metaUserId;
    }

    public void setMetaUserId(Long metaUserId) {
        this.metaUserId = metaUserId;
    }

    public Integer getLifeCount() {
        return lifeCount;
    }

    public void setLifeCount(Integer lifeCount) {
        this.lifeCount = lifeCount;
    }

    public String getWorldType() {
        return worldType;
    }

    public void setWorldType(String worldType) {
        this.worldType = worldType;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthYearCn() {
        return birthYearCn;
    }

    public void setBirthYearCn(String birthYearCn) {
        this.birthYearCn = birthYearCn;
    }

    public String getBirthMonthCn() {
        return birthMonthCn;
    }

    public void setBirthMonthCn(String birthMonthCn) {
        this.birthMonthCn = birthMonthCn;
    }

    public String getBirthDayCn() {
        return birthDayCn;
    }

    public void setBirthDayCn(String birthDayCn) {
        this.birthDayCn = birthDayCn;
    }

    public String getBirthHourCn() {
        return birthHourCn;
    }

    public void setBirthHourCn(String birthHourCn) {
        this.birthHourCn = birthHourCn;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Long getDestinyValue() {
        return destinyValue;
    }

    public void setDestinyValue(Long destinyValue) {
        this.destinyValue = destinyValue;
    }

    public Long getMisfortuneValue() {
        return misfortuneValue;
    }

    public void setMisfortuneValue(Long misfortuneValue) {
        this.misfortuneValue = misfortuneValue;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}