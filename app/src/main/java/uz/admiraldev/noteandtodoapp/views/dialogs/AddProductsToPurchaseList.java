package uz.admiraldev.noteandtodoapp.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.AddProductBottomSheetBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.ShopListViewModel;

public class AddProductsToPurchaseList extends BottomSheetDialogFragment {
    private AddProductBottomSheetBinding binding;
    ShopListViewModel viewModel;

    public AddProductsToPurchaseList(ShopListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddProductBottomSheetBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.etProducts.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.etProducts, InputMethodManager.SHOW_IMPLICIT);

        binding.cancelButton.setOnClickListener(view1 -> dismiss());
        binding.btnSaveShoppingList.setOnClickListener(view1 -> {
            if (binding.etProducts.getText() != null) {
                String productName = binding.etProducts.getText().toString();
                saveData(productName);
            }
            dismiss();
        });
        binding.btnNext.setOnClickListener(view1 -> {
            if (binding.etProducts.getText() != null) {
                String productName = binding.etProducts.getText().toString();
                saveData(productName);
            }
        });
    }

    private void saveData(String productName) {
        if (!productName.isEmpty()) {
            viewModel.insertProduct(productName);
            viewModel.getShoppingList();
            binding.etProducts.setText("");
        } else {
            Toast.makeText(requireContext(), requireContext().getString(R.string.empty_product_name),
                    Toast.LENGTH_SHORT).show();
            binding.etProducts.requestFocus();
        }
    }
}
