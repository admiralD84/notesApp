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

import uz.admiraldev.noteandtodoapp.database.MyAppDatabase;
import uz.admiraldev.noteandtodoapp.databinding.ActivityMainBinding;
import uz.admiraldev.noteandtodoapp.views.ShoppingFragment;
import uz.admiraldev.noteandtodoapp.views.TasksFragment;
import uz.admiraldev.noteandtodoapp.views.dialogs.AddProductsToPurchaseList;
import uz.admiraldev.noteandtodoapp.views.dialogs.TaskAddDialog;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    TaskAddDialog addTaskDialog;
    AddProductsToPurchaseList addProductsToPurchaseList;
    private static MyAppDatabase myAppDatabase;
    NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myAppDatabase = Room.databaseBuilder(this, MyAppDatabase.class, "my-database").build();

        bottomNavigationView = binding.bottomNavigationView;
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String tmpLang = sharedPreferences.getString("appLanguage",
                Resources.getSystem().getConfiguration().locale.getLanguage());
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(tmpLang);
        AppCompatDelegate.setApplicationLocales(appLocale);

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        assert navHostFragment != null;
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
                    addTaskDialog = new TaskAddDialog(TasksFragment.tasksViewModel);
                    addTaskDialog.show(this.getSupportFragmentManager(), TaskAddDialog.class.toString());
                } else if (currentFragmentId == R.id.navigation_shopping_list) {
                    addProductsToPurchaseList = new AddProductsToPurchaseList(ShoppingFragment.shoppingViewModel);
                    addProductsToPurchaseList.show(getSupportFragmentManager(), addProductsToPurchaseList.getTag());
                }
            }
        });
    }

    private void hideBottomBar() {
        binding.bottomAppBar.setVisibility(View.GONE);
        binding.floatingActionButton.setVisibility(View.GONE);
    }

    public static MyAppDatabase getMyAppDatabase() {
        return myAppDatabase;
    }

}