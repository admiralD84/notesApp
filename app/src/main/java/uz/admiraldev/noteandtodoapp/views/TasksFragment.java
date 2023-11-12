package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.adapters.TasksAdapter;
import uz.admiraldev.noteandtodoapp.databinding.FragmentToDoBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.TasksViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;
import uz.admiraldev.noteandtodoapp.views.dialogs.TaskAddDialog;

public class TasksFragment extends Fragment implements TasksAdapter.TaskItemClickListener {
    private FragmentToDoBinding binding;
    private TasksAdapter tasksAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DeleteConfirmDialog deleteDialog;
    private boolean showCompleted;
    public static TasksViewModel tasksViewModel;
    boolean deadlineFired = true;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        showCompleted = sharedPreferences.getBoolean("isHiddenCompleted", true);
        tasksViewModel.setShowCompletedTasks(showCompleted);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentToDoBinding.inflate(getLayoutInflater(), container, false);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton floatingActionButton =
                requireActivity().findViewById(R.id.floatingActionButton);
        if (floatingActionButton.getVisibility() == View.GONE) {
            floatingActionButton.setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.bottomAppBar).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tasksAdapter = new TasksAdapter(this);
        tasksViewModel.tasksLiveData.observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null && tasks.size() != 0) {
                binding.progress.setVisibility(View.GONE);
                binding.rvTasks.setVisibility(View.VISIBLE);
                if (binding.tvEmptyList.getVisibility() == View.VISIBLE)
                    binding.tvEmptyList.setVisibility(View.GONE);
                tasksAdapter.setData(tasks);
                binding.rvTasks.setAdapter(tasksAdapter);
            } else {
                emptyTaskList();
            }
        });
        binding.ivFilter.setOnClickListener(filterView -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), filterView);
            if (showCompleted) {
                popupMenu.inflate(R.menu.task_filter_popup_menu);
            } else {
                popupMenu.inflate(R.menu.task_filter_popup_menu2);
            }
            try {
                Method method = popupMenu.getMenu().getClass()
                        .getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popupMenu.getMenu(), true);
            } catch (Exception e) {
                Log.d("myTag", "note popup menu error" + e.getMessage());
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.hide_completed) {
                    showCompleted = false;
                    editor.putBoolean("isHiddenCompleted", false);
                    editor.apply();
                    tasksViewModel.setShowCompletedTasks(false);
                } else if (item.getItemId() == R.id.sort_by_deadline) {
                    showCompleted = false;
                    deadlineFired = !deadlineFired;
                    tasksViewModel.getSortedTasks("deadlineDate", deadlineFired);
                } else if (item.getItemId() == R.id.start_with_completed) {
                    showCompleted = true;
                    tasksViewModel.getSortedTasks("isDone", false);
                } else if (item.getItemId() == R.id.stat_with_not_completed) {
                    showCompleted = true;
                    tasksViewModel.getSortedTasks("isDone", true);
                } else if (item.getItemId() == R.id.show_completed) {
                    showCompleted = true;
                    editor.putBoolean("isHiddenCompleted", true);
                    editor.apply();
                    tasksViewModel.setShowCompletedTasks(true);
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void emptyTaskList() {
        binding.progress.setVisibility(View.GONE);
        binding.tvEmptyList.setText(getString(R.string.empty_task_list));
        binding.rvTasks.setVisibility(View.GONE);
        binding.tvEmptyList.setVisibility(View.VISIBLE);
    }

    @Override
    public void taskDeleteBtnClicked(View itemView, String taskName, int id, int position) {

        deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                itemView.animate()
                        .setDuration(250)
                        .translationX(-100)
                        .alpha(0)
                        .withEndAction(() -> {
                            itemView.setVisibility(View.GONE);
                            tasksViewModel.deleteTask(id, position);
                        }).start();
            }

            @Override
            public void onNegativeButtonClicked() {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setAlertTitle(getString(R.string.task_delete_dialog_title));
        deleteDialog.setAlertMessage(getString(R.string.task_delete_dialog_desc) + " " + taskName + "?");
        deleteDialog.setVisibilityPositiveBtn(true);
        deleteDialog.setVisibilityNegativeBtn(true);
        deleteDialog.show(requireActivity().getSupportFragmentManager(),
                DeleteConfirmDialog.class.toString());
    }

    @Override
    public void taskEditBtnClicked(int id, int position) {
        tasksViewModel.setCurrentTask(id);
        TaskAddDialog addTaskDialog = new TaskAddDialog(tasksViewModel, position);
        addTaskDialog.setIsUpdateTrue();
        addTaskDialog.show(getParentFragmentManager(), TaskAddDialog.class.toString());
    }

    @Override
    public void taskRepeatBtnClicked(int id, int position) {
        tasksViewModel.setCurrentTask(id);
        TaskAddDialog addTaskDialog = new TaskAddDialog(tasksViewModel, position);
        addTaskDialog.setIsRestoreTrue();
        addTaskDialog.show(getParentFragmentManager(), TaskAddDialog.class.toString());
    }

    @Override
    public void checkBoxChecked(int id, int position) {
        tasksViewModel.updateIsDoneField(id, true, position);
    }
}