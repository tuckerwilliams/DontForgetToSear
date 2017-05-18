package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ait.tuckerwilliams.dontforgettosear.MainActivity;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.ShoppingListAdapter;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;
import com.gc.materialdesign.views.ButtonFloat;

import java.util.UUID;

import io.realm.Realm;

import static android.view.View.inflate;

public class ShoppingListFragment extends Fragment {

    private ShoppingListAdapter mAdapter;
    private Activity activity;
    private Context context;

    private OnShoppingListFragmentInteractionListener mListener;

    public ShoppingListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.shoppingListRecyclerView);
        ButtonFloat btnAddGrocery = (ButtonFloat) view.findViewById(R.id.addGrocery);

        setupBtnAddGroceryListener(btnAddGrocery);

        setUpRecyclerView(view, rv);

        setupToolbar();

        return view;
    }

    private void setupBtnAddGroceryListener(ButtonFloat btnAddGrocery) {
        btnAddGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View alertView = inflate(context, R.layout.alertdialog_add_ingredient, null);
                MaterialDialog.Builder mdb = new MaterialDialog.Builder(getActivity())
                        .title("Describe the grocery").customView(alertView, true);

                mdb.positiveText("Done");
                mdb.negativeText("Cancel");

                final EditText etName = (EditText) alertView.findViewById(
                        R.id.etAddIngredient);
                final EditText etServings = (EditText) alertView.findViewById(
                        R.id.etAddQuantity);

                etName.setError("Can't be empty!");
                etServings.setError("Can't be empty!");

                mdb.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Todo: Ensure non-null name of recipe. Set Error on the Builder button?
                        checkIfGroceryNull(etName, etServings);
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

    private void checkIfGroceryNull(EditText etName, EditText etQuantity) {
        if (!etName.getText().toString().equalsIgnoreCase("")
                && !etQuantity.getText().toString().equalsIgnoreCase("")) {

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            Grocery grocery = realm.createObject(Grocery.class, UUID.randomUUID().toString());
            grocery.setmName(etName.getText().toString());
            grocery.setmDescription(etQuantity.getText().toString());
            grocery.setCheckedOff(false);

            realm.commitTransaction();
            //mAdapter.notifyItemInserted(mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();

        } else {
            etName.setError("Can't be empty!");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
        toolbar.setTitle("");
        //toolbar.setDisplayShowTitleEnabled(false); //TODO-// FIXME: 5/10/17
        TextView tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.tb_shopping_list_title);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shopping_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_clear_all_groceries:
                //do stuff
                return true;

            case R.id.action_clear_all_crossed_off_groceries:
                //do stuff
                return true;
            case R.id.action_opensettings:
                ((MainActivity) activity).openSettingsFragment();
                return true;
        }
        return false;
    }


    private void setUpRecyclerView(View view, RecyclerView rv) {
        if (rv != null) {
            Context context = view.getContext();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);

            mAdapter = new ShoppingListAdapter(context);
            rv.setAdapter(mAdapter);

            checkIfAdapterIsEmpty(view);
            setupTouchSupport(rv);
        }
    }

    private void setupTouchSupport(RecyclerView rv) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    private void checkIfAdapterIsEmpty(View view) {
        if (mAdapter.getItemCount() == 0) {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.VISIBLE);
            tvEmptyAdapter.setText(String.format(getString(R.string.format_empty_adapter), "groceries"));
        } else {
            TextView tvEmptyAdapter = (TextView) view.findViewById(R.id.tvEmptyAdapter);
            tvEmptyAdapter.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();
        if (context instanceof OnShoppingListFragmentInteractionListener) {
            mListener = (OnShoppingListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onShoppingListFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnShoppingListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onShoppingListFragmentInteraction();
    }
}
