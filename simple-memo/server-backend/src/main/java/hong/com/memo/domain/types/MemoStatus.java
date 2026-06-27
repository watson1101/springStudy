package hong.com.memo.domain.types;

/**
 * 备忘状态枚举
 */
public enum MemoStatus {
    PENDING(1, "待办"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    private final int code;
    private final String description;
    MemoStatus(int code, String description) { this.code = code; this.description = description; }
    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static MemoStatus fromCode(int code) {
        for (MemoStatus s : values()) { if (s.code == code) return s; }
        throw new IllegalArgumentException("未知的备忘状态: " + code);
    }
}
