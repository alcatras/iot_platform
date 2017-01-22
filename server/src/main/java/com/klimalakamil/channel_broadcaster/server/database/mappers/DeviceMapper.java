package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public class DeviceMapper extends Mapper<Device> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public DeviceMapper(DatabaseHelper helper) throws SQLException {
        super(helper, Device.class);
    }

    // TODO: switch to JDBC executeUpdate etc... methods
    @Override
    public void insert(Device model) {

        databaseHelper.executeQuery("INSERT INTO " + getTableName(Device.class) +
                "(user_id, name, type, " + getInsertQueryDatesNames() + ") values ('" +
                model.getUser().getId() + "', '" +
                model.getName() + "', '" +
                model.getType() + "', " +
                getInsertQueryDates(model) +
                ");"
        );
    }

    @Override
    public void update(Device model) {

    }

    @Override
    public void delete(Device model) {
        databaseHelper.executeQuery("DELETE FROM " + getTableName(Device.class) +
                " WHERE id = '" + model.getId() + "'"
        );
    }

    @Override
    protected Device createModel(ResultSet resultSet) {
        Device device = new Device();

        try {
            UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
            device.setUser(userMapper.get(resultSet.getInt("user_id")));

            device.setName(resultSet.getString("name"));
            device.setType(resultSet.getInt("type"));
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to parse Device data: " + e.getMessage(), e);
        }

        return device;
    }

    @Override
    public Device get(int id) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(Device.class) +
                " WHERE id = '" + id + "' LIMIT 1");

        return getOne(resultSet);
    }

    public Device get(User user, String name) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(Device.class) +
                " WHERE name = '" + name + "' AND user_id = '" + user.getId() + "' LIMIT 1");

        return getOne(resultSet);
    }
}
