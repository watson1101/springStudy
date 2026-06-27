package hong.com.common.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 所有领域实体的基类，包含创建/修改的审计字段
 * 遵循 DDD 设计，为领域实体提供公共属性
 *
 * @author admin
 * @since 2026-06-22
 */
@Getter
@Setter
public class BaseEntity {

    /** 创建人（默认 admin） */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 修改人（默认 admin） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
