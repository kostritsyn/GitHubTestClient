package kostritsyn.igor.githubtest;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import kostritsyn.igor.githubtest.databinding.ActivityMainBinding;
import kostritsyn.igor.githubtest.di.Injectable;
import kostritsyn.igor.githubtest.ui.auth.AuthActivity;
import kostritsyn.igor.githubtest.ui.auth.AuthViewModel;
import kostritsyn.igor.githubtest.ui.profile.ProfileViewModel;
import kostritsyn.igor.githubtest.ui.repo.RepoListFragment;

public class MainActivity extends AppCompatActivity
        implements HasSupportFragmentInjector, Injectable,
        NavigationView.OnNavigationItemSelectedListener {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ProfileViewModel profileViewModel;

    @Inject
    AuthViewModel authViewModel;

    private ActivityMainBinding binding;

    private boolean authorized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        profileViewModel.getAccessToken().observe(this, accessToken -> {

            MenuItem profileItem = binding.navView.getMenu().findItem(R.id.nav_profile);

            authorized = accessToken != null && accessToken.getToken() != null;
            profileItem.setTitle(authorized ? R.string.menu_logout : R.string.menu_login);
        });

        openSearchFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    public void openSearchFragment() {

        RepoListFragment repoListFragment = new RepoListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_view, repoListFragment, RepoListFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if(authorized) {
                authViewModel.logout();
            } else {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivityForResult(intent, 1000);
            }
        } else if (id == R.id.nav_search) {
            openSearchFragment();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
