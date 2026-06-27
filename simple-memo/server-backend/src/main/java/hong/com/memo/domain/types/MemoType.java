package hong.com.memo.domain.types;

/**
 * 备忘类型枚举 - 三种工作模式
 */
public enum MemoType {
    SIMPLE_NOTE(1, "简单备忘"),
    SINGLE_TIMER(2, "单次定时"),
    RECURRING_TIMER(3, "循环定时");

    private final int code;
    private final String description;
    MemoType(int code, String description) { this.code = code; this.description = description; }
    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static MemoType fromCode(int code) {
        for (MemoType t : values()) { if (t.code == code) return t; }
        throw new IllegalArgumentException("未知的备忘类型: " + code);
    }
}
