package com.example.praktikum4;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.praktikum4.ui.AddBookFragment;
import com.example.praktikum4.ui.FavoritesFragment;
import com.example.praktikum4.ui.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Add animation to bottom navigation
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bottomNavigationView.startAnimation(slideUp);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_favorites) {
            selectedFragment = new FavoritesFragment();
        } else if (itemId == R.id.nav_add) {
            selectedFragment = new AddBookFragment();
        }

        if (selectedFragment != null) {
            // Add animation for fragment transition
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }

        return false;
    }
}