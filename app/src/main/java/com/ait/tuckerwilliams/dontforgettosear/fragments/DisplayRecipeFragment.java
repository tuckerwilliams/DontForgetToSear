package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.MainActivity;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.DisplayRecipeAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.util.UUID;

import io.realm.Realm;

/*
 * http://alexzh.com/tutorials/multiple-row-layouts-using-recyclerview/
 */

public class DisplayRecipeFragment extends Fragment {
    private static final String ARG_RECIPENAME = "recipe_name";
    private static final String TAG_ADD_RECIPE_FRAG = "AddRecipe";

    private Recipe recipe;
    private Activity activity;
    private Realm realm;
    private Context context;

    private OnDisplayRecipeFragmentInteractionListener mListener;

    public DisplayRecipeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DisplayRecipeFragment newInstance(String param1) {
        DisplayRecipeFragment fragment = new DisplayRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mRecipeName = getArguments().getString(ARG_RECIPENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display_recipe, container, false);
        RecyclerView rvIng = (RecyclerView) view.findViewById(R.id.displayIngredientsRecyclerView);
        RecyclerView rvDirections = (RecyclerView) view.findViewById(R.id.displayDirectionsRecyclerView);

        setRecipeImage(view);
        setupRecyclerView(view, rvIng, rvDirections);
        setupToolbar();

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
        toolbar.setTitle("");
        //toolbar.setDisplayShowTitleEnabled(false); //TODO-// FIXME: 5/10/17
        TextView tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbTitle.setText(recipe.getRecipeName());
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void setRecipeImage(View view) {
        ImageView imgRecipe = (ImageView) view.findViewById(R.id.imgDisplayRecipePicture);
        imgRecipe.setImageResource(setRecipeImage(recipe.getRecipeType()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_display_recipe, menu);
        MenuItem pinSrcImg = menu.findItem(R.id.action_pin_recipe);

        if (recipe.isPinned())
            pinSrcImg.setIcon(R.drawable.pin);
        else
            pinSrcImg.setIcon(R.drawable.pin_off);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_pin_recipe:
                persistIsPinnedToRealm(item);
                return true;
            case R.id.action_add_all_to_groceries:
                for (int i = 0; i < recipe.getmIngredientList().size(); i++) {
                    addGrocery(recipe.getmIngredientList().get(i));
                }
                return true;
            case R.id.action_opensettings:
                ((MainActivity) activity).openSettingsFragment();
                return true;
            case R.id.action_edit_recipe:
                openAddRecipeFragment();
                return true;
        }
        return false;
    }

    private void openAddRecipeFragment() {
        realm.close();
        AddRecipeFragment recipeFragment = AddRecipeFragment.newInstance(recipe);

        getFragmentManager().beginTransaction().replace(
                R.id.mainUIContainer, recipeFragment, TAG_ADD_RECIPE_FRAG)
                .commit();
    }

    private void persistIsPinnedToRealm(MenuItem item) {
        realm.beginTransaction();
        if (recipe.isPinned()) {
            item.setIcon(R.drawable.pin_off);
            recipe.setPinned(false);
        } else {
            item.setIcon(R.drawable.pin);
            recipe.setPinned(true);
        }
        realm.commitTransaction();
    }

    private void addGrocery(Ingredient ingredient) {
        realm.beginTransaction();

        Grocery grocery = realm.createObject(Grocery.class, UUID.randomUUID().toString());
        grocery.setmName(ingredient.getmName());
        grocery.setmDescription(ingredient.getmDescription());
        grocery.setCheckedOff(false);

        realm.commitTransaction();
    }

    private void setupRecyclerView(View view, RecyclerView rvIng, RecyclerView rvDir) {
        DisplayRecipeAdapter mAdapterIngredients = new DisplayRecipeAdapter(context, recipe, "INGREDIENT");
        DisplayRecipeAdapter mAdapterDirections = new DisplayRecipeAdapter(context, recipe, "DIRECTION");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);

        rvIng.setLayoutManager(linearLayoutManager);
        rvIng.setItemAnimator(new DefaultItemAnimator());

        rvDir.setLayoutManager(linearLayoutManager2);
        rvDir.setItemAnimator(new DefaultItemAnimator());

        rvIng.setAdapter(mAdapterIngredients);
        rvDir.setAdapter(mAdapterDirections);

        setupTouchSupport(rvIng, mAdapterIngredients);
        setupTouchSupport(rvDir, mAdapterDirections);
    }

    private void setupTouchSupport(RecyclerView rv, DisplayRecipeAdapter mAdapter) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDisplayRecipeFragmentInteraction();
        }
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    //Todo: Extract this method somewhere so it isn't repeated!
    private int setRecipeImage(String type) {
        switch (type) {
            case "Burgers":
                return (R.drawable.burgers_thumbnail);
            case "Beans, Grains, Legume":
                return (R.drawable.bean_thumbnail);
            case "Bread, Rolls, Muffins":
                return (R.drawable.bread_roll_thumbnail);
            case "Cakes, Cupcakes":
                return (R.drawable.cakes_thumbnail);
            case "Cocktail":
                return (R.drawable.cocktails_thumbnail);
            case "Desserts":
                return (R.drawable.desserts_thumbail);
            case "Non-alcoholic drinks":
                return (R.drawable.nonalcoholicdrink_thumbnail);
            case "Pasta, Noodles":
                return (R.drawable.pasta_thumbnail);
            case "Pies":
                return (R.drawable.pies_thumbnail);
            case "Pizza":
                return (R.drawable.pizzas_thumbnail);
            case "Salad":
                return (R.drawable.salad_thumbnail);
            case "Sandwiches":
                return (R.drawable.sandwiches_thumbnail);
            case "Soups, Stews":
                return (R.drawable.soups_thumnail);
            default:
                return (R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();

        realm = Realm.getDefaultInstance();
        if (context instanceof OnDisplayRecipeFragmentInteractionListener) {
            mListener = (OnDisplayRecipeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onDisplayRecipeFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        realm.close();
        mListener = null;

        super.onDetach();
    }

    public interface OnDisplayRecipeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDisplayRecipeFragmentInteraction();
    }
}
