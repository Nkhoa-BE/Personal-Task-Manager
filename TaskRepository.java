package refactor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TaskRepository {
    private static final String DB_FILE_PATH = "tasks_database.json";

    public List<Task> loadAllTasks() {
        List<Task> taskList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray) obj;
                for (Object o : array) {
                    JSONObject json = (JSONObject) o;
                    Task task = parseTaskFromJson(json);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        }
        return taskList;
    }

    public void saveAllTasks(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            jsonArray.add(task.toJson());
        }

        try (FileWriter file = new FileWriter(DB_FILE_PATH)) {
            file.write(jsonArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi vào file: " + e.getMessage());
        }
    }

    private Task parseTaskFromJson(JSONObject json) {
        try {
            String title = json.get("title").toString();
            String description = json.get("description").toString();
            LocalDate dueDate = LocalDate.parse(json.get("due_date").toString(), Validator.getDateFormatter());
            PriorityLevel priority = PriorityLevel.fromString(json.get("priority").toString());

            return new Task(title, description, dueDate, priority);
        } catch (Exception e) {
            return null;
        }
    }
}
