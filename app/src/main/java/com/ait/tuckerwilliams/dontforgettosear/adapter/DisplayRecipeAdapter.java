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
import com.ait.tuckerwilliams.dontforgettosear.data.Direction;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.gui.DrawOver;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

//Todo: Probably want to disable OnSwipeDismiss in DisplayRecipe. But, OnItemView Click, maybe
//cross out an ingredient, e.g. it has been used in recipe being cooked.

public class DisplayRecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<Object> mList;

    private RealmList<Ingredient> mIngredientList;
    private RealmList<Direction> mDirectionList;
    private Realm realm;
    private int stepCount;
    private String recipeName;

    public DisplayRecipeAdapter(RealmList<Ingredient> mIngredientList, RealmList<Direction> directions, Context context, String recipeName) {
        this.context = context;
        this.mIngredientList = mIngredientList;
        this.mDirectionList = directions;
        this.recipeName = recipeName;
        mList = new ArrayList<>();
        mList.addAll(mIngredientList);
        mList.addAll(mDirectionList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1://Ingredient.INGREDIENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case 2://Ingredient.STEP_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_step, parent, false);
                return new StepViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Object object = mList.get(position);
        //int checked = 0;
        if (object != null) {
            if (object instanceof Ingredient) {
                ((IngredientViewHolder) holder).mTitle.setText(context.getResources().getString(
                        R.string.format_ingredient, ((Ingredient) object).getmName()));
                ((IngredientViewHolder) holder).mAmount.setText(context.getResources().getString(
                        R.string.format_ingredient_amount, ((Ingredient)object).getmDescription()));
            } else if (object instanceof Direction) {
                stepCount++;
                ((StepViewHolder) holder).mDescription.setText(context.getResources().getString(
                        R.string.format_step, stepCount, ((Direction)object).getmDescription()));
            } //else {its a section divider, so do nothing//
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object != null) {
                    if (object instanceof Ingredient) {
                        //Todo: How to erase? // how to tell if erasing needs to be done.
                        DrawOver.drawOverTextView(((IngredientViewHolder) holder).mTitle);
                        addGrocery((Ingredient)object);
                    }
                }
            }
        });
    }

    private void addGrocery(Ingredient ingredient) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Grocery grocery = realm.createObject(Grocery.class, UUID.randomUUID().toString());
        grocery.setmName(ingredient.getmName());
        grocery.setmDescription(ingredient.getmDescription());
        grocery.setCheckedOff(false);

        realm.commitTransaction();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            Object object = mList.get(position);
            if (object != null) {
                if (object instanceof Ingredient)
                    return 1;
                else if (object instanceof Direction)
                    return 2;
                else
                    return 3;
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
        //TODO: Fix. maybe, cast to proper type, then find in list, then delete?
        realm.beginTransaction();
        Object obj = mList.get(position);
        if (obj instanceof Ingredient) {
            ((Ingredient) obj).deleteFromRealm();
            notifyItemRemoved(position);
        }

        else if (obj instanceof Direction) {
            ((Direction) obj).deleteFromRealm();
            notifyItemRemoved(position);
        } //else {its a section divider, so do nothing//
        realm.commitTransaction();//Already removing from RealmList, so no need to say mlist.remove(position);
    }

    public RealmList<Ingredient> getmIngredientList() {
        return mIngredientList;
    }

    private static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mAmount;
        private IngredientViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) itemView.findViewById(R.id.tvIngredientAmount);
        }
    }

    private static class StepViewHolder extends RecyclerView.ViewHolder {
        private TextView mDescription;
        private StepViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.tvStepName);
        }
    }
}