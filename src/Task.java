import java.time.LocalDateTime;

public record Task(int id, String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    // handling the errors
    public Task {
        if (id <= 0){
            throw new IllegalArgumentException("ID cannot be less than or equal to zero.");
        }

        if (description == null || description.isBlank()){
            throw new IllegalArgumentException("Description of the task cannot be null.");
        }
        // trimming the description of the task after verifying it in the constructor
        description = description.trim();

        if (status == null){
            throw new IllegalArgumentException("Status of the task cannot be null.");
        }

        if (createdAt == null){
            throw new IllegalArgumentException("createdAt field cannot be null");
        }

        if (updatedAt == null){
            throw new IllegalArgumentException("updatedAt field cannot be null");
        }
        if (!updatedAt.isBefore(createdAt)){
            throw new IllegalArgumentException("updatedAt field cannot be a date in the future");
        }
    }

    // constructor for creating a new task
    public Task(int id, String description){
        this(id, description, Status.TODO, LocalDateTime.now(), LocalDateTime.now());
    }

    // copy helper to update description
    public Task withDescription(String newDescription){
        return new Task(this.id, newDescription, this.status, this.createdAt, LocalDateTime.now());
    }

    // helper to update status
    public Task withStatus(Status newStatus){
        return new Task(this.id, this.description, newStatus, this.createdAt, LocalDateTime.now());
    }
}
