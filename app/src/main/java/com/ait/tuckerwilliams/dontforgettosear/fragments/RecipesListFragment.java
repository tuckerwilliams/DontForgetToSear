package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.RecipesListAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.gui.RecyclerViewDivider;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.util.ArrayList;

import io.realm.Realm;

import static android.view.View.inflate;

//TODO: Give credit to http://www.foodandwine.com/recipe-finder/dish-types for THUMBNAILS!

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRecipeFragmentListener}
 * interface.
 */
public class RecipesListFragment extends Fragment {

    //Todo goal: Allow users to add their own recipe categories. How find thumbnail image, though?

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_RECIPE_NAME = "name";
    private static final String ARG_RECIPE_DESC = "description";

    private RecipesListAdapter mAdapter;

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnRecipeFragmentListener mListener;
    private Activity activity;
    private Context context;
    private Realm realm;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipesListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipesListFragment newInstance(int columnCount, String recipeName,
                                                  String recipeDescrption) {
        RecipesListFragment fragment = new RecipesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_RECIPE_NAME, recipeName);
        args.putString(ARG_RECIPE_DESC, recipeDescrption);
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
        Button btnAddRecipe = (Button) view.findViewById(R.id.addRecipe);
        Spinner recipeTypeSpinner = (Spinner) view.findViewById(R.id.spinnerRecipeCategory);

        setupBtnAddRecipeListener(btnAddRecipe);

        // Set the adapter
        setUpRecyclerView(view, rv);

//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
//        toolbar.setTitle("");
//        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
//        setHasOptionsMenu(true);

        return view;
    }

    private void setupBtnAddRecipeListener(Button btnAddRecipe) {
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Todo: Set up MultiSpinner to allow user to give Recipe categories, e.g., chicken

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //final EditText userInput = new EditText(context);
                //userInput.setHint("Name...");
                final View alertView = inflate(context, R.layout.alertdialog_addrecipe, null);
                builder.setView(alertView);
                builder.setTitle("Tell us a little about the recipe...")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FragmentManager fragmentManager2 = getFragmentManager();
                                FragmentTransaction fragmentTransaction2 =
                                        fragmentManager2.beginTransaction();
                                //Todo: Ensure non-null name of recipe. Set Error on the Builder button?
                                EditText etName = (EditText) alertView.findViewById(
                                        R.id.alert_addrecipename);
                                EditText etServings = (EditText) alertView.findViewById(
                                        R.id.alert_addrecipeservings);
                                Spinner spinner = setupRecipeCategorySpinner(alertView);

                                checkIfAddRecipeNameNull(fragmentManager2, etName, etServings, spinner);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private Spinner setupRecipeCategorySpinner(View alertView) {
        Spinner recipeTypeSpinner = (Spinner) alertView.findViewById(
                R.id.spinnerRecipeCategory);
        return recipeTypeSpinner;
    }

    private void checkIfAddRecipeNameNull(FragmentManager fragmentManager2, EditText etName,
                                          EditText etServings, Spinner spinner) {
        if (etName.getText().toString() != null) {
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
        if (rv instanceof RecyclerView) {
            Context context = view.getContext();
            //RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                rv.setLayoutManager(new LinearLayoutManager(context));
            } else {
                rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //Get the RealmResults list.
            //Todo: Control for empty RealmResults?
            Realm realm = Realm.getDefaultInstance();
            ArrayList<Recipe> list = new ArrayList(realm.where(Recipe.class).findAll());

            mAdapter = new RecipesListAdapter(list, mListener, context);
            rv.setAdapter(mAdapter);

            // adding touch support
            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(rv);

            //TODO: This was causing casting problems. Fix it!
            RecyclerViewDivider decoration = new RecyclerViewDivider(getContext(), Color.GRAY, 1.5f);
            rv.addItemDecoration(decoration);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipelist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                // do stuff
                return true;

            case R.id.action_search:
                // do more stuff
                return true;
        }

        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();
        if (context instanceof OnRecipeFragmentListener) {
            mListener = (OnRecipeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRecipeFragmentListener {
        // TODO: Update argument type and name
        void onRecipeFragmentListener(Uri uri);
    }
}
