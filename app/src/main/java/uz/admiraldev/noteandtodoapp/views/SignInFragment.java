package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentSignInBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.UsersViewModel;

public class SignInFragment extends Fragment {
    FragmentSignInBinding binding;
    UsersViewModel usersViewModel;
    Executor myExecutor = Executors.newSingleThreadExecutor();
    NavController navController;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isRememberLogin = false;
    EditText etLogin, etPassword;
    String enteredLogin, enteredPassword;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        navController = NavHostFragment.findNavController(this);
        if (sharedPreferences.getBoolean("isEnterWithPinCode", false))
            navController.navigate(R.id.action_signInFragment_to_pinCodeFragment);
        if (sharedPreferences.getBoolean("isRememberedUser", false)) {
            navController.navigate(R.id.action_signInFragment_to_noteFragment);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(getLayoutInflater(), container, false);
        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        View bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null && bottomAppBar.getVisibility() == View.VISIBLE) {
            bottomAppBar.setVisibility(View.GONE);
        }

        View fab = requireActivity().findViewById(R.id.floatingActionButton);
        if (fab != null && fab.getVisibility() == View.VISIBLE) {
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etLogin = binding.etLogin;
        etPassword = binding.etPassword;
        editor = sharedPreferences.edit();

        // add user button Clicked
        binding.tvAddUser.setOnClickListener(view1 ->
                navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        );

        // Enter button Clicked
        binding.btnEnter.setOnClickListener(view1 -> {
            if (etLogin.getText() != null && etPassword.getText() != null &&
                    !etLogin.getText().toString().trim().isEmpty() &&
                    !etPassword.getText().toString().trim().isEmpty()) {
                enteredLogin = etLogin.getText().toString().trim();
                enteredPassword = etPassword.getText().toString().trim();
                try {
                    myExecutor.execute(() -> {
                        boolean isLogged = MainActivity.getAppDataBase().userDao().isLogged(enteredLogin, enteredPassword);
                        requireActivity().runOnUiThread(() -> {
                            if (isLogged) {
                                editor.putString("login", enteredLogin);
                                if (isRememberLogin) {
                                    editor.putString("pwd", enteredPassword);
                                    editor.putBoolean("isRememberedUser", true);
                                }
                                editor.apply();
                                navController.navigate(R.id.action_signInFragment_to_noteFragment);
                            } else {
                                binding.etLogin.setText(null);
                                binding.etLogin.requestFocus();
                                Toast.makeText(requireContext(),
                                        requireContext().getString(R.string.incorrect_logn),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } catch (Exception e) {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.db_error), Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(requireContext(), requireContext().getString(R.string.empty_login),
                        Toast.LENGTH_SHORT).show();
        });
        binding.chbRemember.setOnCheckedChangeListener((compoundButton, isChecked) ->
                isRememberLogin = isChecked
        );
        binding.ivPinCode.setOnClickListener(view1 -> {
            if (sharedPreferences.getBoolean("isEnterWithPinCode", false)) {
                navController.navigate(R.id.action_signInFragment_to_pinCodeFragment);
            } else {
                Toast.makeText(requireContext(), getText(R.string.not_saved_user), Toast.LENGTH_SHORT).show();
            }
        });
    }
}