package com.ms.learn.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.transaction.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
