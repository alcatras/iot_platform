package com.klimalakamil.channel_broadcaster.core.database.mappers;

import com.klimalakamil.channel_broadcaster.core.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.core.database.TableBuilder;
import com.klimalakamil.channel_broadcaster.core.database.models.AbstractModel;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by kamil on 15.01.17.
 */
public abstract class AbstractDatabaseMapper<T extends AbstractModel> {

    protected DatabaseHelper databaseHelper;

    AbstractDatabaseMapper(DatabaseHelper helper, Class<T> clazz) {

        this.databaseHelper = helper;

        if (!helper.checkIfTableExists(clazz.getCanonicalName())) {
            getTableBuilder().create(helper);
        }
    }

    public abstract void insert(T model);

    public abstract void update(T model);

    public abstract void delete(T model);

    public abstract T get(int id);

    protected abstract TableBuilder getTableBuilder();

    protected abstract List<T> createModels(ResultSet resultSet);

    protected String getTableName(Class clazz) {
        return clazz.getCanonicalName();
    }
}