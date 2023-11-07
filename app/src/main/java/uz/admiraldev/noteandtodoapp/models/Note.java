package uz.admiraldev.noteandtodoapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    String notesTitle;
    String notesSubtitle;
    String addedDate;
    String addedTime;
    int priority;

    public Note(
            String notesTitle,
            String notesSubtitle,
            String addedDate,
            String addedTime,
            int priority) {
        this.notesTitle = notesTitle;
        this.notesSubtitle = notesSubtitle;
        this.addedDate = addedDate;
        this.addedTime = addedTime;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getAddedDate() {
        return addedDate;
    }


    public String getAddedTime() {
        return addedTime;
    }


    public int getId() {
        return id;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public void setNotesTitle(String notesTitle) {
        this.notesTitle = notesTitle;
    }

    public String getNotesSubtitle() {
        return notesSubtitle;
    }

    public void setNotesSubtitle(String notesSubtitle) {
        this.notesSubtitle = notesSubtitle;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
