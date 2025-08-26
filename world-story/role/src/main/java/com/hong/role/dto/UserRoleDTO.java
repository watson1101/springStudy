package com.hong.role.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联数据传输对象
 */
@Data
public class UserRoleDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}