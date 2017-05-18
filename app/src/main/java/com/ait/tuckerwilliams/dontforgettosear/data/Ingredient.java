package com.ait.tuckerwilliams.dontforgettosear.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

//TODO: Change this to class DataContainer, then implement interfaces Ingredient and Step?

public class Ingredient extends RealmObject { //implements comparable.

    @PrimaryKey
    private String id;

    private String mDescription, mName;
    private boolean isCheckedOff;

    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDescription() {
        return mDescription;
    }
    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCheckedOff() {
        return isCheckedOff;
    }

    public void setCheckedOff(boolean checkedOff) {
        isCheckedOff = checkedOff;
    }
}

//
//    public static final int INGREDIENT_TYPE = 0;
//    public static final int STEP_TYPE = 1;
//    public static final int NO_TYPE = 2;


//    public static int spinnerIndexToType(int index) {
//        if (index == 0) return INGREDIENT_TYPE; else return STEP_TYPE;
//    }

//Todo: Ask Peter about this.
//https://github.com/realm/realm-java/issues/964
//Must remove all NON-getter setter methods from this class? How can I compare ingredients then?

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public int compareTo(@NonNull Ingredient another) {
//        int typeThis = this.mType;
//        int typeAnother = another.getmType();
//
//        if (Integer.compare(typeThis, typeAnother) == 0)
//            return this.mName.compareTo(another.getmName());
//        else
//            return Integer.compare(typeThis, typeAnother);
//    }