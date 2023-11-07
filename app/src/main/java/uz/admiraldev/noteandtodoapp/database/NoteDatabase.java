package uz.admiraldev.noteandtodoapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import uz.admiraldev.noteandtodoapp.database.dao.NoteDao;
import uz.admiraldev.noteandtodoapp.models.Note;

@Database(entities = {Note.class}, version = 4, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
