package com.ait.tuckerwilliams.dontforgettosear.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Recipe extends RealmObject {// implements Comparable<Recipe> {

    @PrimaryKey
    private String id;

    private String recipeName;
    private String recipeType;
    private String recipeServings;

    private String recipeImg;

    private boolean pinned;

    private RealmList<Ingredient> mIngredientList;
    private RealmList<Direction> mDirectionList;

    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public RealmList<Ingredient> getmIngredientList() {
        return mIngredientList;
    }

    public void setmIngredientList(RealmList<Ingredient> mList) {
        this.mIngredientList = mList;
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

    public RealmList<Direction> getmDirectionList() {
        return mDirectionList;
    }

    public void setmDirectionList(RealmList<Direction> mDirectionList) {
        this.mDirectionList = mDirectionList;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        this.recipeImg = recipeImg;
    }
}

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public int compareTo(@NonNull Recipe otherRecipe) {
//        return this.recipeName.compareTo(otherRecipe.getRecipeName());
//    }
