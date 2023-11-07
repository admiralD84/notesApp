package uz.admiraldev.noteandtodoapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    String taskName;
    String addedTime;
    boolean isDone;
    Long deadlineDate;
    String deadlineTime;

    public Task(String taskName, String addedTime, boolean isDone, Long deadlineDate, String deadlineTime) {
        this.taskName = taskName;
        this.addedTime = addedTime;
        this.isDone = isDone;
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
    }

    public void setDeadlineDate(Long deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public Long getDeadlineDate() {
        return deadlineDate;
    }

    public String getDeadlineDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(deadlineDate);
    }

    public String getDeadlineTimeString() {
        if (deadlineDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(deadlineDate);
            return deadlineTime + " | " + currentDate;
        } else return "";
    }

    public String getDoneTimeString() {
        if (deadlineDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(deadlineDate);
        } else return "";
    }

    public String getDeadlineTime() {
        return deadlineTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

}
