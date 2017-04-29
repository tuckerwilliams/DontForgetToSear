package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.MainApplication;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by tuckerwilliams on 4/14/17.
 */

public class DisplayRecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private RealmList<Ingredient> mList;
    private Realm realm;
    private int stepCount;
    private String recipeName;

    public DisplayRecipeAdapter(RealmList<Ingredient> items, Context context, String recipeName) {
        this.context = context;
        this.mList = items;
        this.recipeName = recipeName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0://Ingredient.INGREDIENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case 1://Ingredient.STEP_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_step, parent, false);
                return new StepViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Ingredient object = mList.get(position);
        if (object != null) {
            switch (object.getmType()) {
                case 0://Ingredient.INGREDIENT_TYPE:
                    ((IngredientViewHolder) holder).mTitle.setText(context.getResources().getString(
                            R.string.format_ingredient, object.getmName()));
                    ((IngredientViewHolder) holder).mAmount.setText(context.getResources().getString(
                            R.string.format_ingredient_amount, object.getmDescription()));
                    break;
                case 1://Ingredient.STEP_TYPE:
                    stepCount++;
                            ((StepViewHolder) holder).mDescription.setText(context.getResources().getString(
                            R.string.format_step, stepCount, object.getmName()));
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            Ingredient object = mList.get(position);
            if (object != null) {
                return object.getmType();
            }
        }
        return 0;
    }

    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
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

    public void onItemDismiss(int position) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        mList.get(position).deleteFromRealm();
        realm.commitTransaction();
        realm.close();

        //Already removing from RealmList, so no need to say mlist.remove(position);
        notifyItemRemoved(position);
    }

    private static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mAmount;
        public IngredientViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) itemView.findViewById(R.id.tvIngredientAmount);
        }
    }
    private static class StepViewHolder extends RecyclerView.ViewHolder {
        private TextView mDescription;
        public StepViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.tvStepName);
        }
    }

//    public void sort() {
//        Collections.sort(mList);
//    }

}