package com.hong.user.vo;

import com.hong.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserVO extends BaseEntity {
    private Long id;
    private String username;
    private Integer status;
    private Integer isAdmin;
}