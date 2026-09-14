import java.util.List;
import java.util.Optional;

public class TaskListApp {

    private final TaskService service;

    /// filePath - should be .json
    public TaskListApp(String filePath) {
        TaskRepository repository = new JsonTaskRepository(filePath);
        this.service = new TaskService(repository);
    }

    public void run(String[] args){
        if (args.length == 0){
            printUsage(); // if there are nor args in the cli - display a 'how to use' statement
            return;
        }
        String command = args[0].toLowerCase(); // we will extract the command, which is the 1st argument
        switch (command){
            case "add" -> handleAddTask(args);
            case "update" -> handleUpdateTask(args);
            case "delete" -> handleDeleteTask(args);
            case "mark-in-progress" -> handleMarkInProgress(args);
            case "mark-done" -> handleDone(args);
            case "list" -> handleListTasks(args);
            default -> {
                System.out.println("Unknown command: " + command);
                printUsage();
            }
        }
    }

    public static void main(String[] args) {
        TaskListApp app = new TaskListApp("tasks.json");
        app.run(args);
    }

    private void printUsage(){
        System.out.println("Usage: task-cli <command> [arguments]");
        System.out.println("List of commands:");
        System.out.println(" add <description>");
        System.out.println(" update <id> <description>");
        System.out.println(" delete <id>");
        System.out.println(" mark-in-progress <id>");
        System.out.println(" mark-done <id>");
        System.out.println(" list [done|todo|in-progress]");
    }

    private void handleAddTask(String[] args){
        ///  adding a task (CLI) - task-cli add "description"
        if (args.length < 2 || args[1].isBlank()){
            System.out.println("Error: Task description cannot be empty.");
            System.out.println("Usage: task-cli add \"<description>\"");
            return;
        }
        Task createdTask = service.addTask(args[1]);
        System.out.println("Task added successfully (ID: " + createdTask.id() + ")");
    }

    // helper for extracting the ID of the task
    private Optional<Integer> parseId(String input){
        if (input == null || input.isBlank()){
            System.out.println("Error: Task ID cannot be empty.");
            return Optional.empty();
        }
        try {
            int taskId = Integer.parseInt(input.trim());
            if (taskId <= 0) {
                System.out.println("Error: Task ID must be greater than zero");
                return Optional.empty();
            }
            return Optional.of(taskId);
        } catch (NumberFormatException e) {
            System.out.println("Error: Task ID must be a valid number.");
            return Optional.empty();
        }
    }

    private void handleUpdateTask(String[] args){
        ///  updating a task (CLI) - task-cli update <id> <description>
        if (args.length < 3 || args[1].isBlank() || args[2].isBlank()){
            System.out.println("Error: Task ID/description cannot be empty.");
            System.out.println("Usage: task-cli update \"<id>\" \"<description\"");
            return;
        }
        Optional<Integer> optionalId = parseId(args[1]);
        if (optionalId.isEmpty()){
            return;
        }
        int taskId = optionalId.get();
        boolean updated = service.updateDescription(taskId, args[2]);
        if (updated){
            System.out.println("Task with ID: " + taskId + " updated successfully");
        } else {
            System.out.println("Error: Task not found (ID: " + taskId + ")");
        }
    }

    private void handleDeleteTask(String[] args){
        ///  deleting a task (CLI) - task-cli delete <id>
        if (args.length < 2 || args[1].isBlank()){
            System.out.println("Error: Task ID cannot be empty.");
            System.out.println("Usage: task-cli delete \"<id>\"");
            return;
        }
        Optional<Integer> optionalId = parseId(args[1]);
        if (optionalId.isEmpty()){
            return;
        }
        int taskId = optionalId.get();
        boolean deleted = service.deleteTask(taskId);
        if (deleted){
            System.out.println("Task with ID: " + taskId + " deleted successfully.");
        } else {
            System.out.println("Error: Task not found (ID: " + taskId + ")");
        }
    }

    private void handleMarkInProgress(String[] args){
        ///  marking a task in progress (CLI) - task-cli mark-in-progress <id>
        if (args.length < 2 || args[1].isBlank()){
            System.out.println("Error: Task ID cannot be empty.");
            System.out.println("Usage: task-cli mark-in-progress \"<id>\"");
            return;
        }
        Optional<Integer> optionalId = parseId(args[1]);
        if (optionalId.isEmpty()){
            return;
        }
        int taskId = optionalId.get();
        boolean markedInProgress = service.updateStatus(taskId, Status.IN_PROGRESS);
        if (markedInProgress){
            System.out.println("Task with ID: " + taskId + " marked in progress.");
        } else {
            System.out.println("Error: Task not found (ID: " + taskId + ")");
        }
    }

    private void handleDone(String[] args){
        ///  marking a task done (CLI) - task-cli mark-done <id>
        if (args.length < 2 || args[1].isBlank()){
            System.out.println("Error: Task ID cannot be empty.");
            System.out.println("Usage: task-cli mark-in-progress \"<id>\"");
            return;
        }
        Optional<Integer> optionalId = parseId(args[1]);
        if (optionalId.isEmpty()){
            return;
        }
        int taskId = optionalId.get();
        boolean markedDone = service.updateStatus(taskId, Status.DONE);
        if (markedDone){
            System.out.println("Task with ID: " + taskId + " marked done.");
        } else {
            System.out.println("Error: Task not found (ID: " + taskId + ")");
        }
    }

    private void handleListTasks(String[] args){
        ///  listing all the tasks (CLI) - task-cli list
        ///  listing tasks that are done (CLI) - task-cli list done
        ///  listing tasks that are todo (CLI) - task-cli list todo
        ///  listing tasks that are in-progress (CLI) - task-cli list in-progress
        Status filter = null;
        // parse optional status filter
        if (args.length >= 2){
            Optional<Status> parsedStatus = Status.fromString(args[1]);
            if (parsedStatus.isEmpty()){
                System.out.println("Error: Invalid status filter '" + args[1] + "'.");
                System.out.println("Allowed statuses: todo, done, in-progress");
                return;
            }
            filter = parsedStatus.get();
        }

        // fetch tasks from the service
        List<Task> tasks = service.listTasks(filter);

        // handle empty list
        if (tasks.isEmpty()){
            System.out.println(filter == null ? "No tasks found." : "No tasks found with status: " + filter.getValue());
            return;
        }

        // print list on the terminal
        System.out.printf("%-4s | %-13s | %-35s | %-20s%n", "ID", "Status", "Description", "Updated at");
        System.out.println("---------------------------------------------------------------------------");
        for (Task task : tasks){
            String formattedDate = task.updatedAt().toString().replace("T", " ");
            // trim microseconds
            if(formattedDate.contains(".")){
                formattedDate = formattedDate.substring(0, formattedDate.indexOf('.'));
            }

            System.out.printf("%-4d | %-13s | %-35s | %-20s%n", task.id(), task.status(), task.description(), formattedDate);
        }
    }


}