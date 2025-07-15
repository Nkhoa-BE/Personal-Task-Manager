package refactor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import refactor.PriorityLevel;

public class PersonalTaskManagerViolations {

    private static final String DB_FILE_PATH = "tasks_database.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Phương thức trợ giúp để tải dữ liệu (sẽ được gọi lặp lại)
    private static JSONArray loadTasksFromDb() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                return (JSONArray) obj;
            }
        } catch (IOException | ParseException e) {
            System.err.println("Lỗi khi đọc file database: " + e.getMessage());
        }
        return new JSONArray();
    }

    // Phương thức trợ giúp để lưu dữ liệu
    private static void saveTasksToDb(JSONArray tasksData) {
        try (FileWriter file = new FileWriter(DB_FILE_PATH)) {
            file.write(tasksData.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi vào file database: " + e.getMessage());
        }
    }

    /**
     * Chức năng thêm nhiệm vụ mới
     *
     * @param title Tiêu đề nhiệm vụ.
     * @param description Mô tả nhiệm vụ.
     * @param dueDateStr Ngày đến hạn (định dạng YYYY-MM-DD).
     * @param priorityLevel Mức độ ưu tiên ("Thấp", "Trung bình", "Cao").
     * @param isRecurring Boolean có phải là nhiệm vụ lặp lại không.
     * @return JSONObject của nhiệm vụ đã thêm, hoặc null nếu có lỗi.
     */
    public Task addNewTaskWithViolations(String title, String description,
                                                String dueDateStr, String priorityLevel
                                                ) {
        //
        if (!Validator.isTitleValid(title)) {
            System.out.println("Lỗi: Tiêu đề không được để trống.");
            return null;
        }
        if (!Validator.isDueDateValid(dueDateStr)) {
            System.out.println("Lỗi: Ngày đến hạn không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD.");
            return null;
        }
        if (!Validator.isPriorityValid(priorityLevel)) {
            System.out.println("Lỗi: Mức độ ưu tiên không hợp lệ. Vui lòng chọn từ: Thấp, Trung bình, Cao.");
            return null;
        }

        LocalDate dueDate = Validator.parseDate(dueDateStr);



        // Tải dữ liệu
        List<Task> tasks = loadTaskListFromDb();

        // Kiểm tra trùng lặp
        for (Task task : tasks) {
            if (task.getTitle().equalsIgnoreCase(title)
                && task.getDueDate().format(DATE_FORMATTER).equals(dueDate.format(DATE_FORMATTER))) {
                System.out.printf("Lỗi: Nhiệm vụ '%s' đã tồn tại với cùng ngày đến hạn.\n", title);
                return null;
            }
        }

        String taskId = UUID.randomUUID().toString(); // YAGNI: Có thể dùng số nguyên tăng dần đơn giản hơn.

        //
        Task newTask = new Task(title, description, dueDate, PriorityLevel.fromString(priorityLevel));
        tasks.add(newTask);

        // Ghi file: chuyển toàn bộ danh sách Task → JSONArray để lưu
        JSONArray jsonArray = new JSONArray();
        for (Task t : tasks) {
            jsonArray.add(t.toJson());
        }
        // Lưu dữ liệu
        saveTasksToDb(jsonArray);

        System.out.println("Đã thêm nhiệm vụ mới thành công với ID: " + newTask.getId());
        return newTask;
    }

    private List<Task> loadTaskListFromDb() {
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

    //
    private Task parseTaskFromJson(JSONObject json) {
        try {
            String title = json.get("title").toString();
            String description = json.get("description").toString();
            LocalDate dueDate = LocalDate.parse(json.get("due_date").toString(), DATE_FORMATTER);
            PriorityLevel priority = PriorityLevel.fromString(json.get("priority").toString());

            Task task = new Task(title, description, dueDate, priority);
            return task;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        PersonalTaskManagerViolations manager = new PersonalTaskManagerViolations();
        System.out.println("\nThêm nhiệm vụ hợp lệ:");
        manager.addNewTaskWithViolations(
            "Mua sách",
            "Sách Công nghệ phần mềm.",
            "2025-07-20",
            "Cao"
        );

        System.out.println("\nThêm nhiệm vụ trùng lặp (minh họa DRY - lặp lại code đọc/ghi DB và kiểm tra trùng):");
        manager.addNewTaskWithViolations(
            "Mua sách",
            "Sách Công nghệ phần mềm.",
            "2025-07-20",
            "Cao"
        );

        System.out.println("\nThêm nhiệm vụ lặp lại (minh họa YAGNI - thêm tính năng không cần thiết ngay):");
        manager.addNewTaskWithViolations(
            "Tập thể dục",
            "Tập gym 1 tiếng.",
            "2025-07-21",
            "Trung bình"
        );

        System.out.println("\nThêm nhiệm vụ với tiêu đề rỗng:");
        manager.addNewTaskWithViolations(
            "",
            "Nhiệm vụ không có tiêu đề.",
            "2025-07-22",
            "Thấp"
        );
    }
}


