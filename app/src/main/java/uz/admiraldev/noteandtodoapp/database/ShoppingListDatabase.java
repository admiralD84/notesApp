package uz.admiraldev.noteandtodoapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import uz.admiraldev.noteandtodoapp.database.dao.ShoppingListDao;
import uz.admiraldev.noteandtodoapp.models.ShoppingList;

@Database(entities = {ShoppingList.class}, version = 5, exportSchema = false)
public abstract class ShoppingListDatabase extends RoomDatabase {
    public abstract ShoppingListDao shoppingListDao();
}
