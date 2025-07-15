package refactor;

public enum PriorityLevel {
    THAP("Thấp"),
    TRUNG_BINH("Trung bình"),
    CAO("Cao");

    private final String label;

    PriorityLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValid(String input) {
        for (PriorityLevel level : values()) {
            if (level.getLabel().equalsIgnoreCase(input.trim())) {
                return true;
            }
        }
        return false;
    }

    public static PriorityLevel fromString(String input) {
        for (PriorityLevel level : values()) {
            if (level.getLabel().equalsIgnoreCase(input.trim())) {
                return level;
            }
        }
        throw new IllegalArgumentException("Mức độ ưu tiên không hợp lệ: " + input);
    }
}
