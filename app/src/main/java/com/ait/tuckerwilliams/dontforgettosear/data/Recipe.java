package com.ait.tuckerwilliams.dontforgettosear.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tuckerwilliams on 4/17/17.
 */

public class Recipe extends RealmObject {// implements Comparable<Recipe> {

    @PrimaryKey
    private String id;

    private String recipeName;
    private String recipeType;
    private String recipeServings;
    private RealmList<Ingredient> mList;

    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public RealmList<Ingredient> getmList() {
        return mList;
    }

    public void setmList(RealmList<Ingredient> mList) {
        this.mList = mList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.recipeServings = recipeServings;
    }
}

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public int compareTo(@NonNull Recipe otherRecipe) {
//        return this.recipeName.compareTo(otherRecipe.getRecipeName());
//    }
