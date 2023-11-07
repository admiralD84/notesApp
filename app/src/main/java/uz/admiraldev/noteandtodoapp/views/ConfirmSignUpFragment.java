package uz.admiraldev.noteandtodoapp.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentAfterSignUpBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.UsersViewModel;

public class ConfirmSignUpFragment extends Fragment {
    FragmentAfterSignUpBinding binding;
    UsersViewModel usersViewModel;

    public ConfirmSignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAfterSignUpBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        if (getActivity() != null) {
            usersViewModel = new ViewModelProvider(getActivity()).get(UsersViewModel.class);
            usersViewModel.getUser().observe(getActivity(), savedUser -> {
                if (savedUser != null) {
                    String tempText = "'" + savedUser.getUsername() + "' " +
                            getResources().getString(R.string.add_user_done) +
                            "\n\n" +
                            "Login: " + savedUser.getUsername() +
                            "\nPassword: " + savedUser.getPassword() +
                            "\n\n" + getResources().getString(R.string.add_user_done2);
                    binding.tvDone.setText(tempText);
                }
            });
        }
        binding.btnStart.setOnClickListener(view1 ->
            navController.navigate(R.id.action_afterSignUpFragment_to_signInFragment));
    }

}