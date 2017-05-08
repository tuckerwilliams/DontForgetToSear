package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.ShoppingListAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.gui.RecyclerViewDivider;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnShoppingListFragmentInteractionListener}
 * interface.
 */
public class ShoppingListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ShoppingListAdapter mAdapter;
    private OnShoppingListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingListFragment newInstance(int columnCount) {
        ShoppingListFragment fragment = new ShoppingListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.shoppingListRecyclerView);
        setUpRecyclerView(view, rv);

        return view;
    }


    private void setUpRecyclerView(View view, RecyclerView rv) {
        if (rv != null) {
            Context context = view.getContext();
            //RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);

            //Get the RealmResults list.
            //Todo: Control for empty RealmResults?
            mAdapter = new ShoppingListAdapter(context);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShoppingListFragmentInteractionListener) {
            mListener = (OnShoppingListFragmentInteractionListener) context;
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
    public interface OnShoppingListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onShoppingListFragmentInteraction(Ingredient item);
    }
}
