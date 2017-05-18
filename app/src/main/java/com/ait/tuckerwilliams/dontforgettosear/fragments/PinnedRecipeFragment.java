package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.MainActivity;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.RecipesListAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.util.ArrayList;

import io.realm.Realm;

//TODO: Give credit to http://www.foodandwine.com/recipe-finder/dish-types for THUMBNAILS!

public class PinnedRecipeFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecipesListAdapter mAdapter;

    private int mColumnCount = 1;
    private Realm realm;

    private OnPinnedRecipeFragmentListener mListener;
    private Activity activity;

    public PinnedRecipeFragment() {
    }

    public static PinnedRecipeFragment newInstance(int columnCount) {
        PinnedRecipeFragment fragment = new PinnedRecipeFragment();
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

        View view = inflater.inflate(R.layout.fragment_pinned_recipes, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.pinnedRecipesRecyclerView);

        setUpRecyclerView(view, rv);
        setupToolbar();

        return view;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
        toolbar.setTitle("");
        //toolbar.setDisplayShowTitleEnabled(false); //TODO-// FIXME: 5/10/17
        TextView tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.tb_pinned_recipes_title);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void setUpRecyclerView(View view, RecyclerView rv) {
        if (rv != null) {
            Context context = view.getContext();

            setupRecyclerViewColumnCount(rv, context);
            prepareRecyclerViewAdapter(view, rv, context);
            setupTouchSupport(rv);
        }
    }

    private void setupTouchSupport(RecyclerView rv) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    private void prepareRecyclerViewAdapter(View view, RecyclerView rv, Context context) {

        ArrayList<Recipe> list = new ArrayList(realm.where(Recipe.class)
                .equalTo("pinned", true).findAll());

        mAdapter = new RecipesListAdapter(list, context);
        rv.setAdapter(mAdapter);

        if (mAdapter.getItemCount() == 0) {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.VISIBLE);
            tvEmptyAdapter.setText(String.format(getString(R.string.format_empty_adapter), "pinned recipes"));
        } else {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerViewColumnCount(RecyclerView rv, Context context) {
        if (mColumnCount <= 1) {
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rv.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
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
        realm = Realm.getDefaultInstance();

        if (context instanceof OnPinnedRecipeFragmentListener) {
            mListener = (OnPinnedRecipeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onPinnedRecipeFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        mListener = null;
    }

    public interface OnPinnedRecipeFragmentListener {
        // TODO: Update argument type and name
        void onPinnedRecipeFragmentInteraction();
    }
}
