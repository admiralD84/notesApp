package uz.admiraldev.noteandtodoapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import uz.admiraldev.noteandtodoapp.database.dao.TaskDao;
import uz.admiraldev.noteandtodoapp.models.Task;

@Database(entities = {Task.class}, version = 8, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
