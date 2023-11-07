package uz.admiraldev.noteandtodoapp.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomappbar.BottomAppBar;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.adapters.ShoppingListAdapter;
import uz.admiraldev.noteandtodoapp.databinding.FragmentShoppingBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.ShopListViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.PurchaseConfirmDialog;

public class ShoppingFragment extends Fragment implements ShoppingListAdapter.ProductItemClickListener {
    private FragmentShoppingBinding binding;
    private ShoppingListAdapter shopAdapter;
    PurchaseConfirmDialog purchaseConfirmDialog;
    public static ShopListViewModel shoppingViewModel;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingBinding.inflate(getLayoutInflater(), container, false);
        shoppingViewModel = new ViewModelProvider(requireActivity()).get(ShopListViewModel.class);
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
        binding.rvShoppingList.setLayoutManager(new LinearLayoutManager(requireContext()));
        shopAdapter = new ShoppingListAdapter(this, requireContext());
        shoppingViewModel.shoppingListLive.observe(getViewLifecycleOwner(), products -> {
            if (products.size() != 0) {
                binding.progress.setVisibility(View.GONE);
                binding.rvShoppingList.setVisibility(View.VISIBLE);
                if (binding.tvEmptyList.getVisibility() == View.VISIBLE)
                    binding.tvEmptyList.setVisibility(View.GONE);
                shopAdapter.setData(products);
                binding.rvShoppingList.setAdapter(shopAdapter);
            } else {
                binding.progress.setVisibility(View.GONE);
                binding.tvEmptyList.setText(getString(R.string.empty_shopping_list));
                binding.rvShoppingList.setVisibility(View.GONE);
                binding.tvEmptyList.setVisibility(View.VISIBLE);
            }
        });
        binding.ivProductDelete.setOnClickListener(view1 -> {
//            shoppingViewModel.isSelectItemsOn();
        });

    }

    /*   @Override
       public void onProductItemClick() {
           Toast.makeText(requireContext(), "item clicked", Toast.LENGTH_SHORT).show();
       }

   */
    @Override
    public void purchaseDoneChanged(int id) {
        purchaseConfirmDialog = new PurchaseConfirmDialog((coast, quantity) -> {
            shoppingViewModel.updateIsDoneField(id, true, quantity, coast);
            purchaseConfirmDialog.dismiss();
        });
        purchaseConfirmDialog.show(requireActivity().getSupportFragmentManager(),
                PurchaseConfirmDialog.class.toString());
    }

    @Override
    public void onDeleteClicked(int id) {
        Toast.makeText(requireContext(), "delete clicked", Toast.LENGTH_SHORT).show();
    }
}