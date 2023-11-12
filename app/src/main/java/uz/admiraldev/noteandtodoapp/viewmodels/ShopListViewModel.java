package uz.admiraldev.noteandtodoapp.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.ShoppingList;

public class ShopListViewModel extends ViewModel {
    private final ExecutorService myExecutor;
    private List<ShoppingList> shoppingList;
    private List<ShoppingList> purchasedProductsList;
    private List<ShoppingList> allProductsList;
    public MutableLiveData<List<ShoppingList>> shoppingListLive = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> selectedProducts = new MutableLiveData<>();
    public MutableLiveData<Boolean> isActionDelete = new MutableLiveData<>();

    public ShopListViewModel() {
        this.myExecutor = Executors.newSingleThreadExecutor();
        isActionDelete.setValue(false);
        selectedProducts.setValue(new ArrayList<>());
        getShoppingList();
    }

    public int getSelectedProductsCount() {
        if (selectedProducts.getValue() == null)
            return 0;
        else
            return selectedProducts.getValue().size();
    }

    public void hidePurchasedProducts() {
        purchasedProductsList = new ArrayList<>();
        allProductsList = new ArrayList<>();
        allProductsList.addAll(Objects.requireNonNull(shoppingListLive.getValue()));
        shoppingList.forEach(product -> {
            if (!product.isDone()) {
                purchasedProductsList.add(product);
            }
        });
        shoppingListLive.setValue(purchasedProductsList);
    }

    public void addSelectedProductsId(int noteId) {
        if (noteId == -1)
            Objects.requireNonNull(selectedProducts.getValue()).clear();
        else {
            if (Objects.requireNonNull(selectedProducts.getValue()).contains(noteId)) {
                selectedProducts.getValue().remove(Integer.valueOf(noteId));
            } else {
                selectedProducts.getValue().add(noteId);
            }
        }
    }

    public void deleteSelectedProducts(boolean isHidden){
        if(isHidden){
            purchasedProductsList.remove(selectedProducts.getValue());
        }
        myExecutor.execute(() -> {
            try {
                MainActivity.getShoppingListDatabase().shoppingListDao()
                        .deleteItemByIds((ArrayList<Integer>) selectedProducts.getValue());
                new Handler(Looper.getMainLooper()).post(() ->
                        Objects.requireNonNull(selectedProducts.getValue()).clear());
            } catch (Exception e) {
                Log.d("myTag", "delete products error: " + e.getMessage());
            }
        });
    }
    public void showAllProducts() {
        shoppingListLive.setValue(allProductsList);
    }

    public void setIsActionDelete(Boolean isActionDelete) {
        this.isActionDelete.setValue(isActionDelete);
    }

    public void updateIsDoneField(int productId, boolean isDone, String quantity, String price) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getShoppingListDatabase().shoppingListDao().updateProductPurchaseState(
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
                shoppingList = MainActivity.getShoppingListDatabase().shoppingListDao().getShoppingList();
                shoppingListLive.postValue(shoppingList);
//                new Handler(Looper.getMainLooper()).post(() ->
//                        shoppingListLive.setValue(shoppingList));
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
                MainActivity.getShoppingListDatabase().shoppingListDao().insertProduct(newProduct);
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
    }
}
