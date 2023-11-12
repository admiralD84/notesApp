package uz.admiraldev.noteandtodoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uz.admiraldev.noteandtodoapp.database.NoteDatabase;
import uz.admiraldev.noteandtodoapp.database.ShoppingListDatabase;
import uz.admiraldev.noteandtodoapp.database.TaskDatabase;
import uz.admiraldev.noteandtodoapp.database.UserDatabase;
import uz.admiraldev.noteandtodoapp.databinding.ActivityMainBinding;
import uz.admiraldev.noteandtodoapp.views.ShoppingFragment;
import uz.admiraldev.noteandtodoapp.views.TasksFragment;
import uz.admiraldev.noteandtodoapp.views.dialogs.AddProductsToPurchaseList;
import uz.admiraldev.noteandtodoapp.views.dialogs.TaskAddDialog;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    private static final String USER_DATA_PREF_KEY = "UserData";
    private static final String APP_LANGUAGE_PREF_KEY = "appLanguage";
    private static NoteDatabase noteDatabase;
    TaskAddDialog addTaskDialog;
    AddProductsToPurchaseList addProductsToPurchaseList;
    private static TaskDatabase taskDatabase;
    private static ShoppingListDatabase shoppingDatabase;
    private static UserDatabase userDatabase;
    NavController navController;
    NavDestination currentDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDatabase = Room.databaseBuilder(this, UserDatabase.class, "users-database").build();
        noteDatabase = Room.databaseBuilder(this, NoteDatabase.class, "note-database").build();
        taskDatabase = Room.databaseBuilder(this, TaskDatabase.class, "task-database").build();
        shoppingDatabase = Room.databaseBuilder(this, ShoppingListDatabase.class, "shopping-database").build();

        bottomNavigationView = binding.bottomNavigationView;
        SharedPreferences sharedPreferences = getSharedPreferences(USER_DATA_PREF_KEY, Context.MODE_PRIVATE);
        String tmpLang = sharedPreferences.getString(APP_LANGUAGE_PREF_KEY,
                Resources.getSystem().getConfiguration().locale.getLanguage());
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(tmpLang);
        AppCompatDelegate.setApplicationLocales(appLocale);

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null)
            navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        hideBottomBar();
        initClicks();

    }


    private void initClicks() {
        binding.floatingActionButton.setOnClickListener(view -> {
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null) {
                int currentFragmentId = currentDestination.getId();
                if (currentFragmentId == R.id.navigation_notes) {
                    navController.navigate(R.id.action_navigation_notes_to_addNoteFragment);
                    hideBottomBar();
                } else if (currentFragmentId == R.id.navigation_to_do_list) {
                    addTaskDialog = new TaskAddDialog(TasksFragment.tasksViewModel, -1);
                    addTaskDialog.show(this.getSupportFragmentManager(), TaskAddDialog.class.toString());
                } else if (currentFragmentId == R.id.navigation_shopping_list) {
                    addProductsToPurchaseList = new AddProductsToPurchaseList(ShoppingFragment.shoppingViewModel);
                    addProductsToPurchaseList.show(getSupportFragmentManager(), addProductsToPurchaseList.getTag());
                }
            }
        });
   /*     getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                currentDestination = navController.getCurrentDestination();
                if (currentDestination != null) {
                    int currentFragmentId = currentDestination.getId();
                    if (currentFragmentId == R.id.navigation_notes ||
                            currentFragmentId == R.id.navigation_to_do_list ||
                            currentFragmentId == R.id.navigation_shopping_list) {
                        finish();
                    }
                } else
                    navController.popBackStack();
            }
        });*/
    }


    private void hideBottomBar() {
        binding.bottomAppBar.setVisibility(View.GONE);
        binding.floatingActionButton.setVisibility(View.GONE);
    }

    public static UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public static TaskDatabase getTaskDatabase() {
        return taskDatabase;
    }

    public static NoteDatabase getNoteDatabase() {
        return noteDatabase;
    }

    public static ShoppingListDatabase getShoppingListDatabase() {
        return shoppingDatabase;
    }
}