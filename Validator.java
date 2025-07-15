package refactor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean isTitleValid(String title) {
        return title != null && !title.trim().isEmpty();
    }

    public static boolean isDueDateValid(String dueDateStr) {
        try {
            LocalDate.parse(dueDateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPriorityValid(String priorityLevel) {
        return PriorityLevel.isValid(priorityLevel);
    }

    public static LocalDate parseDate(String dueDateStr) {
        return LocalDate.parse(dueDateStr, DATE_FORMATTER);
    }

    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }
}
