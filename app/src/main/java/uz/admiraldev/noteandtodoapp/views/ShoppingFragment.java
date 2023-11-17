package uz.admiraldev.noteandtodoapp.views;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomappbar.BottomAppBar;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.adapters.ShoppingListAdapter;
import uz.admiraldev.noteandtodoapp.databinding.FragmentShoppingBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.ShopListViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;
import uz.admiraldev.noteandtodoapp.views.dialogs.PurchaseConfirmDialog;

public class ShoppingFragment extends Fragment implements ShoppingListAdapter.ProductItemClickListener {
    private FragmentShoppingBinding binding;
    private ShoppingListAdapter shopAdapter;
    private boolean isPurchasedHidden = false;
    private DeleteConfirmDialog confirmDialog;
    private boolean isActionDelete = false;
    private boolean isEmpty = false;
    PurchaseConfirmDialog purchaseConfirmDialog;
    public static ShopListViewModel shoppingViewModel;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingBinding.inflate(getLayoutInflater(), container, false);
        confirmDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                shoppingViewModel.deleteSelectedProducts();
                binding.ivClearSelected.setVisibility(View.GONE);
                isActionDelete = !isActionDelete;
                shoppingViewModel.setIsActionDelete(isActionDelete);
            }

            @Override
            public void onNegativeButtonClicked() {
                confirmDialog.dismiss();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar.getVisibility() == View.GONE) {
            bottomAppBar.setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShopListViewModel.class);
        binding.rvShoppingList.setLayoutManager(new LinearLayoutManager(requireContext()));
        shopAdapter = new ShoppingListAdapter(this, requireContext(), shoppingViewModel);
        shoppingViewModel.shoppingListLive.observe(getViewLifecycleOwner(), products -> {
            if (products.size() != 0) {
                binding.progress.setVisibility(View.GONE);
                binding.ivProductDelete.setVisibility(View.VISIBLE);
                binding.ivShowHidePurchased.setVisibility(View.VISIBLE);
                binding.ivShowHidePurchased.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue2)));
                binding.rvShoppingList.setVisibility(View.VISIBLE);
                isEmpty = false;
                if (binding.tvEmptyList.getVisibility() == View.VISIBLE)
                    binding.tvEmptyList.setVisibility(View.GONE);
                shopAdapter.setData(products);
                binding.rvShoppingList.setAdapter(shopAdapter);
            } else {
                isEmpty = true;
                binding.progress.setVisibility(View.GONE);
                binding.tvEmptyList.setText(getString(R.string.empty_shopping_list));
                binding.ivProductDelete.setVisibility(View.GONE);
                binding.ivShowHidePurchased.setVisibility(View.GONE);
                binding.rvShoppingList.setVisibility(View.GONE);
                binding.tvEmptyList.setVisibility(View.VISIBLE);
            }
        });
        binding.ivProductDelete.setOnClickListener(view1 -> {
            int selectedItemsCount = shoppingViewModel.getSelectedProductsCount();
            if (selectedItemsCount > 0) {
                confirmDialog.setAlertTitle(getString(R.string.product_delete_dialog_title));
                confirmDialog.setAlertMessage(getString(R.string.product_delete_dialog_desc));
                confirmDialog.setVisibilityPositiveBtn(true);
                confirmDialog.setVisibilityNegativeBtn(true);
                confirmDialog.show(requireActivity().getSupportFragmentManager(),
                        DeleteConfirmDialog.class.toString());
            } else {
                isActionDelete = !isActionDelete;
                shoppingViewModel.setIsActionDelete(isActionDelete);
            }
        });
        shoppingViewModel.isActionDelete.observe(getViewLifecycleOwner(), isDelete -> {
            if (!isEmpty) {
                if (isDelete) {
                    binding.ivShowHidePurchased.setVisibility(View.GONE);
                    binding.ivClearSelected.setImageTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue2)));
                    binding.ivClearSelected.setVisibility(View.VISIBLE);
                } else {
                    binding.ivClearSelected.setVisibility(View.GONE);
                    binding.ivClearSelected.setImageTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue2)));
                    binding.ivShowHidePurchased.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.ivShowHidePurchased.setOnClickListener(hideCompletedBtn -> {
            isPurchasedHidden = !isPurchasedHidden;
            if (isPurchasedHidden) {
                shoppingViewModel.setShowPurchasedProducts(false);
                binding.ivShowHidePurchased.setImageResource(R.drawable.ic_hide);
            } else {
                shoppingViewModel.setShowPurchasedProducts(true);
                binding.ivShowHidePurchased.setImageResource(R.drawable.ic_show);
            }
            binding.ivShowHidePurchased.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue2)));
        });

        binding.ivClearSelected.setOnClickListener(clearSelectedBtn -> {
            isActionDelete = !isActionDelete;
            shoppingViewModel.setIsActionDelete(isActionDelete);
            shoppingViewModel.addSelectedProductsId(-1);
        });
    }

    @Override
    public void purchaseDoneChanged(int id) {
        purchaseConfirmDialog = new PurchaseConfirmDialog((coast, quantity) -> {
            shoppingViewModel.updateIsDoneField(id, quantity, coast);
            purchaseConfirmDialog.dismiss();
        });
        purchaseConfirmDialog.show(requireActivity().getSupportFragmentManager(),
                PurchaseConfirmDialog.class.toString());
    }

    @Override
    public void onItemClicked(int position, int productId) {
        shoppingViewModel.addSelectedProductsId(productId);
    }
}