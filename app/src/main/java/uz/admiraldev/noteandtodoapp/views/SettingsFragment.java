package uz.admiraldev.noteandtodoapp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentSettingsBinding;
import uz.admiraldev.noteandtodoapp.viewmodels.UsersViewModel;
import uz.admiraldev.noteandtodoapp.views.dialogs.AboutAppBottomSheet;
import uz.admiraldev.noteandtodoapp.views.dialogs.DeleteConfirmDialog;

public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NavController navController;
    private final String[] languages = {"🇺🇿 Uz", "🇬🇧 En", "🇷🇺 Ru"};
    boolean userPinCodeHave = false;
    static Boolean isTouched = false;
    DeleteConfirmDialog deleteDialog;
    private Vibrator vibrator;
    String savedLogin;
    String oldLanguage;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = binding.bottomNavigationViewSettings;
        navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        binding.bottomAppBarSettings.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_languages_drop_down, languages);
        binding.tvSelectedLanguage.setAdapter(adapter);
        binding.tvSelectedLanguage.setSelected(true);
        switch (oldLanguage) {
            case "uz":
                binding.tvSelectedLanguage.setText(languages[0], false);
                break;
            case "ru":
                binding.tvSelectedLanguage.setText(languages[2], false);
                break;
            default:
                binding.tvSelectedLanguage.setText(languages[1], false);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);
        requireActivity().findViewById(R.id.bottomAppBar).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
        UsersViewModel usersViewModel = new ViewModelProvider(requireActivity()).get(UsersViewModel.class);
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        oldLanguage = sharedPreferences.getString("appLanguage", Locale.getDefault().getLanguage());
        if (!sharedPreferences.getString("login", "").isEmpty()) {
            savedLogin = sharedPreferences.getString("login", "");
        }
        usersViewModel.checkUserPIN(savedLogin);
        usersViewModel.getIsPinCodeEnteredLiveData()
                .observe(getViewLifecycleOwner(), isPinCodeEntered -> userPinCodeHave = !isPinCodeEntered);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editor = sharedPreferences.edit();
        binding.switchPinCode.setChecked(sharedPreferences.getBoolean("isEnterWithPinCode", false));
        binding.tvAbout.setOnClickListener(aboutAppview -> {
            AboutAppBottomSheet bottomSheetFragment = new AboutAppBottomSheet();
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });
        binding.tvUsers.setOnClickListener(usersListView ->
                navController.navigate(R.id.action_navigation_settings_to_usersListFragment));

        binding.tvDeleteLogin.setOnClickListener(view1 -> {
            String tempLogin = sharedPreferences.getString("login", "");
            if (!tempLogin.isEmpty()) {
                deleteDialog = new DeleteConfirmDialog(new DeleteConfirmDialog.ClickListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        editor.putString("login", null);
                        editor.putString("pwd", null);
                        editor.putBoolean("isRememberedUser", false);
                        editor.apply();
                    }

                    @Override
                    public void onNegativeButtonClicked() {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.setAlertTitle(getString(R.string.login_delete_dialog_title));
                deleteDialog.setAlertMessage(getString(R.string.login_delete_dialog_desc));
                deleteDialog.setVisibilityPositiveBtn(true);
                deleteDialog.setVisibilityNegativeBtn(true);
                deleteDialog.show(requireActivity().getSupportFragmentManager(),
                        DeleteConfirmDialog.class.toString());
            } else
                Toast.makeText(requireContext(), getText(R.string.not_saved_user), Toast.LENGTH_SHORT).show();
        });

        binding.switchPinCode.setOnTouchListener((pinSwitch, motionEvent) -> {
            isTouched = true;
            return false;
        });

        binding.switchPinCode.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (isTouched) {
                isTouched = false;
                if (checked) {
                    if (userPinCodeHave) {
                        editor.putBoolean("isEnterWithPinCode", true);
                        editor.putBoolean("isRememberedUser", false);
                        editor.apply();
                        Toast.makeText(requireContext(),
                                requireContext().getString(R.string.enter_with_pin),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                        binding.switchPinCode.setChecked(false);
                        Toast.makeText(requireContext(),
                                requireContext().getString(R.string.empty_pin),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editor.putBoolean("isEnterWithPinCode", false);
                    editor.apply();
                    Toast.makeText(requireContext(),
                            requireContext().getString(R.string.enter_with_pin_off),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvSelectedLanguage.setOnItemClickListener((parent, arg1, position, id) -> {
            String selectedLanguage = parent.getItemAtPosition(position).toString();
            selectedLanguage = selectedLanguage.substring(selectedLanguage.length() - 2).toLowerCase();
            if (!oldLanguage.equals(selectedLanguage)) {
                editor.putString("appLanguage", selectedLanguage);
                editor.apply();
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selectedLanguage));
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack(R.id.navigation_notes, true);
                        navController.navigate(R.id.navigation_notes);
                    }
                });
    }
}