package com.ait.tuckerwilliams.dontforgettosear;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.ait.tuckerwilliams.dontforgettosear.fragments.AddRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.DisplayRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.PinnedRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.RecipesListFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.SettingsFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.ShoppingListFragment;

//Todo: Remove 'portrait' declaration from manifest, handle fragment orientation change.

public class MainActivity extends AppCompatActivity
        implements RecipesListFragment.OnRecipesListFragmentListener,
        AddRecipeFragment.OnSingleRecipeFragmentInteractionListener,
        DisplayRecipeFragment.OnDisplayRecipeFragmentInteractionListener,
        ShoppingListFragment.OnShoppingListFragmentInteractionListener,
        PinnedRecipeFragment.OnPinnedRecipeFragmentListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

    public static final String TAG_RECIPES_LIST_FRAG = "RecipesList";
    private static final String TAG_PINNED_RECIPES_FRAG = "PinnedRecipes";
    private static final String TAG_SHOPPING_LIST_FRAG = "ShoppingList";
    private static final String TAG_SETTINGS_FRAG = "Settings";
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pinned:
                    openPinnedRecipeFragment();
                    return true;
                case R.id.navigation_recipes:
                    openRecipesListFragment();
                    return true;
                case R.id.navigation_shoppingList:
                    openShoppingListFragment();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Todo: Add Settings Option to choose opening fragment. For now, open Pinned.
        openPinnedRecipeFragment();
    }

    private void openRecipesListFragment() {
        if (findViewById(R.id.mainUIContainer) != null) {
//            if (savedInstanceState == null) {
            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(TAG_RECIPES_LIST_FRAG);
            getFragmentManager().executePendingTransactions();
            if (mFragment != null && !mFragment.isAdded()) {
                if (mFragment instanceof RecipesListFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainUIContainer, mFragment)
                            .commit();
                }
            } else {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainUIContainer, RecipesListFragment.newInstance(1)
                                , TAG_RECIPES_LIST_FRAG)
                        .commit();
            }
        }
    }

    private void openShoppingListFragment() {

        if (findViewById(R.id.mainUIContainer) != null) {
//            if (savedInstanceState == null) {
            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(TAG_SHOPPING_LIST_FRAG);
            getFragmentManager().executePendingTransactions(); //execute transactions before call to isAdded()
            if (mFragment != null && !mFragment.isAdded()) {
                if (mFragment instanceof ShoppingListFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainUIContainer, mFragment)
                            .commit();
                }
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainUIContainer, ShoppingListFragment.newInstance()
                                , TAG_SHOPPING_LIST_FRAG)
                        .commit();
            }
        }
    }

    private void openPinnedRecipeFragment() {
        if (findViewById(R.id.mainUIContainer) != null) {
//            if (savedInstanceState == null) {
            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(TAG_PINNED_RECIPES_FRAG);
            getFragmentManager().executePendingTransactions();
            if (mFragment != null && !mFragment.isAdded()) {
                if (mFragment instanceof PinnedRecipeFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainUIContainer, mFragment)
                            .commit();
                }
            } else {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainUIContainer, PinnedRecipeFragment.newInstance(1)
                                , TAG_PINNED_RECIPES_FRAG)
                        .commit();
            }
        }
    }

    public void openSettingsFragment() {
        if (findViewById(R.id.mainUIContainer) != null) {
//            if (savedInstanceState == null) {
            Fragment mFragment = getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS_FRAG);
            getFragmentManager().executePendingTransactions();
            if (mFragment != null && !mFragment.isAdded()) {
                if (mFragment instanceof SettingsFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainUIContainer, mFragment).addToBackStack(null)
                            .commit();
                }
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainUIContainer, SettingsFragment.newInstance("Settings")
                                , TAG_SETTINGS_FRAG).addToBackStack(null)
                        .commit();
            }
        }
    }


    public void onRecipeListFragmentListener() {

    }

    @Override
    public void onSingleRecipeFragmentInteraction() {
        Toast.makeText(this, "Clicked on add recipe button!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisplayRecipeFragmentInteraction() {
        Toast.makeText(this, "Clicked on a single recipe!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShoppingListFragmentInteraction() {

    }

    @Override
    public void onPinnedRecipeFragmentInteraction() {

    }

    @Override
    public void onSettingsFragmentInteraction() {

    }
}