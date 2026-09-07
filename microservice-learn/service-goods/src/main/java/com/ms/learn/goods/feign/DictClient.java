package com.ms.learn.goods.feign;

import com.ms.learn.common.result.Result;
import com.ms.learn.goods.feign.vo.DictItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 数据字典 Feign 客户端（调用 ms-ds-system）
 */
@FeignClient(name = "ms-ds-system", contextId = "dictClient", fallback = DictClientFallback.class)
public interface DictClient {

    /** 按 dict_type 查询字典项扁平列表 */
    @GetMapping("/api/system/dict/type/{dictType}")
    Result<List<DictItemVO>> listItems(@PathVariable("dictType") String dictType);

    /** 按 dict_type 查询字典项三级树 */
    @GetMapping("/api/system/dict/tree/{dictType}")
    Result<List<DictItemVO>> listItemTree(@PathVariable("dictType") String dictType);

    /** 按字典项ID查询 */
    @GetMapping("/api/system/dict/item/{itemId}")
    Result<DictItemVO> getItem(@PathVariable("itemId") Long itemId);
}
