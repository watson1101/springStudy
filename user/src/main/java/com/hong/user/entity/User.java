package com.hong.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String salt;
    private Integer status; // 0: 禁用, 1: 启用
    private Integer isAdmin; // 0: 普通用户, 1: 管理员
    @TableLogic
    private Integer deleted; // 逻辑删除字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}