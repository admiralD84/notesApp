package uz.admiraldev.noteandtodoapp.views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentSignUpBinding;
import uz.admiraldev.noteandtodoapp.models.User;
import uz.admiraldev.noteandtodoapp.viewmodels.UsersViewModel;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private EditText etLogin, etPassword, etPinCode;
    private UsersViewModel usersViewModel;
    private Drawable[] drawables;
    private AppCompatTextView addPinCode;
    private static boolean isActionEdit = false;
    private boolean isCancel = false;
    private boolean isLoginAlreadyHave;
    private User user;
    private String login, pwd, pinCode;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize
        NavController navController = NavHostFragment.findNavController(this);
        usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        addPinCode = binding.tvAddPinCode;
        etLogin = binding.etLogin;
        etPassword = binding.etPassword;
        etPinCode = binding.etPinCode;
        if (isActionEdit) {
            User user = usersViewModel.getEditUserData();
            binding.tvAddPinCode.setVisibility(View.GONE);
            binding.layoutPinCode.setVisibility(View.VISIBLE);
            binding.etLogin.setText(user.getUsername());
            binding.etPassword.setText(user.getPassword());
            if (!user.getPinCode().equals("-1"))
                binding.etPinCode.setText(user.getPinCode());
        }


        // initialize
        binding.btnBack.setOnClickListener(view1 -> {
            if (isActionEdit) {
                navController.popBackStack(R.id.signUpFragment, true);
                navController.navigate(R.id.usersListFragment);
            } else {
                navController.popBackStack(R.id.signUpFragment, true);
                navController.navigate(R.id.signInFragment);
            }
        });

        binding.btnSave.setOnClickListener(userSaveView -> {
            if (etLogin.getText() != null && etPassword.getText() != null) {
                login = etLogin.getText().toString().trim();
                pwd = etPassword.getText().toString().trim();
                if (login.isEmpty()) {
                    etLogin.setText(null);
                    etLogin.requestFocus();
                    Toast.makeText(requireContext(),
                            requireContext().getString(R.string.login_not_entered),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (pwd.isEmpty()) {
                        etPassword.setText(null);
                        etPassword.requestFocus();
                        Toast.makeText(requireContext(),
                                requireContext().getString(R.string.pwd_not_entered),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (etPinCode.getText() != null) {
                            pinCode = etPinCode.getText().toString().trim();
                            if (!pinCode.isEmpty())
                                user = new User(login, pwd, pinCode);
                            else
                                user = new User(login, pwd, "-1");
                            if (isLoginAlreadyHave) {
                                Toast.makeText(requireContext(),
                                        requireContext().getString(R.string.login_busy),
                                        Toast.LENGTH_SHORT).show();
                                etLogin.requestFocus();
                            } else {
                                if (isActionEdit) {
                                    User editedUser = usersViewModel.getEditUserData();
                                    if (!pinCode.isEmpty()) {
                                        editedUser.setUsername(login);
                                        editedUser.setPassword(pwd);
                                        editedUser.setPinCode(pinCode);
                                    } else {
                                        editedUser.setUsername(login);
                                        editedUser.setPassword(pwd);
                                    }
                                    usersViewModel.updateUser(editedUser);
                                    navController.navigate(R.id.navigation_settings);
                                } else {
                                    usersViewModel.saveUser(user);
                                    navController.navigate(R.id.action_signUpFragment_to_afterSignUpFragment);
                                }
                            }
                        } else
                            Toast.makeText(requireContext(),
                                    requireContext().getString(R.string.data_not_entered),
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            } else
                Toast.makeText(requireContext(),
                        requireContext().getString(R.string.data_not_entered),
                        Toast.LENGTH_SHORT).show();

        });

        addPinCode.setOnClickListener(view1 -> {
            if (isCancel) {
                isCancel = false;
                binding.etPinCode.setText(null);
                binding.layoutPinCode.animate()
                        .setDuration(300)
                        .translationY(100)
                        .alpha(0)
                        .withEndAction(() -> binding.layoutPinCode.setVisibility(View.GONE)).start();
                addPinCode.setText(R.string.add_pin_code_text);
                drawables = addPinCode.getCompoundDrawables();
                drawables[1] = ContextCompat.getDrawable(requireContext(), R.drawable.ic_expend_more);
                addPinCode.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, drawables[1], null);
            } else {
                isCancel = true;
                binding.layoutPinCode.setTranslationY(100);
                binding.layoutPinCode.setAlpha(0f);
                binding.layoutPinCode.setVisibility(View.VISIBLE);
                binding.layoutPinCode.animate().setDuration(500)
                        .translationY(0)
                        .alpha(1f)
                        .start();
                addPinCode.setText(R.string.cancel_text);
                drawables = addPinCode.getCompoundDrawables();
                drawables[1] = ContextCompat.getDrawable(requireContext(), R.drawable.ic_expend_less);
                addPinCode.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, drawables[1], null);
            }
        });
        etLogin.setOnFocusChangeListener((etLoginView, isFocusChanged) -> {
            login = etLogin.getText().toString();
            if (!etLoginView.hasFocus()) {
                if (etLogin.getText() != null)
                    usersViewModel.checkLogin(login).observe(getViewLifecycleOwner(),
                            isLoginSaved -> isLoginAlreadyHave = isLoginSaved);
            }
        });

    }

    public static void setActionEdit() {
        isActionEdit = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActionEdit = false;
    }
}