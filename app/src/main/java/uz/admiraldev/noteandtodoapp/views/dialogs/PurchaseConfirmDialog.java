package uz.admiraldev.noteandtodoapp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.DialogPurchaseConfirmBinding;

public class PurchaseConfirmDialog extends DialogFragment {
    PurchaseConfirmDialog.ClickListener clickListener;
    DialogPurchaseConfirmBinding binding;

    public PurchaseConfirmDialog(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogPurchaseConfirmBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog myDialog = super.onCreateDialog(savedInstanceState);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(myDialog.getWindow()).getAttributes()
                .windowAnimations = R.style.DialogFragmentAnimation;
        return myDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnPositive.setOnClickListener(view1 -> {
            String purchaseCoast = Objects.requireNonNull(binding.etPrice.getText()).toString().trim();
            String purchaseQuantity = Objects.requireNonNull(binding.etQuantity.getText()).toString().trim();
            if (purchaseCoast.isEmpty()) {
                purchaseCoast = "---";
            }
            if (purchaseQuantity.isEmpty()) {
                purchaseQuantity = "---";
            }
            clickListener.onPositiveButtonClicked(purchaseCoast, purchaseQuantity);
            PurchaseConfirmDialog.this.dismiss();
        });
        binding.btnNegative.setOnClickListener(view1 -> PurchaseConfirmDialog.this.dismiss());
        super.onViewCreated(view, savedInstanceState);
    }

    public interface ClickListener {
        void onPositiveButtonClicked(String coast, String quantity);
    }
}
