package com.ait.tuckerwilliams.dontforgettosear.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by tuckerwilliams on 4/14/17.
 */

public final class DummyData {

    public static final int INGREDIENT_TYPE = 0;
    public static final int STEP_TYPE = 1;

    public static List<Ingredient> getData() {
        List<Ingredient> list = new ArrayList<>();
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setmName("BAcon");
        ingredient1.setmDescription("Seared");
        ingredient1.setmType(0);
        list.add(ingredient1);

        return list;
    }

    public static ArrayList<Ingredient> getIngredientList() {
        ArrayList<Ingredient> list = new ArrayList<>();

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setmName("BAcon");
        ingredient1.setmDescription("Seared");
        ingredient1.setmType(0);
        list.add(ingredient1);

        return list;
    }

    public static ArrayList<Recipe> getRecipeDataList(Context context) {
        ArrayList<Ingredient> list = getIngredientList();

        ArrayList<Recipe> recipe = new ArrayList<>();

        recipe.add(new Recipe());
        recipe.add(new Recipe());
        recipe.add(new Recipe());
        recipe.add(new Recipe());
        return recipe;
    }
}
