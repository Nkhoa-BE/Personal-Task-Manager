package refactor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.json.simple.JSONObject;

public class Task {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private PriorityLevel priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String title, String description, LocalDate dueDate, PriorityLevel priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = "Chưa hoàn thành";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("description", description);
        obj.put("due_date", dueDate.format(DATE_FORMATTER));
        obj.put("priority", priority.getLabel());
        obj.put("status", status);
        obj.put("created_at", createdAt.toString());
        obj.put("last_updated_at", updatedAt.toString());
        return obj;
    }
}
