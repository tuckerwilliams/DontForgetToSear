package com.ait.tuckerwilliams.dontforgettosear.adapter;

/**
 * Created by tuckerwilliams on 4/14/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.reflect.Type;
import java.util.UUID;

import io.realm.Realm;

public class AddRecipeAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private Realm realm;
    private ArrayList<Ingredient> mList;
    private int stepCount;

    //Not taking a list because we are making a recipe. Wouldn't make sense to pass list.
    public AddRecipeAdaptor(Context context) {
        this.context = context;
        mList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0://Ingredient.INGREDIENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case 1: //Ingredient.STEP_TYPE:
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
                case 0: //Ingredient.INGREDIENT_TYPE:
                    ((IngredientViewHolder) holder).mTitle.setText(context.getResources().getString(
                            R.string.format_ingredient, object.getmName()));
                    ((IngredientViewHolder) holder).mAmount.setText(context.getResources().getString(
                            R.string.format_ingredient_amount, object.getmDescription()));
                    break;
                case 1: //Ingredient.STEP_TYPE:
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
        //Todo: Replace with execute transaction block.
        realm.beginTransaction();
        mList.get(position).deleteFromRealm();
        realm.commitTransaction();

        mList.remove(position);
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

    public void addStepOrIngredient(String name, String desc, int type) {

        //Todo: Could replace below with Realm.executeTransaction block.
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Ingredient ingredient = realm.createObject(Ingredient.class, UUID.randomUUID().toString());
        ingredient.setmName(name);
        ingredient.setmDescription(desc);
        ingredient.setmType(type);

        //Increment if step
        if (type == 1)
            stepCount++;

        realm.commitTransaction();

        //Todo: Find out where to put the ingredient or step, specifically, and notify changed at that pos.
        mList.add(mList.size(), ingredient);
        notifyItemInserted(mList.size());
    }

    public ArrayList<Ingredient> getmList() {
        return mList;
    }
}