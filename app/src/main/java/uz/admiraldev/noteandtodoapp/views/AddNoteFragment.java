package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentAddNoteBinding;
import uz.admiraldev.noteandtodoapp.models.Note;
import uz.admiraldev.noteandtodoapp.viewmodels.NotesViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;

public class AddNoteFragment extends Fragment {
    FragmentAddNoteBinding binding;
    NavController navController;
    int priority = 3;
    String noteTitle, noteDescription;
    NotesViewModel viewModel;
    DeleteConfirmDialog deleteDialog;
    private Boolean isReadOnly = false;
    private Boolean isUpdate = false;


    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().findViewById(R.id.bottomAppBar).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNoteBinding.inflate(getLayoutInflater(), container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        navController = NavHostFragment.findNavController(this);
        if (viewModel.getSelectedNoteId() != -1)
            isReadOnly = true;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isReadOnly) {
            viewModel.getNoteLiveData().observe(getViewLifecycleOwner(), note -> {
                if (note != null) {
                    readNoteView(note);
                    viewModel.getIsUpdateNote().observe(getViewLifecycleOwner(), isUpdateNote -> {
                        if (isUpdateNote) {
                            setNote(note);
                            binding.ivDelete.setVisibility(View.GONE);
                            binding.btnSave.setVisibility(View.VISIBLE);
                            binding.ivEdit.setVisibility(View.GONE);
                            binding.rgPriority.setVisibility(View.VISIBLE);
                            binding.tvPriority.setVisibility(View.VISIBLE);
                            binding.readLayout.setVisibility(View.GONE);
                            binding.editLayout.setVisibility(View.VISIBLE);
                            isUpdate = true;
                        }
                    });
                }
            });
        } else {

            binding.etTitle.requestFocus();
            // focus o'rnatilganidan keyin klaviaturani ochib beradi
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etTitle, InputMethodManager.SHOW_IMPLICIT);
        }
        initClicks();
    }

    public void initClicks() {

        binding.btnBack.setOnClickListener(btnBackView ->
                navController.navigate(R.id.action_addNoteFragment_to_navigation_notes));

        binding.btnSave.setOnClickListener(view1 -> {
            noteTitle = Objects.requireNonNull(binding.etTitle.getText()).toString();
            noteDescription = Objects.requireNonNull(binding.etSubtitle.getText()).toString();

            if (!noteTitle.isEmpty() && !noteDescription.isEmpty()) {
                if (isUpdate)
                    viewModel.updateNote(noteTitle, noteDescription, priority);
                else
                    viewModel.saveData(noteTitle, noteDescription, priority);
                navController.navigate(R.id.action_addNoteFragment_to_navigation_notes);
            } else {
                Toast.makeText(requireContext(), getString(R.string.data_not_entered), Toast.LENGTH_SHORT).show();
                binding.etTitle.requestFocus();
            }
        });

        binding.ivEdit.setOnClickListener(view1 -> viewModel.setIsUpdateNote(true));

        // delete btn clicked
        binding.ivDelete.setOnClickListener(view -> {
            noteTitle = Objects.requireNonNull(binding.etTitle.getText()).toString();
            deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
                @Override
                public void onPositiveButtonClicked() {
                    viewModel.deleteNote();
                    navController.popBackStack(R.id.addNoteFragment, false);
                    navController.navigate(R.id.action_addNoteFragment_to_navigation_notes);
                }

                @Override
                public void onNegativeButtonClicked() {
                    deleteDialog.dismiss();
                }
            });
            String dialogTitle = getString(R.string.note_delete_dialog_title);
            String dialogDesc = getString(R.string.note_delete_dialog_desc);
            deleteDialog.setAlertTitle(dialogTitle);
            deleteDialog.setAlertMessage(dialogDesc + "\n\"" + noteTitle + "\"?");
            deleteDialog.setVisibilityPositiveBtn(true);
            deleteDialog.setVisibilityNegativeBtn(true);
            deleteDialog.show(requireActivity().getSupportFragmentManager(),
                    DeleteConfirmDialog.class.toString());
        });

        binding.rgPriority.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.rb_high)
                priority = 1;
            else if (checkedId == R.id.rb_medium)
                priority = 2;
            else if (checkedId == R.id.rb_low)
                priority = 3;
        });
    }

    private void setNote(Note note) {
        binding.etTitle.setText(note.getNotesTitle());
        binding.etSubtitle.setText(note.getNotesSubtitle());
    }

    private void readNoteView(Note note) {
        binding.readTitle.setText(note.getNotesTitle());
        binding.readDesc.setText(note.getNotesSubtitle());
        binding.btnSave.setVisibility(View.GONE);
        binding.ivDelete.setVisibility(View.VISIBLE);
        binding.ivEdit.setVisibility(View.VISIBLE);

        binding.tvPriority.setVisibility(View.GONE);
        binding.rgPriority.setVisibility(View.GONE);
        binding.editLayout.setVisibility(View.GONE);
        binding.readLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.setIsUpdateNote(false);
        viewModel.setSelectedNoteId(-1);
    }
}