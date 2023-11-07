package uz.admiraldev.noteandtodoapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "shopping")
public class ShoppingList {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String productName;
    private String cost;
    private String quantity;
    private boolean isDone;
    private Long purchaseTime;

    public ShoppingList(String productName,
                        String cost,
                        String quantity,
                        Long purchaseTime,
                        boolean isDone) {
        this.productName = productName;
        this.cost = cost;
        this.quantity = quantity;
        this.purchaseTime = purchaseTime;
        this.isDone = isDone;
    }

    public String getPurchaseTimeInString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(purchaseTime);
    }

    public Long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
