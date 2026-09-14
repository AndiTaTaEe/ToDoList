# Task Tracker CLI

A lightweight, zero-dependency Command-Line Interface (CLI) application built with Java to track, organize, and manage tasks directly from your terminal. All tasks are persisted locally in a structured JSON format without relying on external libraries or frameworks.

---

## Features

* **Zero external dependencies**: Built entirely using standard Java SE libraries (manual JSON serialization and deserialization using string manipulation and Java NIO).
* **Persistent storage**: Automatically creates and maintains a local `tasks.json` file.
* **Task lifecycle management**: Add, update descriptions, delete, and transition task statuses (`todo`, `in-progress`, `done`).
* **Flexible listing**: View all tasks or filter dynamically by status.
* **Layered architecture**: Clean separation of concerns following Domain, Repository, Service, and CLI layers.

---

## Architecture overview

The application follows clean object-oriented architecture principles:

* **`Task`**: Immutable record representing task entities (ID, description, status, creation timestamp, and update timestamp).
* **`Status`**: Enum representing task states (`TODO`, `IN_PROGRESS`, `DONE`) with custom parsing logic.
* **`TaskRepository` & `JsonTaskRepository`**: Interface and implementation handling native file I/O, manual JSON parsing, and basic CRUD operations.
* **`TaskService`**: Core domain logic coordinating task updates, filtering, and repository interaction.
* **`TaskListApp`**: Command-line interface handling positional argument validation, error handling, formatted tabular printing, and routing.

---

## Prerequisites

* **Java Development Kit (JDK)**: Version 17 or higher (Java 21 recommended for `record` and modern switch pattern matching support).
* A terminal environment (PowerShell, Command Prompt, Bash, or Zsh).

Verify your Java and compiler installation:

```bash
javac -version
java -version
```

---

## Installation & Compilation

1. Clone or navigate to the source directory:

```bash
cd path/to/project/src
```

2. Compile all Java source files:

```bash
javac *.java
```

---

## Usage

Run the compiled application using `java TaskListApp` followed by the command and arguments.

### 1. Add a Task
Creates a new task with status `todo` and prints the generated ID:

```bash
java TaskListApp add "Buy groceries"
# Output: Task added successfully (ID: 1)
```

### 2. Update Task Description
Updates the description and refreshes the `updatedAt` timestamp:

```bash
java TaskListApp update 1 "Buy groceries and prepare dinner"
# Output: Task with ID: 1 updated successfully
```

### 3. Update Task Status
Transition task state to `in-progress` or `done`:

```bash
java TaskListApp mark-in-progress 1
# Output: Task with ID: 1 marked in progress.

java TaskListApp mark-done 1
# Output: Task with ID: 1 marked done.
```

### 4. List Tasks
List all tasks:

```bash
java TaskListApp list
```

Filter tasks by status (`todo`, `in-progress`, `done`):

```bash
java TaskListApp list todo
java TaskListApp list in-progress
java TaskListApp list done
```

**Sample Output:**

```text
ID   | Status        | Description                         | Updated at          
---------------------------------------------------------------------------
1    | done          | Buy groceries and prepare dinner    | 2026-09-14 21:40:00 
2    | in-progress   | Write unit tests                    | 2026-09-14 21:42:15 
```

### 5. Delete a Task
Removes a task by its unique ID:

```bash
java TaskListApp delete 1
# Output: Task with ID: 1 deleted successfully.
```

---

## CLI Shortcut Setup (Optional)

To run the application cleanly as `task-cli` instead of typing `java TaskListApp`:

### Windows (PowerShell / Command Prompt)
Create a file named `task-cli.bat` in your source directory:

```bat
@echo off
java -cp "%~dp0" TaskListApp %*
```

Now execute commands directly:

```cmd
task-cli add "New task"
task-cli list
```

### Linux / macOS
Create a shell script named `task-cli`:

```bash
#!/usr/bin/env bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -cp "$SCRIPT_DIR" TaskListApp "$@"
```

Make it executable:

```bash
chmod +x task-cli
```

Execute commands:

```bash
./task-cli add "New task"
./task-cli list
```

---

## Data Format

Tasks are stored in `tasks.json` in the working execution directory using the following schema:

```json
[
  {
    "id": 1,
    "description": "Buy groceries and prepare dinner",
    "status": "DONE",
    "createdAt": "2026-09-14T21:30:00",
    "updatedAt": "2026-09-14T21:40:00"
  }
]
```

```
