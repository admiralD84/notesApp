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
import uz.admiraldev.noteandtodoapp.viewmodels.ShopListViewModel;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NOT_DONE_ITEM_VIEW = 1;
    private static final int DONE_ITEM_VIEW = 2;
    private List<ShoppingList> productsList;
    private static String sumText;
    private final ShopListViewModel shopListViewModel;
    private final ShoppingListAdapter.ProductItemClickListener itemClickListener;

    public ShoppingListAdapter(ProductItemClickListener itemClickListener,
                               Context context,
                               ShopListViewModel shopListViewModel) {
        this.itemClickListener = itemClickListener;
        this.shopListViewModel = shopListViewModel;
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
                myViewHolder = new CompletedPurchaseViewHolder(doneItemView, shopListViewModel);
                break;
            case NOT_DONE_ITEM_VIEW:
            default:
                View notCompletedItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shop_list, parent, false);
                myViewHolder = new NotCompletedPurchaseViewHolder(notCompletedItemView, shopListViewModel);
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
        private final ShopListViewModel shopListViewModel;
        private final ImageView itemSelect = itemView.findViewById(R.id.iv_select);
        private boolean isActionDelete = false;
        private boolean isSelectedItem = false;

        public NotCompletedPurchaseViewHolder(@NonNull View itemView, ShopListViewModel shopListViewModel) {
            super(itemView);
            this.shopListViewModel = shopListViewModel;
        }

        public void bind(ShoppingList currentProduct, ProductItemClickListener clickListener) {
            productNameTV.setText(currentProduct.getProductName());
            shopListViewModel.isActionDelete.observeForever(isDelete -> {
                if (isDelete) {
                    isActionDelete = true;
                    purchaseDoneChB.setVisibility(View.GONE);
                    itemSelect.setVisibility(View.VISIBLE);
                } else {
                    isActionDelete = false;
                    purchaseDoneChB.setVisibility(View.VISIBLE);
                    itemSelect.setImageResource(R.drawable.ic_circle);
                    itemSelect.setVisibility(View.GONE);
                }
            });
            purchaseDoneChB.setOnCheckedChangeListener((checkBoxView, isChecked) -> {
                checkBoxView.setChecked(false);
                clickListener.purchaseDoneChanged(currentProduct.getId());
            });
            itemView.setOnClickListener(notPurchasedItemView -> {
                if (isActionDelete) {
                    if (isSelectedItem) {
                        isSelectedItem = false;
                        itemSelect.setImageResource(R.drawable.ic_circle);
                    } else {
                        isSelectedItem = true;
                        itemSelect.setImageResource(R.drawable.ic_checked_circle);
                    }
                    clickListener.onItemClicked(getAdapterPosition(), currentProduct.getId());
                }
            });
        }
    }

    static class CompletedPurchaseViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTV = itemView.findViewById(R.id.tv_product_name);
        private final TextView purchaseTime = itemView.findViewById(R.id.tv_purchase_time);
        private final TextView coast = itemView.findViewById(R.id.tv_coast);
        private final ImageView itemSelect = itemView.findViewById(R.id.iv_completed_select);
        private final ShopListViewModel shopListViewModel;
        private boolean isActionDelete = false;
        private boolean isSelectedItem = false;

        public CompletedPurchaseViewHolder(@NonNull View complatedItemView, ShopListViewModel shopListViewModel) {
            super(complatedItemView);
            this.shopListViewModel = shopListViewModel;
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
            shopListViewModel.isActionDelete.observeForever(isDelete -> {
                if (isDelete) {
                    isActionDelete = false;
                    itemSelect.setVisibility(View.VISIBLE);
                } else {
                    isActionDelete = true;
                    itemSelect.setImageResource(R.drawable.ic_circle);
                    itemSelect.setVisibility(View.GONE);
                }
            });
            itemView.setOnClickListener(purchasedItem -> {
                if (!isActionDelete) {
                    if (isSelectedItem) {
                        isSelectedItem = false;
                        itemSelect.setImageResource(R.drawable.ic_circle);
                    } else {
                        isSelectedItem = true;
                        itemSelect.setImageResource(R.drawable.ic_checked_circle);
                    }
                    clickListener.onItemClicked(getAdapterPosition(), currantProduct.getId());
                }
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

        void onItemClicked(int position, int productId);

    }

}
