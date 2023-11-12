package uz.admiraldev.noteandtodoapp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.models.Task;
import uz.admiraldev.noteandtodoapp.viewmodels.TasksViewModel;

public class TaskAddDialog extends DialogFragment {
    private AppCompatButton btnDone, btnNext;
    private final TasksViewModel tasksViewModel;
    TextInputEditText taskName;
    private boolean isUpdateTask = false;
    private boolean isRestoreTask = false;
    SimpleDateFormat dateFormat;
    private ImageView btnClose;
    private final int position;
    public TextView addDeadlineDate, addDeadlineTime, dialogTitle, deadlineTitle;


    public TaskAddDialog(TasksViewModel tasksViewModel, int position) {
        this.position = position;
        this.tasksViewModel = tasksViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_add_task, container, false);

        btnClose = dialogView.findViewById(R.id.btn_close);
        btnDone = dialogView.findViewById(R.id.btn_done);
        deadlineTitle = dialogView.findViewById(R.id.tv_deadline);
        btnNext = dialogView.findViewById(R.id.btn_next_task);
        taskName = dialogView.findViewById(R.id.et_task_name);
        addDeadlineDate = dialogView.findViewById(R.id.tv_dead_date);
        addDeadlineTime = dialogView.findViewById(R.id.tv_time);
        dialogTitle = dialogView.findViewById(R.id.tv_add_task_dialog_title);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        return dialogView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog myDialog = super.onCreateDialog(savedInstanceState);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return myDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isUpdateTask) {
            Task currentTask = tasksViewModel.getCurrentTask();
            taskName.setText(currentTask.getTaskName());
            addDeadlineTime.setText(currentTask.getDeadlineTime());
            addDeadlineDate.setText(currentTask.getDeadlineDateString());
            dialogTitle.setText(getString(R.string.edit_task_title));
            deadlineTitle.setText(getString(R.string.edit_task_deadline));
            btnNext.setVisibility(View.GONE);
            btnDone.setText(getString(R.string.edit_task_btn_text));
        } else if (isRestoreTask) {
            Task currentTask = tasksViewModel.getCurrentTask();
            btnNext.setVisibility(View.GONE);
            dialogTitle.setText(getString(R.string.repeat_task_title));
            taskName.setText(currentTask.getTaskName());
            addDeadlineTime.setText(currentTask.getDeadlineTime());
            addDeadlineDate.setText(currentTask.getDeadlineDateString());
        } else {
            addDeadlineTime.setText(tasksViewModel.getCurrentTime());
            addDeadlineDate.setText(tasksViewModel.getCurrentDate());
        }

        btnDone.setOnClickListener(btnDoneView -> {
            if (taskName.getText() != null && !taskName.getText().toString().trim().isEmpty()) {
                String tempText = taskName.getText().toString().trim();
                if (isUpdateTask) {
                    tasksViewModel.updateTask(tempText, position);
                } else {
                    addNewTask(tempText);
                }
                dismiss();
            } else
                Toast.makeText(requireContext(), requireContext().getString(R.string.empty_task_name),
                        Toast.LENGTH_SHORT).show();
        });

        btnNext.setOnClickListener(btnNextView -> {
            if (taskName.getText() != null && !taskName.getText().toString().trim().isEmpty()) {
                String tempText = taskName.getText().toString().trim();
                addNewTask(tempText);
                taskName.setText(null);
            } else
                Toast.makeText(requireContext(), requireContext().getString(R.string.empty_task_name),
                        Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(btnCloseView -> dismiss());
        addDeadlineTime.setOnClickListener(btnAddDeadlineTimeView -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTitleText(requireContext().getString(R.string.deadline_txt))
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                    .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();
            timePicker.show(getParentFragmentManager(), "TIME_PICKER");

            timePicker.addOnPositiveButtonClickListener(view1 -> {
                tasksViewModel.setDeadlineHour(timePicker.getHour());
                tasksViewModel.setDeadlineMin(timePicker.getMinute());
                String time = timePicker.getHour() + ":" + timePicker.getMinute();
                addDeadlineTime.setText(time);
            });
        });

        addDeadlineDate.setOnClickListener(btnAddDeadlineDateView -> {
            MaterialDatePicker<Long> datePicker = tasksViewModel.getDatePicker();
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (datePicker.getSelection() != null) {
                    tasksViewModel.setDeadlineDate(datePicker.getSelection());
                    addDeadlineDate.setText(dateFormat.format(datePicker.getSelection()));
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUpdateTask = false;
        isRestoreTask = false;
    }

    private void addNewTask(String taskName) {
        if (isRestoreTask)
            tasksViewModel.setShowCompletedTasks(true);
        tasksViewModel.insertTask(taskName);
    }

    public void setIsUpdateTrue() {
        this.isUpdateTask = true;
    }

    public void setIsRestoreTrue() {
        this.isRestoreTask = true;
    }
}
