package com.ait.tuckerwilliams.dontforgettosear;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.fragments.AddRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.DisplayRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.EmptyFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.RecipesListFragment;
import com.ait.tuckerwilliams.dontforgettosear.fragments.ShoppingListFragment;

public class MainActivity extends AppCompatActivity
        implements RecipesListFragment.OnRecipeFragmentListener,
        AddRecipeFragment.OnSingleRecipeFragmentInteractionListener,
        DisplayRecipeFragment.OnDisplayRecipeFragmentInteractionListener,
        EmptyFragment.OnEmptyFragmentListener,
        ShoppingListFragment.OnShoppingListFragmentInteractionListener{

    private TextView mTextMessage;
    private static int CURR_FRAGMENT = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_pinned:
//                    mTextMessage.setText(R.string.title_pinned);
                    //Todo: Add Pinned Recipe Fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainUIContainer, new EmptyFragment(),
                            "EMPTY_TEST_FRAG").commit();
                    return true;
                case R.id.navigation_recipes:
                    openRecipesListFragment();
                    return true;
                case R.id.navigation_shoppingList:
                    //Todo: Add Shopping List Fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainUIContainer, new EmptyFragment(),
                            "EMPTY_TEST_FRAG").commit();
//                    mTextMessage.setText(R.string.title_shoppingList);
                    return true;
                case R.id.navigation_timer:
                    //Todo: Add Timer Fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainUIContainer, new EmptyFragment(),
                            "EMPTY_TEST_FRAG").commit();
//                    mTextMessage.setText(R.string.title_timer);
                    return true;
                case R.id.navigation_settings:
                    //Todo: Add Settings Fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainUIContainer, new EmptyFragment(),
                            "EMPTY_TEST_FRAG").commit();
//                    mTextMessage.setText(R.string.title_settings);
                    return true;
            }
            return false;
        }

    };

    private void openRecipesListFragment() {
        if (findViewById(R.id.mainUIContainer) != null) {
//                        if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainUIContainer, RecipesListFragment.newInstance(1, "RecipesList", "RecipeDesc")
                                , "RECIPES_LIST")
                        .commit();
//                        }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onRecipeFragmentListener(Uri uri) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        DisplayRecipeFragment displayRecipe = (DisplayRecipeFragment.newInstance("displayRecipe", "displayRecipe"));

        fragmentManager.beginTransaction()
                .replace(R.id.mainUIContainer, displayRecipe, "singleRecipeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSingleRecipeFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Clicked on add recipe button!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisplayRecipeFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Clicked on a single recipe!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Ingredient item) {

    }
}