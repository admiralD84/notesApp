package uz.admiraldev.noteandtodoapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uz.admiraldev.noteandtodoapp.models.Task;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM tasks ORDER BY isDone ASC, deadlineDate ASC")
    List<Task> getTasks();

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteTask(int id);

    @Query("UPDATE tasks SET isDone = :isDone, deadlineDate = :currentDate WHERE id = :id")
    void updateTaskDoneField(Long currentDate, int id, boolean isDone);

    @Query("SELECT * FROM tasks ORDER BY CASE WHEN :isAsc = 1 THEN deadlineDate END ASC," +
            " CASE WHEN :isAsc = 0 THEN deadlineDate END DESC")
    List<Task> getSortedTasksByDeadline(boolean isAsc);

    @Query("SELECT * FROM tasks ORDER BY CASE WHEN :isAsc = 1 THEN isDone END ASC," +
            " CASE WHEN :isAsc = 0 THEN isDone END DESC")
    List<Task> getSortedTasksByIsDoneState(boolean isAsc);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Task task);
}