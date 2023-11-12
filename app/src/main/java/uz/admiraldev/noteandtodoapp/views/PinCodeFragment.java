package uz.admiraldev.noteandtodoapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.R;
import uz.admiraldev.noteandtodoapp.databinding.FragmentPinCodeBinding;

public class PinCodeFragment extends Fragment {
    FragmentPinCodeBinding binding;
    NavController navController;
    private int enteredNumberCount = 0;
    Executor myExecutor;
    private String enteredCode = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String savedLogin;

    public PinCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPinCodeBinding.inflate(inflater, container, false);
        sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        navController = NavHostFragment.findNavController(this);
        if (sharedPreferences.getString("login", "").isEmpty()) {
            navController.navigate(R.id.signInFragment);
        } else
            savedLogin = sharedPreferences.getString("login", "");
        myExecutor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogin.setOnClickListener(exitView -> {
            editor.putBoolean("isEnterWithPinCode", false);
            editor.apply();
            navController.navigate(R.id.action_pinCodeFragment_to_signInFragment);
        });
        binding.btnExit.setOnClickListener(exitView -> {
            if (getActivity() != null)
                getActivity().finish();
        });
        binding.btn1.setOnClickListener(btn1View -> {
            enteredNumberCount++;
            enteredCode += "1";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn2.setOnClickListener(btn2View -> {
            enteredNumberCount++;
            enteredCode += "2";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn3.setOnClickListener(btn3View -> {
            enteredNumberCount++;
            enteredCode += "3";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn4.setOnClickListener(btn4View -> {
            enteredNumberCount++;
            enteredCode += "4";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn5.setOnClickListener(btn5View -> {
            enteredNumberCount++;
            enteredCode += "5";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn6.setOnClickListener(btn6View -> {
            enteredNumberCount++;
            enteredCode += "6";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn7.setOnClickListener(btn7View -> {
            enteredNumberCount++;
            enteredCode += "7";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn8.setOnClickListener(btn8View -> {
            enteredNumberCount++;
            enteredCode += "8";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn9.setOnClickListener(btn9View -> {
            enteredNumberCount++;
            enteredCode += "9";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btn0.setOnClickListener(btn0View -> {
            enteredNumberCount++;
            enteredCode += "0";
            if (enteredNumberCount == 4) {
                checkCode(enteredCode);
            } else updateCircles(enteredNumberCount);
        });
        binding.btnClear.setOnClickListener(btnClearView -> {
            if (enteredNumberCount > 0) {
                enteredNumberCount--;
                enteredCode = enteredCode.substring(0, enteredNumberCount);
                updateCircles(enteredNumberCount);
            }
        });
    }

    private void checkCode(String enteredCode) {
        myExecutor.execute(() -> {
            boolean isCorrectPin = MainActivity.getUserDatabase().userDao().isLoggedPin(savedLogin, enteredCode);
            requireActivity().runOnUiThread(() -> {
                if (isCorrectPin) {
                    navController.navigate(R.id.navigation_notes);
                } else {
                    Toast.makeText(requireContext(), "Incorrect PIN code:" + enteredCode, Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    this.enteredCode = "";
                    updateCircles(-1);
                }
            });
        });
    }

    private void updateCircles(int count) {
        switch (count) {
            case 0: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode2.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode3.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode4.setImageResource(R.drawable.ic_circle);
                break;
            }
            case 1: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode2.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode3.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode4.setImageResource(R.drawable.ic_circle);
                break;
            }
            case 2: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode2.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode3.setImageResource(R.drawable.ic_circle);
                binding.ivPinCode4.setImageResource(R.drawable.ic_circle);
                break;
            }
            case 3: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode2.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode3.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode4.setImageResource(R.drawable.ic_circle);
                break;
            }
            case 4: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode2.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode3.setImageResource(R.drawable.ic_circle_filled);
                binding.ivPinCode4.setImageResource(R.drawable.ic_circle_filled);
                break;
            }
            case -1: {
                binding.ivPinCode1.setImageResource(R.drawable.ic_error_circle_filled);
                binding.ivPinCode2.setImageResource(R.drawable.ic_error_circle_filled);
                binding.ivPinCode3.setImageResource(R.drawable.ic_error_circle_filled);
                binding.ivPinCode4.setImageResource(R.drawable.ic_error_circle_filled);
                binding.layoutConstraint.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bounce));
                enteredNumberCount = 0;
                new Handler().postDelayed(() -> updateCircles(enteredNumberCount), 700);
                break;
            }
        }
    }
}