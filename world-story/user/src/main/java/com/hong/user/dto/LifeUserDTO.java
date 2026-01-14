package com.hong.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生命用户数据传输对象
 */
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
}