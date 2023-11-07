package uz.admiraldev.noteandtodoapp.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.models.ShoppingList;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NOT_DONE_ITEM_VIEW = 1;
    private static final int DONE_ITEM_VIEW = 2;
    private List<ShoppingList> productsList;
    private static String sumText;
    private final ShoppingListAdapter.ProductItemClickListener itemClickListener;

    public ShoppingListAdapter(ProductItemClickListener itemClickListener, Context context) {
        this.itemClickListener = itemClickListener;
        sumText = context.getString(R.string.text_sum);
    }


    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder myViewHolder;
        switch (viewType) {
            case DONE_ITEM_VIEW:
                View doneItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_complated_shop_list, parent, false);
                myViewHolder = new CompletedPurchaseViewHolder(doneItemView);
                break;
            case NOT_DONE_ITEM_VIEW:
            default:
                View notCompletedItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shop_list, parent, false);
                myViewHolder = new NotCompletedPurchaseViewHolder(notCompletedItemView);
                break;
        }
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case DONE_ITEM_VIEW:
                ShoppingList donePurchaseModel = productsList.get(position);
                ((CompletedPurchaseViewHolder) holder).bind(donePurchaseModel, itemClickListener);
                break;
            case NOT_DONE_ITEM_VIEW:
                ShoppingList notDonePurchaseModel = productsList.get(position);
                ((NotCompletedPurchaseViewHolder) holder).bind(notDonePurchaseModel, itemClickListener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (productsList.get(position).isDone())
            return DONE_ITEM_VIEW;
        else
            return NOT_DONE_ITEM_VIEW;
    }

    static class NotCompletedPurchaseViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTV = itemView.findViewById(R.id.tv_product_item);
        private final CheckBox purchaseDoneChB = itemView.findViewById(R.id.chb_purchase_done);

        public NotCompletedPurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ShoppingList currentProduct, ProductItemClickListener clickListener) {
            productNameTV.setText(currentProduct.getProductName());

            purchaseDoneChB.setOnCheckedChangeListener((checkBoxView, isChecked) -> {
                checkBoxView.setChecked(false);
                clickListener.purchaseDoneChanged(currentProduct.getId());
            });
        }
    }

    static class CompletedPurchaseViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTV = itemView.findViewById(R.id.tv_product_name);
        private final TextView purchaseTime = itemView.findViewById(R.id.tv_purchase_time);
        private final TextView coast = itemView.findViewById(R.id.tv_coast);
        private final ImageView deleteProduct = itemView.findViewById(R.id.iv_purchase_delete);

        public CompletedPurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ShoppingList currantProduct, ProductItemClickListener clickListener) {
            // o'chirilgan tekst
            SpannableString spannedTaskName = new SpannableString(currantProduct.getProductName());
            spannedTaskName.setSpan(new StrikethroughSpan(),
                    0, spannedTaskName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productNameTV.setText(spannedTaskName);
            purchaseTime.setText(currantProduct.getPurchaseTimeInString());
            String tmpCoast = currantProduct.getQuantity() + " " + currantProduct.getCost() + " " + sumText;
            coast.setText(tmpCoast);

            deleteProduct.setOnClickListener(deleteProductView -> {
                Log.d("myTag", "delete button clicked");
                clickListener.onDeleteClicked(currantProduct.getId());
            });
        }
    }

    public void setData(List<ShoppingList> newProducts) {
        if (productsList == null)
            productsList = newProducts;
        else {
            productsList.clear();
            productsList.addAll(newProducts);
        }
    }

    public interface ProductItemClickListener {

        void purchaseDoneChanged(int id);

        void onDeleteClicked(int id);
    }

}
