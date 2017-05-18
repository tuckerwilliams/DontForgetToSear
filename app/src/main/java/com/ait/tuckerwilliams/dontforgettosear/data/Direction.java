package com.ait.tuckerwilliams.dontforgettosear.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Direction extends RealmObject {

    @PrimaryKey
    private String id;

    private String mDescription;

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

}
