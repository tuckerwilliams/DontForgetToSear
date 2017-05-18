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
import com.afollestad.materialdialogs.MaterialDialog;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Grocery;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.inflate;

/**
 * {@link RecyclerView.Adapter} that can display a  and makes a call to the
 * specified {@link com.ait.tuckerwilliams.dontforgettosear.fragments.RecipesListFragment.OnRecipesListFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private final Context context;
    private final ArrayList<Grocery> mList;
    private Realm realm;

    public ShoppingListAdapter(Context context) {
        this.context = context;

        realm = Realm.getDefaultInstance();
        RealmResults<Grocery> results = realm.where(Grocery.class).findAll();
        mList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            mList.add(results.get(i));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ingredient, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Grocery grocery = mList.get(position);
        if (grocery != null) {
            ((ViewHolder) holder).mTitle.setText(grocery.getmName());
            ((ViewHolder) holder).mAmount.setText(grocery.getmDescription());
        }

        setupEditGroceryListener((ViewHolder) holder, grocery);
    }

    private void setupEditGroceryListener(final ViewHolder holder, final Grocery grocery) {
        holder.imgEditGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog.Builder mdb2 = new MaterialDialog.Builder(context);
                mdb2.title("Edit this grocery");

                final View etView = inflate(context, R.layout.alertdialog_edit_ingredient, null);
                final MaterialEditText etIngredientName = (MaterialEditText) etView.findViewById(R.id.etChangeStepOrIngredient);
                final MaterialEditText etIngredientQuantity = (MaterialEditText) etView.findViewById(R.id.etChangeIngredientQuantity);

                mdb2.customView(etView, false);
                mdb2.autoDismiss(true);

                mdb2.positiveText("Save");
                mdb2.negativeText("Cancel");

                mdb2.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!etIngredientName.getText().toString().equals("") &&
                                !etIngredientQuantity.getText().toString().equals("")) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();

                            grocery.setmName(etIngredientName.getText().toString());
                            grocery.setmDescription(etIngredientQuantity.getText().toString());

                            realm.commitTransaction();
                            realm.close();

                            holder.mTitle.setText(context.getResources().getString(
                                    R.string.format_ingredient, grocery.getmName()));
                            holder.mAmount.setText(context.getResources().getString(
                                    R.string.format_ingredient_amount, grocery.getmDescription()));
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                });
                mdb2.show();
            }

        });
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

    public void onItemMove(int fromPosition, int toPosition) {
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
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mTitle;
        private final TextView mAmount;
        private final ImageView imgEditGrocery;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.tvIngredientName);
            mAmount = (TextView) view.findViewById(R.id.tvIngredientAmount);
            imgEditGrocery = (ImageView) view.findViewById(R.id.imgEditRecipeIngredientOrStep);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}