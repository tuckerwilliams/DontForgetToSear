package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.AddRecipeAdaptor;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.util.UUID;

import io.realm.Realm;

//TODO: Provide auto-complete suggestions for ingredients.
//TODO: Consider doing FloatingActionButton for adding? Is that more Material?

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddRecipeFragment.OnSingleRecipeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECIPENAME = "recipeName";
    private static final String ARG_RECIPESERVINGS = "recipeServings";
    private static final String ARG_RECIPETYPE = "recipeType";

    private String mRecipeName;
    private String mRecipeServings;
    private String mRecipeType;

    private Activity activity;
    private Context context;
    private Realm realm;

    private Spinner stepIngredientSpinner;
    private RecyclerView mIngredientRecyclerView;
    private AddRecipeAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private OnSingleRecipeFragmentInteractionListener mListener;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mRecipeName Parameter 1.
     * @return A new instance of fragment AddRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecipeFragment newInstance(String mRecipeName, String mRecipeServings, String mRecipeType) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, mRecipeName);
        args.putString(ARG_RECIPESERVINGS, mRecipeServings);
        args.putString(ARG_RECIPETYPE, mRecipeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeName = getArguments().getString(ARG_RECIPENAME);
            mRecipeServings = getArguments().getString(ARG_RECIPESERVINGS);
            mRecipeType = getArguments().getString(ARG_RECIPETYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        Button addButton = (Button) view.findViewById(R.id.btnAddStepOrIngredient);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.addRecipeRecyclerView);
        TextView tvRecipeName = (TextView) view.findViewById(R.id.tvRecipeName);
        tvRecipeName.setText(mRecipeName);
        stepIngredientSpinner = (Spinner) view.findViewById(R.id.btnSpinnerForStepOrIngredient);
        EditText etQuantity = (EditText) view.findViewById(R.id.etIngredientQuantity);

        setupSpinnerListener(etQuantity);
        setupRecyclerView(view, rv);
//        setupToolbar();
        setupAddButton(addButton);

        return view;
    }

    private void setupSpinnerListener(final EditText etQuantity) {
        stepIngredientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Ingredient
                    etQuantity.setVisibility(View.VISIBLE);
                } else if (position == 1) { //Step (of the recipe)
                    etQuantity.setVisibility(View.GONE);
                    //TODO: If step, change EditText <width> to match_parent
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("onCreateView", "spinner onNothingSelected()");
            }
        });
    }

    private void setupAddButton(Button button) {
        button.setOnClickListener(this);
    }

//    private void setupToolbar() {
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
//        toolbar.setSystemUiVisibility(View.GONE);
//    }

    private void setupRecyclerView(View view, RecyclerView rv) {
        //AddRecipeAdaptor adapter = new AddRecipeAdaptor(DummyData.getData());
        mAdapter = new AddRecipeAdaptor(context);
        //mAdapter.sort();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);

        // adding touch support
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.addrecipe, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSingleRecipeFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();

        if (context instanceof OnSingleRecipeFragmentInteractionListener) {
            mListener = (OnSingleRecipeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Todo: Is this the best way to handle recipe saving? On detach? What about super.onBackPressed()?
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Todo: Make sure UUID is same when in fragment window. E.g., reset upon exit.
                //Todo: Only create a user if we don't have one.
                Recipe recipe = realm.createObject(Recipe.class, UUID.randomUUID().toString());
                recipe.setRecipeName(mRecipeName);
                recipe.getmList().addAll(mAdapter.getmList());
                recipe.setRecipeType(mRecipeType);
                recipe.setRecipeServings(mRecipeServings);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddStepOrIngredient:
                //Check that step/ingredient isn't empty. For now, QTY empty default to 0.
                View parent = (View) v.getParent();
                View grandParent = (View) parent.getParent();

                EditText etAdd = (EditText) parent.findViewById(R.id.etAddStepOrIngredient);
                EditText etQuantity = (EditText) parent.findViewById(R.id.etIngredientQuantity);
                Spinner spinner = (Spinner) grandParent.findViewById(R.id.btnSpinnerForStepOrIngredient);

                if (etAdd.getText().toString().equals("")) {
                    etAdd.setError("Can't be empty. Need ingredient or step!");
                } else if (etQuantity.getText().toString().equals("") &&
                        spinner.getSelectedItemPosition() == 0) {
                    etAdd.setHint("An ingredient. Basil?");
                    etQuantity.setError("Can't be empty. Need quantity!");
                } else {
                    if (spinner.getSelectedItemPosition() == 1) {
                        etAdd.setHint("A step. Don't forget to sear?");
                        //etQuantity.setText("");
                    }

                    mAdapter.addStepOrIngredient(etAdd.getText().toString(),
                            etQuantity.getText().toString(),
                            spinner.getSelectedItemPosition());
                    etAdd.setText(""); //Clear the input field after adding
                    etQuantity.setText("");
                    //TODO: Is there a way to sort such that you don't have to do whole list?E.g., itemSelected?
                }
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSingleRecipeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSingleRecipeFragmentInteraction(Uri uri);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
