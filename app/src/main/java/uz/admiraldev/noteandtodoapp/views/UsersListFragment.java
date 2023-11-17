package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.adapters.UsersAdapter;
import uz.admiraldev.noteandtodoapp.databinding.FragmentUsersListBinding;
import uz.admiraldev.noteandtodoapp.models.User;
import uz.admiraldev.noteandtodoapp.viewmodels.UsersViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;

public class UsersListFragment extends Fragment implements UsersAdapter.UserItemButtonClick {
    private UsersViewModel usersViewModel;
    private FragmentUsersListBinding binding;
    private NavController navController;
    DeleteConfirmDialog deleteDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String savedLogin;

    public UsersListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersListBinding.inflate(getLayoutInflater(), container, false);
        navController = NavHostFragment.findNavController(this);
        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        savedLogin = sharedPreferences.getString("login", "");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usersViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), usersList -> {
            binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvUsers.setAdapter(new UsersAdapter(usersList, this));
        });
        binding.btnBack.setOnClickListener(usersListView ->
                navController.navigate(R.id.action_usersListFragment_to_navigation_settings));
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                        navController.navigate(R.id.navigation_settings);
                    }
                });
    }

    @Override
    public void onDeleteBtnClicked(User user) {
        deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                if (user.getUsername().equals(savedLogin)) {
                    editor.putString("login", null);
                    editor.apply();
                }
                usersViewModel.deleteUsers(user);
                usersViewModel.getUsersLiveData().observe(getViewLifecycleOwner(), usersList -> {
                    binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.rvUsers.setAdapter(new UsersAdapter(usersList, UsersListFragment.this));
                });
            }

            @Override
            public void onNegativeButtonClicked() {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setAlertTitle(getString(R.string.user_delete_dialog_title));
        deleteDialog.setAlertMessage(getString(R.string.user_delete_dialog_desc) + " " + user.getUsername() + "?");
        deleteDialog.setVisibilityPositiveBtn(true);
        deleteDialog.setVisibilityNegativeBtn(true);
        deleteDialog.show(requireActivity().getSupportFragmentManager(),
                DeleteConfirmDialog.class.toString());
    }

    @Override
    public void onEditBtnClicked(int position, User user) {
        usersViewModel.setEditUserData(user);
        SignUpFragment.setActionEdit();
        navController.navigate(R.id.signUpFragment);
    }
}