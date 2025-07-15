package refactor;
import java.time.LocalDate;
import java.util.List;


public class PersonalTaskManagerViolations {

    private final TaskRepository repo = new TaskRepository();
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
        List<Task> tasks = repo.loadAllTasks();

        // Kiểm tra trùng lặp
        for (Task task : tasks) {
            if (task.getTitle().equalsIgnoreCase(title)
                && task.getDueDate().equals(dueDate)) {
                System.out.printf("Lỗi: Nhiệm vụ '%s' đã tồn tại với cùng ngày đến hạn.\n", title);
                return null;
            }
        }

        Task newTask = new Task(title, description, dueDate, PriorityLevel.fromString(priorityLevel));
        tasks.add(newTask);

        repo.saveAllTasks(tasks);

        System.out.println("Đã thêm nhiệm vụ mới thành công với ID: " + newTask.getId());
        return newTask;
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


