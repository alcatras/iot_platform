package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.models.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 15.01.17.
 */
public abstract class Mapper<T extends Model> {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected DatabaseHelper databaseHelper;
    Logger logger = Logger.getLogger(Mapper.class.getName());

    Mapper(DatabaseHelper helper, Class<T> clazz) throws SQLException {

        this.databaseHelper = helper;

        if (!helper.checkIfTableExists(getTableName(clazz))) {
            throw new SQLException("Table " + getTableName(clazz) + " does not exists.");
        }

        MapperRegistry.getInstance().register(clazz, this);
    }

    public abstract void insert(T model);

    public abstract void update(T model);

    public abstract void delete(T model);

    public abstract T get(int id);

    protected abstract T createModel(ResultSet resultSet);

    protected List<T> createModels(ResultSet resultSet) {
        List<T> models = new ArrayList<T>();

        try {
            while (resultSet.next()) {
                T model = createModel(resultSet);
                model.setId(resultSet.getInt("id"));
                // TODO: something
                //model.setDateCreated(LocalDateTime.parse(resultSet.getString("created_at"), formatter));
                //model.setDateUpdated(LocalDateTime.parse(resultSet.getString("updated_at"), formatter));

                models.add(model);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Invalid result set returned: " + e.getMessage(), e);
        }

        return models;
    }

    protected String getTableName(Class clazz) {
        return clazz.getSimpleName().toLowerCase() + "s";
    }
}