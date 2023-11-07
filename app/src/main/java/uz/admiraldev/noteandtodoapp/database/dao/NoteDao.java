package uz.admiraldev.noteandtodoapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.noteandtodoapp.models.Note;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note notes);

    @Query("SELECT * FROM notes ORDER BY addedDate DESC, addedTime DESC")
    List<Note> getNotes();

    /*
     * Sort qilganda Order by field_name1, field_name2 qilinsa ikkita ustun bo'yicha saralab beradi
     * Agar Case when qilinsa condition natijasiga qarab har xil sort qiladi
     *
     */
    @Query("SELECT * FROM notes ORDER BY CASE WHEN :isAsc = 1 THEN priority END ASC," +
            " CASE WHEN :isAsc = 0 THEN priority END DESC, " + " addedDate DESC, addedTime DESC")
    List<Note> getSortedNotesByPriority(boolean isAsc);

    @Query("SELECT * FROM notes ORDER BY CASE WHEN :isAsc = 1 THEN addedDate END ASC," +
            " CASE WHEN :isAsc = 0 THEN addedDate END DESC," +
            " CASE WHEN :isAsc = 1 THEN addedTime END ASC," + "" +
            " CASE WHEN :isAsc = 0 THEN addedTime END DESC")
    List<Note> getSortedNotesByDate(boolean isAsc);

    @Query("SELECT * FROM notes WHERE id = :id")
    Note getNote(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNote(Note note);

    @Query("DELETE FROM notes WHERE id = :id")
    void deleteNote(int id);

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    void deleteItemByIds(ArrayList<Integer> ids);
}
