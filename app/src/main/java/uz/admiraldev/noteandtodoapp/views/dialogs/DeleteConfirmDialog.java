package uz.admiraldev.noteandtodoapp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import uz.admiraldev.noteandtodoapp.R;


public class DeleteConfirmDialog extends DialogFragment {
    DeleteConfirmDialog.ClickListener listener;
    String alertTitle, alertMessage;
    boolean isVisibilityNegativeBtn = false,
            isVisibilityPositiveBtn = false;

    AppCompatButton negativeBtn, positiveBtn;

    public DeleteConfirmDialog(ClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_delete_confirm, container, false);
        TextView titleTextView = dialogView.findViewById(R.id.tv_alert_title);
        TextView alertMessageTextView = dialogView.findViewById(R.id.tv_alert_msg);
        negativeBtn = dialogView.findViewById(R.id.btn_negative);
        positiveBtn = dialogView.findViewById(R.id.btn_positive);
        if (alertTitle != null)
            titleTextView.setText(alertTitle);
        if (alertMessage != null)
            alertMessageTextView.setText(alertMessage);
        if (!isVisibilityNegativeBtn)
            setVisibilityNegativeBtn(false);
        if (!isVisibilityPositiveBtn)
            setVisibilityPositiveBtn(false);
        return dialogView;
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
        positiveBtn.setOnClickListener(view1 -> {
            listener.onPositiveButtonClicked();
            DeleteConfirmDialog.this.dismiss();
        });
        negativeBtn.setOnClickListener(view1 -> {
            listener.onNegativeButtonClicked();
//            DeleteConfirmDialog.this.dismiss();
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void setVisibilityNegativeBtn(boolean visibilityNegativeBtn) {
        isVisibilityNegativeBtn = visibilityNegativeBtn;
    }

    public void setVisibilityPositiveBtn(boolean visibilityPositiveBtn) {
        isVisibilityPositiveBtn = visibilityPositiveBtn;
    }


    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public interface ClickListener {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }
}
