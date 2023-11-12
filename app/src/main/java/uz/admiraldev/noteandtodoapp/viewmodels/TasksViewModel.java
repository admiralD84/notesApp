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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.Task;

public class TasksViewModel extends ViewModel {
    private final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final ExecutorService myExecutor = Executors.newSingleThreadExecutor();
    private List<Task> allTasks;
    private Long deadlineDate;
    private int deadlineHour, deadlineMin;
    private boolean isChanged;
    private Task currentTask;
    private boolean showCompletedTasks;
    public MutableLiveData<List<Task>> tasksLiveData = new MutableLiveData<>();

    public void getAllTasks() {
        myExecutor.execute(() -> {
            try {
                List<Task> fetchedTasks = MainActivity.getTaskDatabase().taskDao().getTasks();
                new Handler(Looper.getMainLooper()).post(() -> {
                    allTasks = fetchedTasks;
                    filteredTasks(allTasks);
                });
            } catch (Exception e) {
                Log.d("myTag", "getAllTasks exception : " + e.getMessage());
            }
        });
    }

    private void filteredTasks(List<Task> tasks) {
        if (showCompletedTasks) {
            tasksLiveData.setValue(tasks);
        } else {
            List<Task> notCompletedTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (!task.isDone()) {
                    notCompletedTasks.add(task);
                }
            }
            tasksLiveData.setValue(notCompletedTasks);
        }
    }

    public void setShowCompletedTasks(boolean showCompletedTasks) {
        this.showCompletedTasks = showCompletedTasks;
        if (allTasks == null) {
            getAllTasks();
        } else {
            filteredTasks(allTasks);
        }
    }

    public void deleteTask(int taskId, int position) {
        allTasks.remove(position);
        filteredTasks(allTasks);
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().deleteTask(taskId);
            } catch (Exception e) {
                Log.d("myTag", "delete task error: " + e.getMessage());
            }
        });
    }

    public void insertTask(String taskName) {
        Task newTask = newTaskBuilder(taskName);
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().insert(newTask);
                new Handler(Looper.getMainLooper()).post(() -> {
                    List<Task> localTasksList = tasksLiveData.getValue();
                    if (localTasksList == null) {
                        localTasksList = new ArrayList<>();
                        localTasksList.add(newTask);
                    } else
                        localTasksList.add(newTask);
                    filteredTasks(localTasksList);
                });
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
        getAllTasks();
    }

    public void updateIsDoneField(int taskId, boolean isDone, int position) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().updateTaskDoneField(
                        Calendar.getInstance().getTimeInMillis(),
                        taskId,
                        isDone);

                List<Task> updatedTasks = tasksLiveData.getValue();
                Task updatedTask = updatedTasks.get(position);
                updatedTask.setDone(isDone);
                updatedTask.setDeadlineDate(Calendar.getInstance().getTimeInMillis());
                updatedTasks.set(position, updatedTask);
                tasksLiveData.postValue(updatedTasks);
            } catch (Exception e) {
                Log.d("myTag", "update task error: " + e.getMessage());
            }
        });
    }

    public void getSortedTasks(String fieldName, boolean isASC) {
        myExecutor.execute(() -> {
            try {
                List<Task> sortedTasks;
                if (fieldName.equals("deadlineDate")) {
                    showCompletedTasks = false;
                    sortedTasks = MainActivity.getTaskDatabase().taskDao().getSortedTasksByDeadline(isASC);
                } else if (fieldName.equals("isDone")) {
                    showCompletedTasks = true;
                    sortedTasks = MainActivity.getTaskDatabase().taskDao().getSortedTasksByIsDoneState(isASC);
                } else
                    sortedTasks = new ArrayList<>();
                new Handler(Looper.getMainLooper()).post(() -> {
                    filteredTasks(sortedTasks);
                });
            } catch (Exception e) {
                Log.d("myTag", "getSortedTasks error: " + e.getMessage());
            }
        });
    }

    public void updateTask(String taskName, int position) {
        Task taskToUpdate = Objects.requireNonNull(tasksLiveData.getValue()).get(position);

        if (taskToUpdate != null) {
            taskToUpdate.setTaskName(taskName);
            if (deadlineDate != null) {
                taskToUpdate.setDeadlineDate(deadlineDate);
            }
            if (isChanged) {
                String time = (deadlineHour < 10 ? "0" + deadlineHour : deadlineHour) + ":" +
                        (deadlineMin < 10 ? "0" + deadlineMin : deadlineMin);
                taskToUpdate.setDeadlineTime(time);
            }

            myExecutor.execute(() -> {
                try {
                    MainActivity.getTaskDatabase().taskDao().updateTask(taskToUpdate);
                    List<Task> updatedTasks = tasksLiveData.getValue();
                    updatedTasks.set(position, taskToUpdate);
                    tasksLiveData.postValue(updatedTasks);
                } catch (Exception e) {
                    Log.d("myTag", "update task error: " + e.getMessage());
                }
            });
        }
    }


    private Task newTaskBuilder(String taskName) {
        String selectedTime;
        String currentDate = DATA_FORMAT.format(Calendar.getInstance().getTime());
        String currentTime = TIME_FORMAT.format(Calendar.getInstance().getTime());
        if (deadlineDate == null) deadlineDate = Calendar.getInstance().getTimeInMillis();
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

    public void setCurrentTask(int id) {
        allTasks.forEach(task -> {
            if (task.getId() == id) {
                currentTask = task;
            }
        });
    }

    public String getCurrentTime() {
        LocalTime newTime = LocalTime.now().plusHours(4);
        if (newTime.getMinute() < 10)
            return newTime.getHour() + ":0" + newTime.getMinute();
        else
            return newTime.getHour() + ":" + newTime.getMinute();
    }

    public String getCurrentDate() {
        return DATA_FORMAT.format(Calendar.getInstance().getTime());
    }

    public MaterialDatePicker<Long> getDatePicker() {
        CalendarConstraints.DateValidator dateValidator =
                DateValidatorPointForward.from(Calendar.getInstance().getTimeInMillis());
        CalendarConstraints.Builder constraintsBuilder =
                new CalendarConstraints.Builder().setStart(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        return builder.setCalendarConstraints(constraintsBuilder.setValidator(dateValidator).build())
                .setTitleText("Select date of deadline")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }
}
