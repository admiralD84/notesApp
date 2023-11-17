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
    private final ExecutorService myExecutor = Executors.newSingleThreadExecutor();
    private List<ShoppingList> shoppingList;
    private boolean showPurchasedProducts;
    public MutableLiveData<List<ShoppingList>> shoppingListLive = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> selectedProducts = new MutableLiveData<>();
    public MutableLiveData<Boolean> isActionDelete = new MutableLiveData<>();

    public ShopListViewModel() {
        isActionDelete.setValue(false);
        selectedProducts.setValue(new ArrayList<>());
        getAllProducts();
    }

    public int getSelectedProductsCount() {
        if (selectedProducts.getValue() == null)
            return 0;
        else
            return selectedProducts.getValue().size();
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

    public void setIsActionDelete(Boolean isActionDelete) {
        this.isActionDelete.setValue(isActionDelete);
    }

    public void updateIsDoneField(int productId, String quantity, String price) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().shoppingListDao().updateProductPurchaseState(
                        productId, Calendar.getInstance().getTimeInMillis(), true,
                        price, quantity);
                new Handler(Looper.getMainLooper()).post(() -> {
                    for (ShoppingList product : shoppingList) {
                        if (product.getId() == productId) {
                            product.setDone(true);
                            product.setCost(price);
                            product.setQuantity(quantity);
                        }
                    }
                });
                shoppingListLive.postValue(new ArrayList<>(shoppingList));
            } catch (Exception e) {
                Log.d("myTag", "Mark product is purchased error: " + e.getMessage());
            }
        });
    }

    public void getAllProducts() {
        myExecutor.execute(() -> {
            try {
                List<ShoppingList> fetchedProducts = MainActivity.getAppDataBase().shoppingListDao().getShoppingList();
                new Handler(Looper.getMainLooper()).post(() -> {
                    shoppingList = fetchedProducts;
                    filteredProducts(shoppingList);
                });
            } catch (Exception e) {
                Log.d("myTag", "getAllTasks exception : " + e.getMessage());
            }
        });
    }

    private void filteredProducts(List<ShoppingList> products) {
        if (showPurchasedProducts) {
            shoppingListLive.setValue(products);
        } else {
            List<ShoppingList> notPurchasedProducts = new ArrayList<>();
            for (ShoppingList product : products) {
                if (!product.isDone()) {
                    notPurchasedProducts.add(product);
                }
            }
            shoppingListLive.setValue(notPurchasedProducts);
        }
    }

    public void setShowPurchasedProducts(boolean showPurchasedProducts) {
        this.showPurchasedProducts = showPurchasedProducts;
        if (shoppingList == null) {
            getAllProducts();
        } else {
            filteredProducts(shoppingList);
        }
    }

    public void insertProduct(String productName) {
        ShoppingList newProduct = new ShoppingList(productName, "", "",
                Calendar.getInstance().getTimeInMillis(), false);
        myExecutor.execute(() -> {
            try {
                MainActivity.getAppDataBase().shoppingListDao().insertProduct(newProduct);
                new Handler(Looper.getMainLooper()).post(this::getAllProducts);
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
    }

    public void deleteSelectedProducts() {
        if (selectedProducts.getValue() != null && selectedProducts.getValue().size() > 0) {
            List<Integer> selectedProductsId = selectedProducts.getValue();
            shoppingList.removeIf(product -> selectedProductsId.contains(product.getId()));
            myExecutor.execute(() -> {
                try {
                    MainActivity.getAppDataBase().shoppingListDao()
                            .deleteItemByIds((ArrayList<Integer>) selectedProducts.getValue());
                    new Handler(Looper.getMainLooper()).post(() -> {
                        filteredProducts(shoppingList);
                        selectedProducts.getValue().clear();
                    });
                } catch (Exception e) {
                    Log.d("myTag", "delete products error: " + e.getMessage());
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }
}