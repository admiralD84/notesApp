package uz.admiraldev.noteandtodoapp.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.ShoppingList;

public class ShopListViewModel extends ViewModel {
    private final ExecutorService myExecutor;
    private List<ShoppingList> shoppingList;
    public final MutableLiveData<List<ShoppingList>> shoppingListLive = new MutableLiveData<>();
//    public final MutableLiveData<Boolean> isPurchased = new MutableLiveData<>();

    public ShopListViewModel() {
        this.myExecutor = Executors.newSingleThreadExecutor();
        getShoppingList();
    }

    public void updateIsDoneField(int productId, boolean isDone, String quantity, String price) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getMyAppDatabase().shoppingListDao().updateProductPurchaseState(
                        productId,
                        Calendar.getInstance().getTimeInMillis(),
                        isDone,
                        price,
                        quantity);
            } catch (Exception e) {
                Log.d("myTag", "delete task error: " + e.getMessage());
            }
        });
        getShoppingList();
    }

    public void getShoppingList() {
        myExecutor.execute(() -> {
            try {
                shoppingList = MainActivity.getMyAppDatabase().shoppingListDao().getShoppingList();
                new Handler(Looper.getMainLooper()).post(() ->
                        shoppingListLive.setValue(shoppingList));
            } catch (Exception e) {
                Log.d("myTag", "getShoppingList error: " + e.getMessage());
            }
        });
    }

    public void insertProduct(String productName) {
        myExecutor.execute(() -> {
            ShoppingList newProduct = new ShoppingList(productName, "", "",
                    Calendar.getInstance().getTimeInMillis(), false);
            try {
                MainActivity.getMyAppDatabase().shoppingListDao().insertProduct(newProduct);
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
    }

  /*  public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        LocalTime time = LocalTime.now();
        Calendar cal = Calendar.getInstance();
        String dateSting = dateFormat.format(cal);
        if (time.getMinute() < 10)
            return dateSting + " | " + time.getHour() + ":0" + time.getMinute();
        else
            return dateSting + " | " + time.getHour() + ":" + time.getMinute();
    }*/
}
