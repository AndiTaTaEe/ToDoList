import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonTaskRepository implements TaskRepository {
    private final Path jsonPath;
    // TODO - add regex patterns for deserialization

    public JsonTaskRepository(String jsonFileName){
        this.jsonPath = Path.of(jsonFileName);
        createJsonFile();

    }

    private void createJsonFile(){
        try {
            if (jsonPath.getParent()!= null){
                Files.createDirectories(jsonPath.getParent());
            }
            // if the file doesnt exist - create the file json formatted
            if (!Files.exists(jsonPath)){
                Files.writeString(jsonPath, "[]");
            } else {
                String content = Files.readString(jsonPath).trim();
                if (content.isEmpty()){
                    Files.writeString(jsonPath, "[]");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error at creating tasks file: "+ jsonPath, e);
        }
    }

    // helper method to write a task in json style
    private String taskToJson(Task task){
        String escapedDesc = task.description()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

        return String.format(
                "  {\n" +
                        "    \"id\": %d,\n" +
                        "    \"description\": \"%s\",\n" +
                        "    \"status\": \"%s\",\n" +
                        "    \"createdAt\": \"%s\",\n" +
                        "    \"updatedAt\": \"%s\"\n" +
                        "  }",
                task.id(),
                escapedDesc,
                task.status().name(),
                task.createdAt(),
                task.updatedAt()
        );
    }

    // reading tasks from the json file - convert the JSON -> object - deserialization - TODO - with regex and patterns
    private List<Task> readTasksFromJson() {
        List<Task> parsedTasks = new ArrayList<>();
        return parsedTasks;
    }

    // writing tasks to the json file - convert the object -> json - serialization
    private void writeTasksToJson(List<Task> tasks) {
        try {
            StringBuilder stringBuilder = new StringBuilder("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                // adding in the stringBuilder the json formatted task
                stringBuilder.append(taskToJson(tasks.get(i)));
                // if we haven't reached the last task - then append a comma for the next one
                if (i < tasks.size() - 1) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\n");
            }
            stringBuilder.append("]");
            // writing to the file
            Files.writeString(jsonPath, stringBuilder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Error saving tasks to: " + jsonPath, e);
        }
    }

    @Override
    public void add(Task task) {

    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean update(Task task) {
        return false;
    }

    @Override
    public Optional<Task> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Task> findAll() {
        return List.of();
    }
}
