package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private FragmentNoteBinding binding;
    private NotesViewModel viewModel;
    private NotesAdapter notesAdapter;
    private DeleteConfirmDialog deleteDialog;
    private NavController navController;
    boolean isSelectAction;
    private InputMethodManager inputMethodManager;
    boolean isSearchAction = false;

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
        viewModel.getNotesFromDB();
        viewModel.notesLiveData.observe(getViewLifecycleOwner(), notes -> {
            if (notes != null && !notes.isEmpty()) {
                binding.progress.setVisibility(View.GONE);
                binding.layoutIcon.setVisibility(View.VISIBLE);
                setNotes(notes);
            } else
                setEmptyNotes();
        });
        inputMethodManager = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {

        binding.ivEdit.setOnClickListener(editView -> {
            viewModel.setEditMode(true);
            isSelectAction = false;
            navController.popBackStack(R.id.signUpFragment, false);
            navController.navigate(R.id.action_navigation_notes_to_addNoteFragment);
        });

        binding.ivDelete.setOnClickListener(view -> {
            deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
                @Override
                public void onPositiveButtonClicked() {
                    viewModel.deleteNote();
                    listenerSelectionChanged();
                }

                @Override
                public void onNegativeButtonClicked() {
                    viewModel.clearSelectedNotesList();
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
            if (isSearchAction) {
                isSearchAction = false;
                inputMethodManager.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
                binding.ivFilter.setImageResource(R.drawable.ic_filter);
                binding.etSearch.setText("");
                binding.tilSearch.animate()
                        .setDuration(250)
                        .translationX(-100)
                        .alpha(0)
                        .withEndAction(() -> {
                            binding.tilSearch.setVisibility(View.GONE);
                            binding.tvNotePageTitle.setVisibility(View.VISIBLE);
                        }).start();
                viewModel.getNotesFromDB();
                binding.rvNotes.requestFocus();
            } else {
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
            }
        });
        binding.ivSearch.setOnClickListener(btnSearch -> {
            if (!isSearchAction) {
                isSearchAction = true;
                binding.tvNotePageTitle.setVisibility(View.GONE);
                binding.ivFilter.setImageResource(R.drawable.ic_close);
                binding.ivFilter.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue2)));
                binding.tilSearch.setTranslationX(80);
                binding.tilSearch.setAlpha(0.2f);
                binding.tilSearch.setVisibility(View.VISIBLE);
                binding.tilSearch.animate().setDuration(250)
                        .translationX(0)
                        .alpha(1f)
                        .start();
                binding.etSearch.requestFocus();
                inputMethodManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);
            } else {
                String searchNoteText = binding.etSearch.getText().toString().trim();
                if (!searchNoteText.isEmpty())
                    viewModel.searchNotes(searchNoteText);
            }
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
        viewModel.selectedNotesList.observe(getViewLifecycleOwner(), notes -> {
            if (notes.size() == 0) {
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
                if (notes.size() == 1) {
                    binding.ivEdit.setVisibility(View.VISIBLE);
                } else {
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
            viewModel.setSelectedNote(position);
            viewModel.setReadOnlyMode(true);
            navController.popBackStack(R.id.signUpFragment, false);
            navController.navigate(R.id.action_navigation_notes_to_addNoteFragment);
        }
        listenerSelectionChanged();
    }

    @Override
    public void onItemLongClicked(int position, int id) {
        isSelectAction = true;
        viewModel.addSelectedNoteId(id);
        viewModel.setSelectedNote(position);
        listenerSelectionChanged();
    }
}