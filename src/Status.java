import java.util.Optional;

public enum Status {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String value;

    Status(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    // helper function - to parse the names accordingly from the console
    public static Optional<Status> fromString(String input){
        if (input == null || input.isBlank()){
            return Optional.empty();
        }
        // normalized string - trimming the white spaces from the value
        String normalized = input.trim().toLowerCase().replace("_", "-").replace(" ", "-");

        for (Status status: Status.values()){
            if (status.value.equals(normalized) || status.name().equalsIgnoreCase(input.trim())){
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
