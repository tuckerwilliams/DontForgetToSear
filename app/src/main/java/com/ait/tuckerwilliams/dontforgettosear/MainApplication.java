package com.ait.tuckerwilliams.dontforgettosear;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        // Configure Realm for the application
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("examples.realm").schemaVersion(0).deleteRealmIfMigrationNeeded()
                .build();

        //Use the below line to clear RealmDB. Otherwise, keep commented.
        //Realm.deleteRealm(realmConfiguration); //Deletes the realm,
        // use when you want a clean slate for dev/etc

        // Make this Realm the default
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
