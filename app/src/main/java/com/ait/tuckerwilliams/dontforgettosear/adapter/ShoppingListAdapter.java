package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.fragments.RecipesListFragment.OnRecipeFragmentListener;
import com.ait.tuckerwilliams.dontforgettosear.fragments.ShoppingListFragment;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * {@link RecyclerView.Adapter} that can display a  and makes a call to the
 * specified {@link OnRecipeFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private Realm realm;
    private ArrayList<Grocery> mList;

    public ShoppingListAdapter(Context context) {
        this.context = context;

        realm = Realm.getDefaultInstance();
        RealmResults<Grocery> results = realm.where(Grocery.class).findAll();
        mList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            mList.add(results.get(i));
        }
    }

    public ArrayList<Grocery> getmList() {
        return mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Grocery grocery = mList.get(position);
        if (grocery != null) {
            //((ViewHolder)holder).mTitle.setText(grocery.getmName());
            ((ViewHolder) holder).mTitle.setText(grocery.getmName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            Grocery object = mList.get(position);
            if (object != null) {
                return 1;
            }
        }
        return 0;
    }

    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }


    public void onItemDismiss(int position) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Grocery grocery = mList.get(position);
        grocery.deleteFromRealm();

        mList.remove(position);

        notifyItemRemoved(position);
        realm.commitTransaction();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mTitle;
        private final TextView mAmount;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) view.findViewById(R.id.tvIngredientAmount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}