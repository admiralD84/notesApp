package uz.admiraldev.noteandtodoapp.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;
import java.util.List;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.adapters.NotesAdapter;
import uz.admiraldev.noteandtodoapp.databinding.FragmentNoteBinding;
import uz.admiraldev.noteandtodoapp.models.Note;
import uz.admiraldev.noteandtodoapp.viewmodels.NotesViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;

public class NoteFragment extends Fragment implements NotesAdapter.OnNoteItemClickListener {
    FragmentNoteBinding binding;
    NotesViewModel viewModel;
    NotesAdapter notesAdapter;
    DeleteConfirmDialog deleteDialog;
    NavController navController;
    boolean isSelectAction;

    public NoteFragment() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(getLayoutInflater(), container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        navController = NavHostFragment.findNavController(this);
        loadData();
        initClicks();
        return binding.getRoot();
    }

    private void loadData() {
        viewModel.getNotesLiveData().observe(getViewLifecycleOwner(), notes -> {
            // viewModel orqali qaydlarni bazadan o'qib olish
            if (notes != null && !notes.isEmpty()) {
                binding.progress.setVisibility(View.GONE);
                setNotes(notes);
            } else
                setEmptyNotes();
        });
    }

    private void initClicks() {

        binding.ivEdit.setOnClickListener(editView -> {
            viewModel.setIsUpdateNote(true);
            isSelectAction = false;
            navController.popBackStack(R.id.signUpFragment, false);
            navController.navigate(R.id.action_navigation_notes_to_addNoteFragment);
        });

        binding.ivDelete.setOnClickListener(view -> {
            deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
                @Override
                public void onPositiveButtonClicked() {
                    viewModel.deleteNote();
                    loadData();
                    viewModel.getSelectedNotesList().getValue().clear();
                    listenerSelectionChanged();
                }

                @Override
                public void onNegativeButtonClicked() {
                    viewModel.getSelectedNotesList().getValue().clear();
                    deleteDialog.dismiss();
                    listenerSelectionChanged();
                }
            });
            deleteDialog.setAlertTitle(getString(R.string.note_delete_dialog_title));
            deleteDialog.setAlertMessage(getString(R.string.note_delete_dialog_desc) + "?");
            deleteDialog.setVisibilityPositiveBtn(true);
            deleteDialog.setVisibilityNegativeBtn(true);
            deleteDialog.show(requireActivity().getSupportFragmentManager(),
                    DeleteConfirmDialog.class.toString());
        });

        binding.ivFilter.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.inflate(R.menu.note_filter_popup_menu);
            try {
                Method method = popupMenu.getMenu().getClass()
                        .getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popupMenu.getMenu(), true);
            } catch (Exception e) {
                Log.d("myTag", "note popup menu error" + e.getMessage());
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.show_new_first) {
                    viewModel.getSortedNoteList("addedDate", false);
                } else if (item.getItemId() == R.id.show_old_first) {
                    viewModel.getSortedNoteList("addedDate", true);
                } else if (item.getItemId() == R.id.sort_high_priority_first) {
                    viewModel.getSortedNoteList("priority", true);
                } else if (item.getItemId() == R.id.sort_low_priority_first) {
                    viewModel.getSortedNoteList("priority", false);
                }
                return false;
            });
            popupMenu.show();
        });
    }

    public void setEmptyNotes() {
        binding.progress.setVisibility(View.GONE);
        binding.tvEmptyList.setText(getString(R.string.empty_note_list));
        binding.rvNotes.setVisibility(View.GONE);
        binding.tvEmptyList.setVisibility(View.VISIBLE);
    }

    public void setNotes(List<Note> notesList) {
        notesAdapter = new NotesAdapter(notesList, this, requireContext(), viewModel);
        binding.rvNotes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvNotes.setAdapter(notesAdapter);
        binding.rvNotes.setVisibility(View.VISIBLE);
        if (binding.tvEmptyList.getVisibility() == View.VISIBLE)
            binding.tvEmptyList.setVisibility(View.GONE);
    }

    private void listenerSelectionChanged() {
        viewModel.getSelectedNotesList().observe(getViewLifecycleOwner(), selectedNotes -> {
            if (selectedNotes.size() == 0) {
                binding.ivDelete.setVisibility(View.GONE);
                binding.ivEdit.setVisibility(View.GONE);
                binding.ivSearch.setVisibility(View.VISIBLE);
                binding.ivFilter.setVisibility(View.VISIBLE);
                isSelectAction = false;
                notesAdapter.notifyDataSetChanged();
            } else {
                binding.ivFilter.setVisibility(View.GONE);
                binding.ivSearch.setVisibility(View.GONE);
                binding.ivDelete.setVisibility(View.VISIBLE);
                if (selectedNotes.size() == 1) {
                    binding.ivEdit.setVisibility(View.VISIBLE);
                    viewModel.setSelectedNoteId(selectedNotes.get(0));
                } else {
                    viewModel.setSelectedNoteId(-1);
                    binding.ivEdit.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, int id) {
        if (isSelectAction) {
            viewModel.addSelectedNoteId(id);
        } else {
            viewModel.setSelectedNoteId(id);
            navController.popBackStack(R.id.signUpFragment, false);
            navController.navigate(R.id.action_navigation_notes_to_addNoteFragment);
        }
        listenerSelectionChanged();
    }

    @Override
    public void onItemLongClicked(int position, int id) {
        isSelectAction = true;
        viewModel.addSelectedNoteId(id);
        listenerSelectionChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.getSelectedNotesList().getValue().clear();
    }
}