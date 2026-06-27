package hong.com.memo.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备忘 Mapper 接口
 */
@Mapper
public interface MemoMapper extends BaseMapper<MemoPO> {
}
