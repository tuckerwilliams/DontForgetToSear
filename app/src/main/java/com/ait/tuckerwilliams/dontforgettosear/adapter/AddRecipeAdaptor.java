package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Direction;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

public class AddRecipeAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final Context context;
    private final ArrayList<Ingredient> mIngredientList;
    private final ArrayList<Direction> mDirectionList;
    private Realm realm;
    private boolean isIngredient = false;

    private int stepCount;

    public AddRecipeAdaptor(Context context, String which,
                            @Nullable RealmList<Ingredient> ingList,
                            @Nullable RealmList<Direction> dirList) {
        this.context = context;

        if (which.equals("INGREDIENT"))
            isIngredient = true;

        if (ingList == null && dirList == null) {
            this.mIngredientList = new ArrayList<>();
            this.mDirectionList = new ArrayList<>();
        } else if (ingList != null) {
            this.mIngredientList = new ArrayList<>();
            this.mIngredientList.addAll(ingList);
            this.mDirectionList = new ArrayList<>();
        } else {
            this.mDirectionList = new ArrayList<>();
            this.mDirectionList.addAll(dirList);
            this.mIngredientList = new ArrayList<>();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1://Ingredient.INGREDIENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case 2: //Ingredient.STEP_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_direction, parent, false);
                return new StepViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isIngredient) {
            final Ingredient ing = mIngredientList.get(position);
            if (ing != null) {
                ((IngredientViewHolder) holder).mTitle.setText(context.getResources().getString(
                        R.string.format_ingredient, ing.getmName()));
                ((IngredientViewHolder) holder).mAmount.setText(context.getResources().getString(
                        R.string.format_ingredient_amount, ing.getmDescription()));
            }
        } else {
            final Direction dir = mDirectionList.get(position);
            stepCount++;
            ((StepViewHolder) holder).mDescription.setText(context.getResources().getString(
                    R.string.format_step, stepCount, dir.getmDescription()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isIngredient) {
            if (mIngredientList != null)
                return 1;
        } else {
            if (mDirectionList != null)
                return 2;
        }
        return 0;
    }

    public int getItemCount() {
        if (isIngredient) {
            if (mIngredientList == null)
                return 0;
            return mIngredientList.size();
        } else {
            if (mDirectionList == null)
                return 0;
            return mDirectionList.size();
        }
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (isIngredient) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mIngredientList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mIngredientList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        } else {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDirectionList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDirectionList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void onItemDismiss(int position) {
        stepCount--;
        //Todo: Replace with execute transaction block.
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if (isIngredient) {
            Ingredient ing = mIngredientList.get(position);
            mIngredientList.remove(position); // NEED THIS! Crash without.
            ing.deleteFromRealm();
            notifyItemRemoved(position);
        } else {
            Direction direction = mDirectionList.get(position);
            mDirectionList.remove(position); // NEED THIS! Crash without.

            direction.deleteFromRealm();
            notifyItemRemoved(position);
        }
        realm.commitTransaction();//Already removing from RealmList, so no need to say mlist.remove(position);
    }

    public void addIngredient(String name, String desc) {

        //Todo: Could replace below with Realm.executeTransaction block.
        realm = Realm.getDefaultInstance();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        Ingredient ingredient = realm.createObject(Ingredient.class, UUID.randomUUID().toString());
        ingredient.setmName(name);
        ingredient.setmDescription(desc);
        //Todo: Find out where to put the ingredient or step, specifically, and notify changed at that pos.
        mIngredientList.add(mIngredientList.size(), ingredient);

        realm.commitTransaction();
        notifyItemInserted(mIngredientList.size());
    }

    public void addStep(String desc) {
        realm = Realm.getDefaultInstance();

        //Todo: Could replace below with Realm.executeTransaction block.
        if (!realm.isInTransaction())
            realm.beginTransaction();

        Direction direction = realm.createObject(Direction.class, UUID.randomUUID().toString());
        direction.setmDescription(desc);

        //Todo: Find out where to put the ingredient or step, specifically, and notify changed at that pos.
        mDirectionList.add(mDirectionList.size(), direction);

        realm.commitTransaction();
        notifyItemInserted(mDirectionList.size());
    }

    public ArrayList<Ingredient> getmIngredientList() {
        return mIngredientList;
    }

    public ArrayList<Direction> getmDirectionList() {
        return mDirectionList;
    }

    private static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mAmount;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) itemView.findViewById(R.id.tvIngredientAmount);
        }
    }

    private static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDescription;

        public StepViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.tvStepName);
        }

    }
}