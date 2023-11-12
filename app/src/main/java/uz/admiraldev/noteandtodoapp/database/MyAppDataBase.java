package uz.admiraldev.noteandtodoapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import uz.admiraldev.noteandtodoapp.database.dao.NoteDao;
import uz.admiraldev.noteandtodoapp.database.dao.ShoppingListDao;
import uz.admiraldev.noteandtodoapp.database.dao.TaskDao;
import uz.admiraldev.noteandtodoapp.database.dao.UserDao;
import uz.admiraldev.noteandtodoapp.models.Note;
import uz.admiraldev.noteandtodoapp.models.ShoppingList;
import uz.admiraldev.noteandtodoapp.models.Task;
import uz.admiraldev.noteandtodoapp.models.User;

@Database(entities = {Note.class, ShoppingList.class, Task.class, User.class}, version = 1, exportSchema = false)
public abstract class MyAppDataBase extends RoomDatabase {
    public abstract NoteDao noteDao();

    public abstract ShoppingListDao shoppingListDao();

    public abstract TaskDao taskDao();

    public abstract UserDao userDao();
}