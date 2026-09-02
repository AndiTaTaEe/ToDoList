import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonTaskRepository implements TaskRepository {
    private final Path jsonPath;

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

    // create a helper method to extract the values from a json formatted filed (json -> "key": value)
    private String extractValue(String block, String key){
        String searchKey = "\"" + key + "\":"; // key model that we are searching - "fieldName:"
        int keyIndex = block.indexOf(searchKey);
        if (keyIndex == -1){
            throw new IllegalArgumentException("Key not found: "+ key);
        }
        int startIndex = keyIndex + searchKey.length(); // startIndex starts at the value portion
        // advance any whitespace or newlines after the colon
        while (startIndex < block.length() && Character.isWhitespace(block.charAt(startIndex))) {
            startIndex++;
        }
        // check if the values is in quotes - string
        if (block.charAt(startIndex) == '"'){
            int contentStart = startIndex + 1; // next char after the 1st quote
            int contentEnd = block.indexOf('"', contentStart); // search for the index of the closing quote
            return block.substring(contentStart, contentEnd);
        } else {
            // if the char of the start index is not a quote - for numbers - read until comma, newline, closing brace
            int endIndex = startIndex;
            while (endIndex < block.length() && block.charAt(endIndex) != ',' && block.charAt(endIndex) != '\n' && block.charAt(endIndex) != '}'){
                endIndex++;
            }
            return block.substring(startIndex, endIndex).trim();
        }
    }

    // reading tasks from the json file - convert the JSON -> object - deserialization
    private List<Task> readTasksFromJson() {
        List<Task> parsedTasks = new ArrayList<>();
        try {
            if(!Files.exists(jsonPath)){
                return parsedTasks;
            }
            String content = Files.readString(jsonPath).trim();
            if (content.isEmpty() || content.equals("[]")){
                return parsedTasks;
            }
            int startIndex = content.indexOf('{'); // start index of the json file
            while (startIndex != -1){
                int endIndex = content.indexOf('}', startIndex);
                if (endIndex == -1) break;
                String block = content.substring(startIndex, endIndex+1);
                // parsing the elements of the task block
                int id = Integer.parseInt(extractValue(block, "id"));
                String description = extractValue(block, "description")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
                Status status = Status.fromString(extractValue(block, "status"))
                        .orElse(Status.TODO);
                LocalDateTime createdAt = LocalDateTime.parse(extractValue(block, "createdAt"));
                LocalDateTime updatedAt = LocalDateTime.parse(extractValue(block, "updatedAt"));

                // returning them into a new task object
                parsedTasks.add(new Task(id, description,status, createdAt, updatedAt));

                // move to the next object in the file
                startIndex = content.indexOf('{', endIndex + 1);
            }
        } catch (IOException e){
            throw new RuntimeException("Error reading tasks from: " + jsonPath, e);
        }
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
