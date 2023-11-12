package uz.admiraldev.noteandtodoapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.noteandtodoapp.models.ShoppingList;

@Dao
public interface ShoppingListDao {
    @Insert
    void insertProduct(ShoppingList product);

    @Query("SELECT * FROM shopping ORDER BY isDone ASC")
    List<ShoppingList> getShoppingList();

    @Query("DELETE FROM shopping WHERE id IN (:ids)")
    void deleteItemByIds(ArrayList<Integer> ids);

    @Query("UPDATE shopping SET isDone = :isDone, quantity = :quantity, cost = :price, purchaseTime = :currentDate WHERE id = :id")
    void updateProductPurchaseState(int id,
                                    Long currentDate,
                                    boolean isDone,
                                    String price,
                                    String quantity);
}
