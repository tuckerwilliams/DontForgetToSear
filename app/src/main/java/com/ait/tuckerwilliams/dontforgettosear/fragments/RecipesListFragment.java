package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ait.tuckerwilliams.dontforgettosear.MainActivity;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.RecipesListAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;
import com.gc.materialdesign.views.ButtonFloat;

import java.util.ArrayList;

import io.realm.Realm;

import static android.view.View.inflate;

//TODO: Give credit to http://www.foodandwine.com/recipe-finder/dish-types for THUMBNAILS!

public class RecipesListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private RecipesListAdapter mAdapter;

    private Realm realm;

    private int mColumnCount = 1;
    private OnRecipesListFragmentListener mListener;
    private Activity activity;
    private Context context;

    public RecipesListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipesListFragment newInstance(int columnCount) {
        RecipesListFragment fragment = new RecipesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.nestedRecyclerView);
        ButtonFloat btnAddRecipe = (ButtonFloat) view.findViewById(R.id.addRecipe);

        setupBtnAddRecipeListener(btnAddRecipe);
        setUpRecyclerView(view, rv);
        setupToolbar();

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
        toolbar.setTitle("");
        //toolbar.setDisplayShowTitleEnabled(false); //TODO-// FIXME: 5/10/17
        TextView tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.tb_my_recipes_title);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void setupBtnAddRecipeListener(ButtonFloat btnAddRecipe) {
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View alertView = inflate(context, R.layout.alertdialog_addrecipe, null);
                MaterialDialog.Builder mdb = new MaterialDialog.Builder(getActivity())
                        .title("Tell us about the recipe...").customView(alertView, true);

                mdb.positiveText("Done");
                mdb.negativeText("Cancel");

                final EditText etName = (EditText) alertView.findViewById(
                        R.id.alert_addrecipename);
                final EditText etServings = (EditText) alertView.findViewById(
                        R.id.alert_addrecipeservings);
                final Spinner spinner = setupRecipeCategorySpinner(alertView);

                etName.setError("Can't be empty!");
                etServings.setError("Can't be empty!");

                mdb.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Todo: Ensure non-null name of recipe. Set Error on the Builder button?
                        checkIfAddRecipeNameNull(etName, etServings, spinner);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                });

                mdb.show();
            }
        });
    }

    private Spinner setupRecipeCategorySpinner(View alertView) {
        return (Spinner) alertView.findViewById(R.id.spinnerRecipeCategory);
    }

    private void checkIfAddRecipeNameNull(EditText etName, EditText etServings, Spinner spinner) {
        FragmentManager fragmentManager2 = getFragmentManager();

        if (!etName.getText().toString().equalsIgnoreCase("")
                && !etServings.getText().toString().equalsIgnoreCase("")) {
            AddRecipeFragment recipeFragment = AddRecipeFragment.newInstance(
                    etName.getText().toString(), etServings.getText().toString(),
                    spinner.getSelectedItem().toString());

            fragmentManager2.beginTransaction().replace(R.id.mainUIContainer,
                    recipeFragment, "SingleRecipeFragment").addToBackStack(null).commit();
        } else {
            etName.setError("Can't be empty!");
        }
    }

    private void setUpRecyclerView(View view, RecyclerView rv) {
        if (rv != null) {
            Context context = view.getContext();

            setRecyclerViewColumnCount(rv, context);
            prepareRecyclerViewAdapter(view, rv, context);
            setupTouchSupport(rv);
        }
    }

    private void setRecyclerViewColumnCount(RecyclerView rv, Context context) {
        if (mColumnCount <= 1) {
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
    }

    private void prepareRecyclerViewAdapter(View view, RecyclerView rv, Context context) {
        ArrayList<Recipe> list = new ArrayList<>(realm.where(Recipe.class).findAll());

        mAdapter = new RecipesListAdapter(list, context);
        rv.setAdapter(mAdapter);

        if (mAdapter.getItemCount() == 0) {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.VISIBLE);
            tvEmptyAdapter.setText(String.format(getString(R.string.format_empty_adapter), "recipes"));
        } else {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.GONE);
        }
    }

    private void setupTouchSupport(RecyclerView rv) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search_for_recipe:
                // do more stuff
                return true;
            case R.id.action_opensettings:
                ((MainActivity) activity).openSettingsFragment();
                return true;
        }

        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();
        realm = Realm.getDefaultInstance();

        if (context instanceof OnRecipesListFragmentListener) {
            mListener = (OnRecipesListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onRecipeListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        mListener = null;
    }

    public interface OnRecipesListFragmentListener {
        // TODO: Update argument type and name
        void onRecipeListFragmentListener();
    }
}
