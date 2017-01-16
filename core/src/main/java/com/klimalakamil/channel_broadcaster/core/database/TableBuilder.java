package com.klimalakamil.channel_broadcaster.core.database;

/**
 * Created by kamil on 15.01.17.
 */
public abstract class TableBuilder {

    public abstract void create(DatabaseHelper databaseHelper);

    public abstract void drop(DatabaseHelper databaseHelper);
}
