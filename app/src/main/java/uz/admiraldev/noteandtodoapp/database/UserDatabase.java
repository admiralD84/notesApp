package uz.admiraldev.noteandtodoapp.database;

import androidx.room.RoomDatabase;
import androidx.room.Database;

import uz.admiraldev.noteandtodoapp.database.dao.UserDao;
import uz.admiraldev.noteandtodoapp.models.User;

@Database(entities = {User.class}, version = 3, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

