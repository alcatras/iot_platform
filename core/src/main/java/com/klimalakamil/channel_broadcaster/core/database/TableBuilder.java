package com.klimalakamil.channel_broadcaster.core.database;

import com.klimalakamil.channel_broadcaster.core.database.models.AbstractModel;

import javax.xml.crypto.Data;

/**
 * Created by kamil on 15.01.17.
 */
public abstract class TableBuilder {

    public abstract void create(DatabaseHelper databaseHelper);

    public abstract void drop(DatabaseHelper databaseHelper);
}
