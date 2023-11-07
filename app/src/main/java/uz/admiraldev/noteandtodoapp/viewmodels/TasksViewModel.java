package uz.admiraldev.noteandtodoapp.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.Task;

public class TasksViewModel extends ViewModel {
    private final ExecutorService myExecutor;
    private boolean isHidden = true;
    private List<Task> tasksList;
    int deadlineHour, deadlineMin;
    Long deadlineDate;
    Calendar calendar;
    Task currentTask;
    SimpleDateFormat timeFormat;
    SimpleDateFormat dateFormat;
    LocalTime time;
    boolean isChanged;
    public MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();
    private final MutableLiveData<Task> selectedTaskLiveData = new MutableLiveData<>();

    public TasksViewModel() {
        myExecutor = Executors.newSingleThreadExecutor();
        tasksList = new ArrayList<>();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        time = LocalTime.now();
        taskDefine(isHidden);
    }

    public void getTasksList() {
        myExecutor.execute(() -> {
            try {
                tasksList = MainActivity.getTaskDatabase().taskDao().getTasks();
                new Handler(Looper.getMainLooper()).post(() -> tasksLiveData.setValue(tasksList));
            } catch (Exception e) {
                Log.d("myTag", "getTasksList error: " + e.getMessage());
            }
        });
    }

    public void taskDefine(boolean show) {
        isHidden = show;
        if (show) {
            showAllTasks();
        } else
            showOnlyNotCompletedTasks();
        getTasksList();
    }

    public void showOnlyNotCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (int i = 0; i < tasksList.size() - 1; i++) {
            if (!tasksList.get(i).isDone()) {
                completedTasks.add(tasksList.get(i));
            }
        }
        tasksLiveData.setValue(completedTasks);
    }

    public void showAllTasks() {
        tasksLiveData.setValue(tasksList);
    }

    public void setTaskById(int id) {
        tasksList.forEach(task -> {
            if (task.getId() == id) {
                selectedTaskLiveData.setValue(currentTask);
            }
        });
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setDeadlineHour(int deadlineHour) {
        isChanged = true;
        this.deadlineHour = deadlineHour;
    }

    public void setDeadlineMin(int deadlineMin) {
        this.deadlineMin = deadlineMin;
    }

    public void setDeadlineDate(long deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void deleteTask(int taskId, int position) {
        tasksList.remove(position);
        tasksLiveData.setValue(tasksList);
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().deleteTask(taskId);
            } catch (Exception e) {
                Log.d("myTag", "delete task error: " + e.getMessage());
            }
        });
    }

    public void updateIsDoneField(int taskId, boolean isDone) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().updateTaskDoneField(
                        Calendar.getInstance().getTimeInMillis(),
                        taskId,
                        isDone);
            } catch (Exception e) {
                Log.d("myTag", "delete task error: " + e.getMessage());
            }
        });
        getTasksList();
    }

    public void insertTask(String taskName) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().insert(newTaskBuilder(taskName));
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
    }

    public String getCurrentTime() {
        LocalTime newTime = time.plusHours(4);
        if (newTime.getMinute() < 10)
            return newTime.getHour() + ":0" + newTime.getMinute();
        else
            return newTime.getHour() + ":" + newTime.getMinute();
    }

    public String getCurrentDate() {
        return dateFormat.format(calendar.getTime());
    }

    public Long getCurrentDateLong() {
        return calendar.getTimeInMillis();
    }

    private Task newTaskBuilder(String taskName) {
        String selectedTime;
        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());
        if (deadlineDate == null) deadlineDate = getCurrentDateLong();
        if (!isChanged) {
            selectedTime = getCurrentTime();
        } else {
            if (deadlineMin < 10)
                selectedTime = deadlineHour + ":0" + deadlineMin;
            else
                selectedTime = deadlineHour + ":" + deadlineMin;
        }
        String addedTime = currentDate + " " + currentTime;

        return new Task(taskName, addedTime, false, deadlineDate, selectedTime);
    }

    public void updateTask(String taskName) {
        if (selectedTaskLiveData.getValue() != null) {
            selectedTaskLiveData.getValue().setTaskName(taskName);
            if (deadlineDate != null)
                selectedTaskLiveData.getValue().setDeadlineDate(deadlineDate);
            if (isChanged)
                selectedTaskLiveData.getValue().setDeadlineTime(deadlineHour + ":" + deadlineMin);
        }
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().updateTask(selectedTaskLiveData.getValue());
            } catch (Exception e) {
                Log.d("myTag", "update task error: " + e.getMessage());
            }
        });
    }

    public MaterialDatePicker<Long> getDatePicker() {
        CalendarConstraints.DateValidator dateValidator = DateValidatorPointForward.from(calendar.getTimeInMillis());
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setStart(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        constraintsBuilder.setValidator(dateValidator);
        return builder.setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select date of deadline")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
    }

    public void getSortedTasks(String fieldName, boolean isASC) {
        myExecutor.execute(() -> {
            try {
                if (fieldName.equals("deadlineDate"))
                    tasksList = MainActivity.getTaskDatabase().taskDao().getSortedTasksByDeadline(isASC);
                else if (fieldName.equals("isDone"))
                    tasksList = MainActivity.getTaskDatabase().taskDao().getSortedTasksByIsDoneState(isASC);
                new Handler(Looper.getMainLooper()).post(() -> tasksLiveData.setValue(tasksList));
            } catch (Exception e) {
                Log.d("myTag", "getNotesLiveData error: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }

}
