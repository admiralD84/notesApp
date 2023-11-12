package uz.admiraldev.noteandtodoapp.adapters;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.models.Task;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Task> taskList;
    private static final int NOT_DONE_ITEM_VIEW = 1;
    private static final int DONE_ITEM_VIEW = 2;
    private final TaskItemClickListener mTaskItemClickListener;

    public TasksAdapter(TaskItemClickListener mTaskItemClickListener) {
        taskList = new ArrayList<>();
        this.mTaskItemClickListener = mTaskItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder myViewHolder;
        switch (viewType) {
            case DONE_ITEM_VIEW:
                View doneItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_completed_tasks, parent, false);
                myViewHolder = new CompletedTaskViewHolder(doneItemView);
                break;
            case NOT_DONE_ITEM_VIEW:
            default:
                View notCompletedItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tasks, parent, false);
                myViewHolder = new NotCompletedTaskViewHolder(notCompletedItemView);
                break;
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case DONE_ITEM_VIEW:
                Task doneTaskModel = taskList.get(position);
                ((CompletedTaskViewHolder) holder).bind(doneTaskModel, mTaskItemClickListener);
                break;
            case NOT_DONE_ITEM_VIEW:
                Task notDoneTaskModel = taskList.get(position);
                ((NotCompletedTaskViewHolder) holder).bind(notDoneTaskModel, mTaskItemClickListener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (taskList.get(position).isDone())
            return DONE_ITEM_VIEW;
        else
            return NOT_DONE_ITEM_VIEW;
    }

    static class NotCompletedTaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView deadlineTime = itemView.findViewById(R.id.tv_deadline_time);
        private final TextView taskAddedTime = itemView.findViewById(R.id.tv_added_time);
        private final CheckBox isDoneCheckbox = itemView.findViewById(R.id.chb_done);
        private final TextView taskText = itemView.findViewById(R.id.tv_task_text);
        private final LinearLayout hideShowLayout = itemView.findViewById(R.id.hide_show_layout);
        private final ImageView deleteTask = itemView.findViewById(R.id.iv_task_delete);
        private final ImageView editTask = itemView.findViewById(R.id.iv_task_edit);

        public NotCompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Task currentTask, TaskItemClickListener clickListener) {
            taskText.setText(currentTask.getTaskName());
            deadlineTime.setText(currentTask.getDeadlineTimeString());
            taskAddedTime.setText(currentTask.getAddedTime());

            itemView.setOnClickListener(itemview -> {
                if (clickListener != null) {
                    int visibility = hideShowLayout.getVisibility();
                    if (visibility == 0) {
                        hideShowLayout.animate()
                                .setDuration(300)
                                .translationY(-100)
                                .alpha(0)
                                .withEndAction(() -> hideShowLayout.setVisibility(View.GONE)).start();
                    } else {
                        hideShowLayout.setTranslationY(-100);
                        hideShowLayout.setAlpha(0.2f);
                        hideShowLayout.setVisibility(View.VISIBLE);
                        hideShowLayout.animate().setDuration(500)
                                .translationY(0)
                                .alpha(1f)
                                .start();
                    }
                }
            });

            editTask.setOnClickListener(taskEditView ->
                    clickListener.taskEditBtnClicked(currentTask.getId(), getAdapterPosition()));

            deleteTask.setOnClickListener(taskDeleteView ->
                    clickListener.taskDeleteBtnClicked(itemView, currentTask.getTaskName(),
                            currentTask.getId(), getAdapterPosition()));

            isDoneCheckbox.setOnCheckedChangeListener((checkBoxView, isChecked) -> {
                currentTask.setDone(isChecked);
                clickListener.checkBoxChecked(currentTask.getId(), getAdapterPosition());
            });
        }
    }

    static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskName = itemView.findViewById(R.id.tv_done_task_text);
        private final LinearLayout hideShowLayout = itemView.findViewById(R.id.hide_show_layout);
        private final TextView doneTime = itemView.findViewById(R.id.tv_completed_time);
        private final TextView addedTime = itemView.findViewById(R.id.tv_added_time);
        private final ImageView repeatTask = itemView.findViewById(R.id.iv_task_restore);
        private final ImageView deleteTask = itemView.findViewById(R.id.iv_done_task_delete);

        public CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Task task, TaskItemClickListener clickListener) {
            // o'chirilgan tekst
            SpannableString spannedTaskName = new SpannableString(task.getTaskName());
            spannedTaskName.setSpan(new StrikethroughSpan(),
                    0, spannedTaskName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            taskName.setText(spannedTaskName);
            addedTime.setText(task.getAddedTime());
            doneTime.setText(task.getDoneTimeString());

            repeatTask.setOnClickListener(repeatTaskBtn ->
                    clickListener.taskRepeatBtnClicked(task.getId(), getAdapterPosition()));

            deleteTask.setOnClickListener(deleteTaskBtn ->
                    clickListener.taskDeleteBtnClicked(itemView,
                            task.getTaskName(),
                            task.getId(),
                            getAdapterPosition()));

            itemView.setOnClickListener(itemview -> {
                if (clickListener != null) {
                    int visibility = hideShowLayout.getVisibility();
                    if (visibility == 0) {
                        hideShowLayout.animate()
                                .setDuration(300)
                                .translationY(-100)
                                .alpha(0)
                                .withEndAction(() -> hideShowLayout.setVisibility(View.GONE)).start();
                    } else {
                        hideShowLayout.setTranslationY(-100);
                        hideShowLayout.setAlpha(0.2f);
                        hideShowLayout.setVisibility(View.VISIBLE);
                        hideShowLayout.animate().setDuration(500)
                                .translationY(0)
                                .alpha(1f)
                                .start();
                    }
                }
            });

        }
    }

    public void setData(List<Task> newTasks) {
        if (taskList == null)
            taskList = newTasks;
        else {
            taskList.clear();
            taskList.addAll(newTasks);
        }
    }

    public interface TaskItemClickListener {
        void taskDeleteBtnClicked(View itemView, String taskName, int id, int position);

        void taskEditBtnClicked(int id, int position);

        void taskRepeatBtnClicked(int id, int position);

        void checkBoxChecked(int id, int position);
    }
}
