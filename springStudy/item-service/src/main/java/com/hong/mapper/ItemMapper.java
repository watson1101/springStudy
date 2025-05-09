package com.hong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.domain.ItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper extends BaseMapper<ItemVO> {

    List<ItemVO> selectByIds(@Param("ids") List<Long> ids);


}
