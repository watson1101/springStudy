package hong.com.user.infrastructure.converter;

import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.types.UserRole;
import hong.com.user.infrastructure.persistence.UserInfoPO;
import org.springframework.stereotype.Component;

/**
 * 用户对象转换器，领域实体 ↔ 持久化对象
 */
@Component
public class UserConverter {

    public UserInfo toDomain(UserInfoPO po) {
        if (po == null) return null;
        UserInfo domain = new UserInfo();
        domain.setId(po.getId());
        domain.setUsername(po.getUsername());
        domain.setPassword(po.getPassword());
        domain.setNickname(po.getNickname());
        domain.setEmail(po.getEmail());
        domain.setPhone(po.getPhone());
        domain.setAvatar(po.getAvatar());
        domain.setRole(UserRole.fromCode(po.getRole()));
        domain.setStatus(po.getStatus());
        domain.setMultiDeviceLogin(po.getMultiDeviceLogin());
        domain.setDeleted(po.getDeleted());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setCreatedTime(po.getCreatedTime());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setUpdatedTime(po.getUpdatedTime());
        return domain;
    }

    public UserInfoPO toPO(UserInfo domain) {
        if (domain == null) return null;
        UserInfoPO po = new UserInfoPO();
        po.setId(domain.getId());
        po.setUsername(domain.getUsername());
        po.setPassword(domain.getPassword());
        po.setNickname(domain.getNickname());
        po.setEmail(domain.getEmail());
        po.setPhone(domain.getPhone());
        po.setAvatar(domain.getAvatar());
        po.setRole(domain.getRole() != null ? domain.getRole().getCode() : UserRole.USER.getCode());
        po.setStatus(domain.getStatus());
        po.setMultiDeviceLogin(domain.getMultiDeviceLogin());
        po.setDeleted(domain.getDeleted());
        po.setCreatedBy(domain.getCreatedBy());
        po.setCreatedTime(domain.getCreatedTime());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setUpdatedTime(domain.getUpdatedTime());
        return po;
    }
}
