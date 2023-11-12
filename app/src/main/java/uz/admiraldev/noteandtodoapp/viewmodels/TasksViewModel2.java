package uz.admiraldev.noteandtodoapp.viewmodels;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
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

public class TasksViewModel2 extends ViewModel {
    private final ExecutorService myExecutor = Executors.newSingleThreadExecutor();
    private List<Task> tasksList;
    private Task currentTask;
    int deadlineHour, deadlineMin;
    Long deadlineDate;
    Calendar calendar;
    SimpleDateFormat timeFormat;
    SimpleDateFormat dateFormat;
    LocalTime time;
    boolean isChanged;
    public MediatorLiveData<List<Task>> tasksLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Task> selectedTaskLiveData = new MutableLiveData<>();
    private boolean isCompletedTasksHidden;

  /*  public TasksViewModel() {
        filteredTasksLiveData.addSource(allTasksLiveData, tasks -> {
            if (isCompletedTasksHidden) {
                List<Task> notCompletedTasks = new ArrayList<>();
                for (Task task : tasks) {
                    if (!task.isDone()) {
                        notCompletedTasks.add(task);
                    }
                }
                filteredTasksLiveData.setValue(notCompletedTasks);
            } else {
                filteredTasksLiveData.setValue(tasks);
            }
        });

        tasksLiveData.addSource(filteredTasksLiveData, tasks -> tasksLiveData.setValue(tasks));
    }*/

   /* public void setCompletedTasksHidden(boolean completedTasksHidden) {
        isCompletedTasksHidden = completedTasksHidden;
        List<Task> currentTasks = allTasksLiveData.getValue();
        if (currentTasks != null) {
            filteredTasksLiveData.setValue(currentTasks);
        }
    }*/

     public TasksViewModel2() {
         tasksList = new ArrayList<>();
         calendar = Calendar.getInstance();
         dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
         timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
         time = LocalTime.now();
         getTasksList();
     }

    public void setCompletedTasksHidden(boolean completedTasksHidden) {
        isCompletedTasksHidden = completedTasksHidden;
    }

    public void getTasksList() {
        myExecutor.execute(() -> {
            try {
                tasksList = MainActivity.getTaskDatabase().taskDao().getTasks();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isCompletedTasksHidden)
                        showOnlyNotCompletedTasks();
                    else
                        showAllTasks();
                });
            } catch (Exception e) {
                Log.d("myTag", "getTasksList error: " + e.getMessage());
            }
        });
    }

    public void setTaskById(int id) {
        tasksList.forEach(task -> {
            if (task.getId() == id) {
                selectedTaskLiveData.setValue(currentTask);
                currentTask = task;
            }
        });
    }

    public void showOnlyNotCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (int i = 0; i < tasksList.size(); i++) {
            if (!tasksList.get(i).isDone()) {
                completedTasks.add(tasksList.get(i));
            }
        }
        tasksLiveData.setValue(completedTasks);
    }

    public void getSortedTasks(String fieldName, boolean isASC) {
        myExecutor.execute(() -> {
            try {
                if (fieldName.equals("deadlineDate"))
                    tasksList = MainActivity.getTaskDatabase().taskDao().getSortedTasksByDeadline(isASC);
                else if (fieldName.equals("isDone"))
                    tasksList = MainActivity.getTaskDatabase().taskDao().getSortedTasksByIsDoneState(isASC);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (fieldName.equals("isDone"))
                        showAllTasks();
                    else
                        showOnlyNotCompletedTasks();
                });
            } catch (Exception e) {
                Log.d("myTag", "getNotesLiveData error: " + e.getMessage());
            }
        });
    }

    public void showAllTasks() {
        tasksLiveData.setValue(tasksList);
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
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().deleteTask(taskId);
            } catch (Exception e) {
                Log.d("myTag", "delete task error: " + e.getMessage());
            }
        });
        tasksList.remove(position);
        if (!isCompletedTasksHidden)
            showAllTasks();
        else
            showOnlyNotCompletedTasks();
    }

    public void updateIsDoneField(int taskId, boolean isDone) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().updateTaskDoneField(
                        Calendar.getInstance().getTimeInMillis(),
                        taskId,
                        isDone);
            } catch (Exception e) {
                Log.d("myTag", "update task error: " + e.getMessage());
            }
          /*  new Handler(Looper.getMainLooper()).post(() -> {
                tasksList.get(position).setDone(isDone);
                if (isCompletedTasksHidden) {
                    showOnlyNotCompletedTasks();
                } else {
                    showAllTasks();
                }
            });*/
        });
    }

    public void insertTask(String taskName) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getTaskDatabase().taskDao().insert(newTaskBuilder(taskName));
            } catch (Exception e) {
                Log.d("myTag", "insert task error: " + e.getMessage());
            }
        });
        getTasksList();
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
        getTasksList();
    }

    public MaterialDatePicker<Long> getDatePicker() {
        CalendarConstraints.DateValidator dateValidator = DateValidatorPointForward.from(calendar.getTimeInMillis());
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setStart(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        constraintsBuilder.setValidator(dateValidator);
        return builder.setCalendarConstraints(constraintsBuilder.build()).setTitleText("Select date of deadline").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }
}
