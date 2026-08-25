package com.ms.learn.system.sync;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.ms.learn.system.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * binlog 增量同步服务
 * 监听 Mac 源库 OPENCLAW_A_STOCK 的增删改, 实时写入 Ubuntu 目标库
 * 开关: sys_config 表 sync.a_stock.enabled
 */
@Slf4j
@Service
public class BinlogSyncService {

    private final BinlogSyncProperties props;
    private final SysConfigService configService;
    private final JdbcTemplate targetJdbc; // 操作目标库(Ubuntu MySQL)
    private final JdbcTemplate sourceJdbc; // 源库连接(用于读取表结构)

    private final AtomicBoolean running = new AtomicBoolean(false);
    private BinaryLogClient client;
    private Thread listenerThread;

    /** 表名 -> 列名列表(缓存, 解决 binlog 行数据只有值没有列名的问题) */
    private final Map<String, List<String>> tableColumns = new HashMap<>();

    /** 表名 -> 列名 -> 列类型(用于类型转换, enum 存完整定义) */
    private final Map<String, Map<String, String>> tableColumnTypes = new HashMap<>();

    /** 表名 -> 表ID(binlog tableMap 关联用) */
    private final Map<Long, String> tableNameById = new HashMap<>();

    public BinlogSyncService(BinlogSyncProperties props,
                             SysConfigService configService,
                             @org.springframework.beans.factory.annotation.Qualifier("targetJdbcTemplate") JdbcTemplate targetJdbc,
                             @org.springframework.beans.factory.annotation.Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbc) {
        this.props = props;
        this.configService = configService;
        this.targetJdbc = targetJdbc;
        this.sourceJdbc = sourceJdbc;
    }

    /**
     * 检查开关并启动/停止
     */
    public void syncWithConfig() {
        boolean enabled = configService.getBoolean("sync.a_stock.enabled", false);
        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    /**
     * 启动 binlog 监听(幂等)
     */
    public synchronized void start() {
        if (running.get()) {
            log.info("binlog 同步已在运行, 忽略重复启动");
            return;
        }
        try {
            loadTableColumns();
            BinaryLogClient c = new BinaryLogClient(
                    props.getSource().getHost(),
                    props.getSource().getPort(),
                    props.getSource().getUsername(),
                    props.getSource().getPassword());
            c.setServerId(props.getListen().getServerId());
            c.registerEventListener(this::handleEvent);

            // 用独立线程运行监听, 避免阻塞主线程/Spring刷新
            Thread t = new Thread(() -> {
                try {
                    running.set(true);
                    log.info("binlog 同步监听已启动: {}:{} -> 目标库 {}",
                            props.getSource().getHost(), props.getSource().getPort(), props.getTarget().getDatabase());
                    c.connect();
                } catch (Exception e) {
                    log.error("binlog 连接失败: {}", e.getMessage(), e);
                    running.set(false);
                }
            }, "a-stock-binlog-sync");
            t.setDaemon(false);
            this.listenerThread = t;
            this.client = c;
            t.start();
        } catch (Exception e) {
            log.error("启动 binlog 同步失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 停止 binlog 监听(幂等)
     */
    public synchronized void stop() {
        if (!running.get() && client == null) {
            log.info("binlog 同步未在运行");
            return;
        }
        running.set(false);
        if (client != null) {
            try {
                client.disconnect();
            } catch (Exception ignore) {
            }
            client = null;
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
        log.info("binlog 同步已停止");
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * 从源库加载 10 张表的列名
     */
    private void loadTableColumns() {
        String db = props.getSource().getDatabase();
        List<Map<String, Object>> cols = sourceJdbc.queryForList(
                "SELECT TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE, COLUMN_TYPE " +
                        "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? ORDER BY ORDINAL_POSITION", db);
        Map<String, Map<Integer, String>> ordered = new HashMap<>();
        tableColumnTypes.clear();
        for (Map<String, Object> row : cols) {
            String table = (String) row.get("TABLE_NAME");
            String column = (String) row.get("COLUMN_NAME");
            int pos = ((Number) row.get("ORDINAL_POSITION")).intValue();
            String dataType = (String) row.get("DATA_TYPE");
            String columnType = (String) row.get("COLUMN_TYPE");
            ordered.computeIfAbsent(table, k -> new HashMap<>()).put(pos, column);
            // 记录完整类型信息(enum 需记全定义, 用于索引->值转换)
            tableColumnTypes.computeIfAbsent(table, k -> new HashMap<>()).put(column, columnType != null ? columnType : dataType);
        }
        tableColumns.clear();
        ordered.forEach((table, posMap) -> {
            List<String> colList = new ArrayList<>();
            posMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEach(e -> colList.add(e.getValue()));
            tableColumns.put(table, colList);
            log.info("加载源库表结构: {} -> {} 列", table, colList.size());
        });
    }

    /**
     * 处理 binlog 事件
     */
    private void handleEvent(Event event) {
        if (!running.get()) {
            return;
        }
        EventData data = event.getData();
        EventType type = event.getHeader().getEventType();

        // 记录表名映射(解决 tableId -> 表名)
        if (type == EventType.TABLE_MAP && data instanceof TableMapEventData) {
            TableMapEventData t = (TableMapEventData) data;
            if (props.getSource().getDatabase().equalsIgnoreCase(t.getDatabase())) {
                tableNameById.put(t.getTableId(), t.getTable());
            }
            return;
        }

        if (data instanceof WriteRowsEventData) {
            WriteRowsEventData w = (WriteRowsEventData) data;
            insertRows(w.getTableId(), w.getRows());
        } else if (data instanceof UpdateRowsEventData) {
            UpdateRowsEventData u = (UpdateRowsEventData) data;
            updateRows(u.getTableId(), u.getRows());
        } else if (data instanceof DeleteRowsEventData) {
            DeleteRowsEventData d = (DeleteRowsEventData) data;
            deleteRows(d.getTableId(), d.getRows());
        }
    }

    private void insertRows(long tableId, List<Serializable[]> rows) {
        String table = tableNameById.get(tableId);
        if (table == null || rows == null || rows.isEmpty()) {
            return;
        }
        List<String> cols = tableColumns.get(table);
        if (cols == null) {
            log.warn("未找到表 {} 的列结构, 跳过 INSERT", table);
            return;
        }
        Map<String, String> colTypes = tableColumnTypes.getOrDefault(table, Map.of());
        for (Serializable[] row : rows) {
            String sql = null;
            try {
                sql = buildInsertSql(table, cols, colTypes, row);
                targetJdbc.update(sql);
                log.debug("[INSERT→{}] {}", table, sql);
            } catch (Exception e) {
                log.error("[INSERT] 表 {} 同步失败: {} | SQL={} | cause={}", table, e.getMessage(), sql, String.valueOf(getRootMessage(e)));
            }
        }
    }

    private void updateRows(long tableId, List<Map.Entry<Serializable[], Serializable[]>> rows) {
        String table = tableNameById.get(tableId);
        if (table == null || rows == null || rows.isEmpty()) {
            return;
        }
        List<String> cols = tableColumns.get(table);
        if (cols == null) {
            log.warn("未找到表 {} 的列结构, 跳过 UPDATE", table);
            return;
        }
        Map<String, String> colTypes = tableColumnTypes.getOrDefault(table, Map.of());
        for (Map.Entry<Serializable[], Serializable[]> entry : rows) {
            Serializable[] oldRow = entry.getKey();
            Serializable[] newRow = entry.getValue();
            try {
                String sql = buildUpdateSql(table, cols, colTypes, oldRow, newRow);
                if (sql == null) {
                    continue;
                }
                targetJdbc.update(sql);
                log.debug("[UPDATE→{}] {}", table, sql);
            } catch (Exception e) {
                log.error("[UPDATE] 表 {} 同步失败: {} | SQL={}", table, e.getMessage());
            }
        }
    }

    private void deleteRows(long tableId, List<Serializable[]> rows) {
        String table = tableNameById.get(tableId);
        if (table == null || rows == null || rows.isEmpty()) {
            return;
        }
        List<String> cols = tableColumns.get(table);
        if (cols == null) {
            log.warn("未找到表 {} 的列结构, 跳过 DELETE", table);
            return;
        }
        Map<String, String> colTypes = tableColumnTypes.getOrDefault(table, Map.of());
        int idIdx = cols.indexOf("id");
        if (idIdx < 0) {
            log.warn("表 {} 无 id 主键, 跳过 DELETE(需人工处理)", table);
            return;
        }
        for (Serializable[] row : rows) {
            try {
                Serializable id = row[idIdx];
                String sql = "DELETE FROM " + quote(table) + " WHERE id = " + quoteValue(id, colTypes.getOrDefault("id", "string"));
                targetJdbc.update(sql);
                log.debug("[DELETE→{}] id={}", table, id);
            } catch (Exception e) {
                log.error("[DELETE] 表 {} 同步失败: {}", table, e.getMessage());
            }
        }
    }

    private String buildInsertSql(String table, List<String> cols, Map<String, String> colTypes, Serializable[] row) {
        StringBuilder colSql = new StringBuilder();
        StringBuilder valSql = new StringBuilder();
        for (int i = 0; i < Math.min(cols.size(), row.length); i++) {
            if (i > 0) {
                colSql.append(", ");
                valSql.append(", ");
            }
            colSql.append('`').append(cols.get(i)).append('`');
            valSql.append(quoteValue(row[i], colTypes.getOrDefault(cols.get(i), "string")));
        }
        return "INSERT INTO " + quote(table) + " (" + colSql + ") VALUES (" + valSql + ")";
    }

    private String buildUpdateSql(String table, List<String> cols, Map<String, String> colTypes, Serializable[] oldRow, Serializable[] newRow) {
        int idIdx = cols.indexOf("id");
        if (idIdx < 0) {
            log.warn("表 {} 无 id 主键, 跳过 UPDATE", table);
            return null;
        }
        StringBuilder setSql = new StringBuilder();
        for (int i = 0; i < Math.min(cols.size(), newRow.length); i++) {
            Serializable oldV = i < oldRow.length ? oldRow[i] : null;
            Serializable newV = newRow[i];
            String safeOld = toComparableString(oldV);
            String safeNew = toComparableString(newV);
            if (oldV != null && newV != null && safeOld.equals(safeNew)) {
                continue;
            }
            if (i == idIdx) {
                continue;
            }
            if (setSql.length() > 0) {
                setSql.append(", ");
            }
            setSql.append('`').append(cols.get(i)).append("` = ")
                    .append(quoteValue(newV, colTypes.getOrDefault(cols.get(i), "string")));
        }
        if (setSql.length() == 0) {
            return null;
        }
        String idType = colTypes.getOrDefault("id", "string");
        Serializable id = newRow[idIdx];
        return "UPDATE " + quote(table) + " SET " + setSql + " WHERE id = " + quoteValue(id, idType);
    }

    private String quote(String name) {
        return "`" + name + "`";
    }

    /**
     * 根据列类型进行安全转义与格式化
     */
    private String quoteValue(Serializable v, String dataType) {
        if (v == null) {
            return "NULL";
        }
        String realType = dataType == null ? "string" : dataType.toLowerCase();
        // text/blob/json 等大对象: binlog 返回 byte[], 需转成 UTF-8 字符串
        if (v instanceof byte[]) {
            String str = new String((byte[]) v, java.nio.charset.StandardCharsets.UTF_8);
            return "'" + str.replace("'", "''") + "'";
        }
        String s = String.valueOf(v);
        // 数字/布尔类型: 不包引号(保持数值), 防止 '1' 变字符串 (COLUMN_TYPE 可能含 unsigned 后缀, 用 startsWith)
        if (realType.startsWith("int") || realType.startsWith("bigint")
                || realType.startsWith("tinyint") || realType.startsWith("smallint")
                || realType.startsWith("mediumint") || realType.startsWith("decimal")
                || realType.startsWith("float") || realType.startsWith("double")
                || realType.startsWith("bit")) {
            return s;
        }
        // datetime/timestamp: 转成 MySQL 标准格式
        if (realType.contains("date") || realType.contains("time")) {
            String fmt = formatDateValue(s);
            return "'" + fmt + "'";
        }
        // enum: binlog 返回的是索引(1..n), 需转成对应枚举字符串
        if (realType.startsWith("enum")) {
            List<String> enumVals = parseEnumValues(realType);
            if (!enumVals.isEmpty()) {
                try {
                    int idx = Integer.parseInt(s.trim());
                    if (idx >= 1 && idx <= enumVals.size()) {
                        return "'" + enumVals.get(idx - 1).replace("'", "''") + "'";
                    }
                } catch (NumberFormatException ignore) {
                    // 如果不是索引, 而是已转换好的值, 直接返回
                }
            }
            return "'" + s.replace("'", "''") + "'";
        }
        // 字符串类型 (varchar/char/text等): 修复可能的字符集乱码(latin1误读utf8)
        return "'" + fixCharset(s).replace("'", "''") + "'";
    }

    /**
     * 修复 binlog 字符串乱码: MySQL varchar 在 binlog 中可能被按 latin1 解码,
     * 若还原成 UTF-8 后包含 CJK/高字节序列则采用还原结果
     */
    private String fixCharset(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            // 按 latin1 编码回字节, 再按 UTF-8 解码
            byte[] raw = s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            String utf8 = new String(raw, java.nio.charset.StandardCharsets.UTF_8);
            // 仅当还原结果包含 CJK 统一表意文字时才采用, 否则保持原值(避免误转换纯 ASCII)
            if (containsCjk(utf8)) {
                return utf8;
            }
        } catch (Exception ignore) {
        }
        return s;
    }

    private boolean containsCjk(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取异常链中最深层的 message(定位真正的 DB 错误)
     */
    private String getRootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage();
    }

    /**
     * 把 binlog 的日期值格式化成 MySQL 认识的形式
     */
    private String formatDateValue(String s) {
        // 处理形如 "Sat Aug 22 22:02:44 CST 2026" 的字符串
        java.util.Date d = tryParseDate(s);
        if (d != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        }
        // 已经是 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 则原样
        return s.trim();
    }

    private java.util.Date tryParseDate(String s) {
        try {
            // 尝试解析常见英文日期格式
            java.text.SimpleDateFormat f1 = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
            return f1.parse(s);
        } catch (Exception ignore) {
        }
        try {
            java.text.SimpleDateFormat f2 = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", java.util.Locale.ENGLISH);
            return f2.parse(s);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 判断两值是否序列化后相等(用于 UPDATE 时跳过未变化列)
     */
    private String toComparableString(Serializable v) {
        if (v == null) {
            return "NULL";
        }
        if (v instanceof byte[]) {
            return new String((byte[]) v, java.nio.charset.StandardCharsets.UTF_8);
        }
        return String.valueOf(v);
    }

    /**
     * 解析 enum 定义, 如 "enum('domestic','foreign')" -> [domestic, foreign]
     */
    private List<String> parseEnumValues(String columnType) {
        List<String> result = new ArrayList<>();
        try {
            int start = columnType.indexOf('(');
            int end = columnType.lastIndexOf(')');
            if (start >= 0 && end > start) {
                String inner = columnType.substring(start + 1, end);
                // 按 'xxx' 分割
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("'([^']*)'").matcher(inner);
                while (m.find()) {
                    result.add(m.group(1));
                }
            }
        } catch (Exception ignore) {
        }
        return result;
    }
}
