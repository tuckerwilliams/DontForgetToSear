package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.DisplayRecipeAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.DummyData;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.util.ArrayList;

/*
 * http://alexzh.com/tutorials/multiple-row-layouts-using-recyclerview/
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisplayRecipeFragment.OnDisplayRecipeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisplayRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayRecipeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECIPENAME = "recipe_name";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mRecipeName;
    private String mParam2;

    private DisplayRecipeAdapter mAdapter;
    private Recipe recipe;

    private OnDisplayRecipeFragmentInteractionListener mListener;

    public DisplayRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayRecipeFragment newInstance(String param1, String param2) {
        DisplayRecipeFragment fragment = new DisplayRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeName = getArguments().getString(ARG_RECIPENAME);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_recipe, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.displayRecipeRecyclerView);
        TextView tvRecipeName = (TextView) view.findViewById(R.id.tvRecipeNameDisplayRecipeFrag);
        tvRecipeName.setText(mRecipeName);

        setupRecyclerView(view, rv);

        return view;
    }

    private void setupRecyclerView(View view, RecyclerView rv) {
        Log.i("setupRV,DRFrag", String.format("Recipe list size = %d", recipe.getmList().size()));
        mAdapter = new DisplayRecipeAdapter(recipe.getmList(), getContext(), mRecipeName);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDisplayRecipeFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDisplayRecipeFragmentInteractionListener) {
            mListener = (OnDisplayRecipeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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
    public interface OnDisplayRecipeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDisplayRecipeFragmentInteraction(Uri uri);
    }
}
