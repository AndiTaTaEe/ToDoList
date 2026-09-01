import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void add(Task task);
    boolean delete(int id);
    boolean update(Task task);
    Optional<Task> findById(int id);
    List<Task> findAll();
}
