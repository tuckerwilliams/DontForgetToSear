package com.ait.tuckerwilliams.dontforgettosear.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.fragments.DisplayRecipeFragment;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;

import static com.ait.tuckerwilliams.dontforgettosear.MainActivity.TAG_RECIPES_LIST_FRAG;

/**
 * {@link RecyclerView.Adapter} that can display a  and makes a call to the
 * specified {@link com.ait.tuckerwilliams.dontforgettosear.fragments.RecipesListFragment.OnRecipesListFragmentListener}.
 */
public class RecipesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final String TAG_DISPLAY_RECIPE_FRAG = "DisplayRecipe";

    //private final OnRecipeFragmentListener mListener;

    private final ArrayList<Recipe> mList;
    private final Context context;
    private Realm realm;

    public RecipesListAdapter(ArrayList<Recipe> items, Context context) {
        mList = items; //TODO: Update this with realm database! This is where the Recipe list appears.
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recipe, parent, false);

        realm = Realm.getDefaultInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Recipe recipeObj = mList.get(position);
        if (recipeObj != null) {
            setRecipeRowThumbnail(holder, recipeObj.getRecipeType());
            ((ViewHolder) holder).mTitle.setText(recipeObj.getRecipeName());
            ((ViewHolder) holder).mRecipeType.setText(context.getResources().getString(
                    R.string.format_recipe_category, recipeObj.getRecipeType(),
                    recipeObj.getRecipeServings()));
        }

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onRecipeFragmentListener(holder.mItem);
//                }
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) context;
                DisplayRecipeFragment myFrag = DisplayRecipeFragment.newInstance(
                        recipeObj != null ? recipeObj.getRecipeName() : null);

                myFrag.setRecipe(recipeObj);
                //Create a bundle to pass data, add data, set the bundle to your fragment and:
                try {
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(activity.getFragmentManager()
                                    .findFragmentByTag(TAG_RECIPES_LIST_FRAG)
                                    .getId(), myFrag)
                            .addToBackStack(null).commit();
                } catch (Exception exception) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainUIContainer, myFrag)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            Recipe object = mList.get(position);
            if (object != null) {
                return 0;
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

        realm.beginTransaction();

        if (!mList.get(position).isPinned()) {
            mList.get(position).deleteFromRealm();
            mList.remove(position);
            notifyItemRemoved(position);
        } else //we are in the Pinned Recipes list
        {
            mList.get(position).setPinned(false);
        }

        realm.commitTransaction();
        realm.close();
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

    private void setRecipeRowThumbnail(final RecyclerView.ViewHolder holder, String type) {
        switch (type) {
            case "Burgers":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.burgers_thumbnail);
                break;
            case "Beans, Grains, Legume":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.bean_thumbnail);
                break;
            case "Bread, Rolls, Muffins":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.bread_roll_thumbnail);
                break;
            case "Cakes, Cupcakes":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.cakes_thumbnail);
                break;
            case "Cocktail":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.cocktails_thumbnail);
                break;
            case "Desserts":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.desserts_thumbail);
                break;
            case "Non-alcoholic drinks":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.nonalcoholicdrink_thumbnail);
                break;
            case "Pasta, Noodles":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.pasta_thumbnail);
                break;
            case "Pies":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.pies_thumbnail);
                break;
            case "Pizza":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.pizzas_thumbnail);
                break;
            case "Salad":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.salad_thumbnail);
                break;
            case "Sandwiches":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.sandwiches_thumbnail);
                break;
            case "Soups, Stews":
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.drawable.soups_thumnail);
                break;
            default:
                ((ViewHolder) holder).mRecipeTypeImage.setImageResource(R.mipmap.ic_launcher);
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mRecipeType;
        public final ImageView mRecipeTypeImage;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.tvIngredientName);
            mRecipeType = (TextView) view.findViewById(R.id.tvIngredientAmount);
            mRecipeTypeImage = (ImageView) view.findViewById(R.id.imgRecipeRowQuickPic);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}