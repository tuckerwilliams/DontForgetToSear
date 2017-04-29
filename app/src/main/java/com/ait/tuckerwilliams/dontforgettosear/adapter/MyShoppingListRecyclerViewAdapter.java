package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.fragments.ShoppingListFragment.OnShoppingListFragmentInteractionListener;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ingredient} and makes a call to the
 * specified {@link OnShoppingListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private RealmList<Ingredient> mList;
    private Realm realm;
    private int stepCount;
    private final OnShoppingListFragmentInteractionListener mListener;

    //Todo: Do I want a separate persisted Realm of Ingredients? Maybe.
    //Todo: But if so, how would I find it? Ingredient.class? Or create a Shopping list class?
    //It's kind of like persisting another kind of recipe, no?

    public MyShoppingListRecyclerViewAdapter(Context context, OnShoppingListFragmentInteractionListener listener) {
        mListener = listener;
        this.context = context;
        mList = new RealmList<Ingredient>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Ingredient object = mList.get(position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Ingredient mIngredient;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
