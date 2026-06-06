package com.hong.api.client.fallback;

import com.hong.api.client.ItemClient;
import com.hong.api.dto.ItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ItemClientFallback implements FallbackFactory<ItemClient> {

    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {

            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("queryItemByIds error: {}", cause.getMessage());
                // 创建失败，返回空集合
                return null;
            }
        };
    }
}
