package com.ms.learn.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 异常日志记录器 —— 按配置把异常写入「文件」和/或「数据库」。
 * <p>文件路径部署时须挂载到宿主机目录，避免只留在容器内。</p>
 * <p>单个文件超过 maxFileSizeMb 时自动滚动，主文件重新开始写。</p>
 */
@Slf4j
@Component
public class ExceptionLogRecorder {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter ROLL_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final ExceptionLogProperties properties;
    private final ExceptionLogDbWriter dbWriter;

    public ExceptionLogRecorder(ExceptionLogProperties properties,
                                ObjectProvider<ExceptionLogDbWriter> dbWriterProvider) {
        this.properties = properties;
        this.dbWriter = dbWriterProvider.getIfAvailable();
    }

    /**
     * 记录一条异常日志，按开关分别落文件与数据库。
     * 两路互相隔离：一路失败不影响另一路，也不影响主业务返回。
     */
    public void record(ExceptionLog exceptionLog) {
        if (!properties.isEnabled() || exceptionLog == null) {
            return;
        }
        if (properties.getFile().isEnabled()) {
            writeToFile(exceptionLog);
        }
        if (properties.getDb().isEnabled() && dbWriter != null) {
            writeToDb(exceptionLog);
        }
        if (properties.getAlert().isEnabled()) {
            // 告警扩展点：暂不实现具体处理
            log.debug("[exception-log] 告警开关已开启，但告警处理暂未实现");
        }
    }

    private void writeToFile(ExceptionLog e) {
        try {
            Path dir = Paths.get(properties.getFile().getPath());
            Files.createDirectories(dir);
            Path file = resolveActiveFile(dir);
            String line = buildFileLine(e);
            Files.write(file, line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            // 文件写失败不影响落库与业务
            log.warn("[exception-log] 写入异常日志文件失败: {}", ex.getMessage());
        }
    }

    /**
     * 解析当前应写入的文件：未超限用主文件；达到上限则把主文件归档为带时间戳的文件，
     * 主文件重新开始写入。
     */
    private Path resolveActiveFile(Path dir) throws IOException {
        String baseName = properties.getFile().getFileName();
        Path main = dir.resolve(baseName);
        long maxBytes = properties.getFile().getMaxFileSizeMb() * 1024L * 1024L;

        if (!Files.exists(main)) {
            return main;
        }
        if (maxBytes <= 0 || Files.size(main) < maxBytes) {
            return main;
        }
        String stamp = LocalDateTime.now().format(ROLL_TS);
        Path archived = dir.resolve(baseName + "." + stamp);
        Files.move(main, archived);
        log.info("[exception-log] 日志文件已达 {}MB 上限，已滚动为 {}",
                properties.getFile().getMaxFileSizeMb(), archived.getFileName());
        return main;
    }

    private String buildFileLine(ExceptionLog e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getOccurTime() == null ? "" : e.getOccurTime().format(FILE_TS))
                .append(" | service=").append(nvl(e.getServiceName()))
                .append(" | type=").append(nvl(e.getExceptionType()))
                .append(" | uri=").append(nvl(e.getRequestMethod())).append(' ').append(nvl(e.getRequestUri()))
                .append(" | ip=").append(nvl(e.getIp()))
                .append(" | userId=").append(nvl(e.getUserId()))
                .append(" | msg=").append(nvl(e.getMessage()))
                .append(System.lineSeparator());
        if (e.getStackTrace() != null) {
            sb.append(e.getStackTrace()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void writeToDb(ExceptionLog e) {
        try {
            dbWriter.insert(e);
        } catch (Exception ex) {
            // 落库失败不影响文件与业务
            log.warn("[exception-log] 异常日志落库失败: {}", ex.getMessage());
        }
    }

    private String nvl(String s) {
        return s == null ? "-" : s;
    }
}
