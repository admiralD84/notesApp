package uz.admiraldev.noteandtodoapp.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.admiraldev.noteandtodoapp.MainActivity;
import uz.admiraldev.noteandtodoapp.models.User;

public class UsersViewModel extends ViewModel {

    private List<User> users = new ArrayList<>();
    private final ExecutorService myExecutor = Executors.newSingleThreadExecutor();
    private String login;
    private User editUser;
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginHave = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPinCodeEnteredLiveData = new MutableLiveData<>();

    public void deleteUsers(User user) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getUserDatabase().userDao().deleteUser(user);
            } catch (Exception e) {
                Log.d("myTag", "msg: " + e.getMessage());
            }
        });
    }

    public User getEditUserData() {
        return editUser;
    }

    public LiveData<Boolean> getIsPinCodeEnteredLiveData() {
        return isPinCodeEnteredLiveData;
    }

    public void checkUserPIN(String login) {
        myExecutor.execute(() -> {
            try {
                boolean isPinCodeEntered = MainActivity.getUserDatabase().userDao().notEmptyPinCode(login, "-1");
                isPinCodeEnteredLiveData.postValue(isPinCodeEntered);
            } catch (Exception e) {
                Log.d("myTag", "checkUserPIN error ->" + e.getMessage());
            }
        });
    }

    public void setEditUserData(User editUser) {
        this.editUser = editUser;
    }

    public LiveData<List<User>> getUsersLiveData() {
        myExecutor.execute(() -> {
            try {
                users = MainActivity.getUserDatabase().userDao().getUsers();
                usersLiveData.postValue(users);
            } catch (Exception e) {
                Log.d("myTag", "msg: " + e.getMessage());
            }
        });
        return usersLiveData;
    }

    public void saveUser(User user) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getUserDatabase().userDao().insertUser(user);
            } catch (Exception e) {
                Log.d("myTag", "save user error: " + e.getMessage());
            }
        });
        login = user.getUsername();
    }

    public LiveData<User> getUser() {
        myExecutor.execute(() -> {
            try {
                User tempUser = MainActivity.getUserDatabase().userDao().getUserByUsername(login);
                userLiveData.postValue(tempUser);
            } catch (Exception e) {
                Log.d("myTag", "User get error: " + e.getMessage());
            }
        });
        return userLiveData;
    }

    public void updateUser(User newUser) {
        myExecutor.execute(() -> {
            try {
                MainActivity.getUserDatabase().userDao().updateUser(newUser);
            } catch (Exception e) {
                Log.d("myTag", "user update error: " + e.getMessage());
            }
        });
    }

    public LiveData<Boolean> checkLogin(String login) {
        myExecutor.execute(() -> {
            boolean result;
            try {
                result = MainActivity.getUserDatabase().userDao().isTakenLogin(login);
                loginHave.postValue(result);
//                new Handler(Looper.getMainLooper()).post(() -> loginHave.setValue(result));
            } catch (Exception e) {
                Log.d("myTag", "check login error: " + e.getMessage());
            }
        });
        return loginHave;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        myExecutor.shutdown();
    }
}
