package com.ait.tuckerwilliams.dontforgettosear.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Grocery extends RealmObject {

    @PrimaryKey
    private String id;

    private boolean isCheckedOff;
    private String mDescription, mName;

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
