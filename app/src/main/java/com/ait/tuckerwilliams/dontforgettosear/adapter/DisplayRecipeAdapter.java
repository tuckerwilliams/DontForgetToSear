package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Direction;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.data.Ingredient;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;
import com.ait.tuckerwilliams.dontforgettosear.view.DrawOver;
import com.gc.materialdesign.views.CheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import io.realm.Realm;

import static android.view.View.inflate;

//Todo: Probably want to disable OnSwipeDismiss in DisplayRecipe. But, OnItemView Click, maybe
//cross out an ingredient, e.g. it has been used in recipe being cooked.

public class DisplayRecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {


    private final Context context;

    private ArrayList<Ingredient> mIngredientList;
    private ArrayList<Direction> mDirectionList;

    private int stepCount;

    private boolean isIngredient = false;

    public DisplayRecipeAdapter(Context context, Recipe recipe, String which) {

        this.context = context;

        if (which.equals("INGREDIENT")) {
            isIngredient = true;
            mIngredientList = new ArrayList<>();
            for (int i = 0; i < recipe.getmIngredientList().size(); i++) {
                mIngredientList.add(recipe.getmIngredientList().get(i));
            }
        } else {
            mDirectionList = new ArrayList<>();
            for (int i = 0; i < recipe.getmDirectionList().size(); i++) {
                mDirectionList.add(recipe.getmDirectionList().get(i));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1://Ingredient.INGREDIENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case 2://Ingredient.STEP_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_direction, parent, false);
                return new StepViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (isIngredient) {
            final Ingredient ing = mIngredientList.get(position);
            if (ing != null) {
                setIngredientTextViews((IngredientViewHolder) holder, ing);
                editIngredient((IngredientViewHolder) holder, ing);

                ((IngredientViewHolder) holder).cbFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        draw(holder, ing.getId());
                    }
                });
            }
        } else {
            Direction direction = mDirectionList.get(position);
            stepCount++;
            ((StepViewHolder) holder).mDescription.setText(context.getResources().getString(
                    R.string.format_step, stepCount, direction.getmDescription()));
        }
    }


    private void setIngredientTextViews(IngredientViewHolder holder, Ingredient object) {
        holder.mTitle.setText(context.getResources().getString(
                R.string.format_ingredient, object.getmName()));
        holder.mAmount.setText(context.getResources().getString(
                R.string.format_ingredient_amount, object.getmDescription()));
    }

    private void editIngredient(final IngredientViewHolder holder, final Ingredient object) {

        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog.Builder mdb1 = new MaterialDialog.Builder(context)
                        .autoDismiss(true)
                        .negativeText("Add to grocery")
                        .positiveText("Edit")
                        .contentGravity(GravityEnum.CENTER)
                        .stackingBehavior(StackingBehavior.ALWAYS);

                mdb1.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final View etView = inflate(context, R.layout.alertdialog_edit_ingredient, null);

                        MaterialDialog.Builder mdb2 = new MaterialDialog.Builder(context)
                                .title("Edit...")
                                .positiveText("Save")
                                .negativeText("Cancel")
                                .customView(etView, false)
                                .autoDismiss(true);

                        final MaterialEditText etIngredientName = (MaterialEditText) etView.findViewById(R.id.etChangeStepOrIngredient);
                        final MaterialEditText etIngredientQuantity = (MaterialEditText) etView.findViewById(R.id.etChangeIngredientQuantity);

                        mdb2.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (!etIngredientName.getText().toString().equals("") &&
                                        !etIngredientQuantity.getText().toString().equals("")) {

                                    commitToRealmIfNonNull(object,
                                            etIngredientName,
                                            etIngredientQuantity,
                                            holder);
                                }
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            }
                        });

                        final MaterialDialog dialog2 = mdb2.build();
                        dialog2.show();
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addGrocery(object);
                    }
                });

                final MaterialDialog dialog1 = mdb1.build();
                dialog1.show();
            }
        });
    }

    private void commitToRealmIfNonNull(Ingredient object, MaterialEditText etIngredientName, MaterialEditText etIngredientQuantity, IngredientViewHolder holder) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Ingredient ing = realm.where(Ingredient.class).equalTo("id", object.getId()).findFirst();

        if (ing != null) {
            ing.setmName(etIngredientName.getText().toString());
            ing.setmDescription(etIngredientQuantity.getText().toString());

            realm.commitTransaction();
            realm.close();

            holder.mTitle.setText(context.getResources().getString(
                    R.string.format_ingredient, object.getmName()));
            holder.mAmount.setText(context.getResources().getString(
                    R.string.format_ingredient_amount, object.getmDescription()));

        }
    }

    private void draw(final RecyclerView.ViewHolder holder, String id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Ingredient ing = realm.where(Ingredient.class).equalTo("id", id).findFirst();

        if (!ing.isCheckedOff() || !((IngredientViewHolder) holder).cbFinish.isCheck()) {
            DrawOver.drawOverTextView(((IngredientViewHolder) holder).mTitle);
            DrawOver.drawOverTextView(((IngredientViewHolder) holder).mAmount);
            ing.setCheckedOff(true);
            ((IngredientViewHolder) holder).cbFinish.setChecked(true);
            //addGrocery((Ingredient) object);
        } else {
            DrawOver.eraseDrawFromTextView(((IngredientViewHolder) holder).mTitle);
            DrawOver.eraseDrawFromTextView(((IngredientViewHolder) holder).mAmount);
            ing.setCheckedOff(false);
            ((IngredientViewHolder) holder).cbFinish.setChecked(false);
        }

        realm.commitTransaction();
        realm.close();
    }

    private void addGrocery(Ingredient ingredient) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Grocery grocery = realm.createObject(Grocery.class, UUID.randomUUID().toString());
        grocery.setmName(ingredient.getmName());
        grocery.setmDescription(ingredient.getmDescription());
        grocery.setCheckedOff(false);

        realm.commitTransaction();
        realm.close();
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

        Realm realm = Realm.getDefaultInstance();
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
        realm.commitTransaction();
    }

    private static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mAmount;
        private final ImageView editImg;
        private final CheckBox cbFinish;

        private IngredientViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) itemView.findViewById(R.id.tvIngredientAmount);
            editImg = (ImageView) itemView.findViewById(R.id.imgEditRecipeIngredientOrStep);
            cbFinish = (CheckBox) itemView.findViewById(R.id.cbIngredientDone);
        }
    }

    private static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDescription;

        private StepViewHolder(View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.tvStepName);
        }
    }
}