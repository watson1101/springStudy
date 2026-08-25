package com.ms.learn.system.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时根据开关决定是否启动 binlog 同步
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BinlogSyncRunner implements ApplicationRunner {

    private final BinlogSyncService binlogSyncService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("系统启动, 检查 A股资讯增量同步开关...");
        binlogSyncService.syncWithConfig();
    }
}
