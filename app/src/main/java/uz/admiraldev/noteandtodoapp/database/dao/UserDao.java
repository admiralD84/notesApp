package uz.admiraldev.noteandtodoapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uz.admiraldev.noteandtodoapp.models.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username AND password = :password)")
    Boolean isLogged(String username, String password);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username AND pinCode = :pinCode)")
    Boolean isLoggedPin(String username, String pinCode);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username AND pinCode = :empty)")
    Boolean notEmptyPinCode(String username, String empty);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username)")
    Boolean isTakenLogin(String username);
}

