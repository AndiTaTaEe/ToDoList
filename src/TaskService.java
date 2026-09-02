import java.util.List;

public class TaskService {
    private final TaskRepository repository;
    public TaskService(TaskRepository repository){
        this.repository = repository;
    }

    // add a task
    public Task addTask(String description){
        return repository.add(new Task(1, description));
    }

    // update the description
    public boolean updateDescription(int id, String newDescription){
        return repository.findById(id)
                .map(task -> repository.update(task.withDescription(newDescription)))
                .orElse(false);
    }

    // mark a task as in progress or done
    public boolean updateStatus(int id, Status newStatus){
        return repository.findById(id)
                .map(task -> repository.update(task.withStatus(newStatus)))
                .orElse(false);
    }

    // delete a task
    public boolean deleteTask(int id){
        return repository.delete(id);
    }

    // filter the task list and return it
    public List<Task> listTasks(Status statusFilter){
        List<Task> allTasks = repository.findAll();
        if (statusFilter == null){
            return allTasks;
        }
        return allTasks.stream()
                .filter(task -> task.status() == statusFilter)
                .toList();
    }
}
