package hong.com.memo.infrastructure.converter;

import hong.com.memo.domain.entity.Memo;
import hong.com.memo.domain.types.MemoStatus;
import hong.com.memo.domain.types.MemoType;
import hong.com.memo.infrastructure.persistence.MemoPO;
import org.springframework.stereotype.Component;

/**
 * 备忘对象转换器 - 领域实体 ↔ 持久化对象
 */
@Component
public class MemoConverter {

    public Memo toDomain(MemoPO po) {
        if (po == null) return null;
        Memo domain = new Memo();
        domain.setId(po.getId());
        domain.setUserId(po.getUserId());
        domain.setTitle(po.getTitle());
        domain.setContent(po.getContent());
        domain.setMemoType(MemoType.fromCode(po.getMemoType()));
        domain.setBackgroundImage(po.getBackgroundImage());
        domain.setRemindTime(po.getRemindTime());
        domain.setCronExpression(po.getCronExpression());
        domain.setStatus(MemoStatus.fromCode(po.getStatus()));
        domain.setSortOrder(po.getSortOrder());
        domain.setDeleted(po.getDeleted());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setCreatedTime(po.getCreatedTime());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setUpdatedTime(po.getUpdatedTime());
        return domain;
    }

    public MemoPO toPO(Memo domain) {
        if (domain == null) return null;
        MemoPO po = new MemoPO();
        po.setId(domain.getId());
        po.setUserId(domain.getUserId());
        po.setTitle(domain.getTitle());
        po.setContent(domain.getContent());
        po.setMemoType(domain.getMemoType() != null ? domain.getMemoType().getCode() : 1);
        po.setBackgroundImage(domain.getBackgroundImage());
        po.setRemindTime(domain.getRemindTime());
        po.setCronExpression(domain.getCronExpression());
        po.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : 1);
        po.setSortOrder(domain.getSortOrder());
        po.setDeleted(domain.getDeleted());
        po.setCreatedBy(domain.getCreatedBy());
        po.setCreatedTime(domain.getCreatedTime());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setUpdatedTime(domain.getUpdatedTime());
        return po;
    }
}
