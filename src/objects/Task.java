package objects;

import java.util.Date;

public class Task {
    private TaskType type;
    private Date date;

    public Task() {}

    public Task(TaskType tType, Date taskDate) {
        type = tType;
        date = taskDate;
    }

    public TaskType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
