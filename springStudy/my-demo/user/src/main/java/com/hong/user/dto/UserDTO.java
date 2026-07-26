package com.hong.user.dto;

import com.hong.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseEntity {
    private String username;
    private String password;
}