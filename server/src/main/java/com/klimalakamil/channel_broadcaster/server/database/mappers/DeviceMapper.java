package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public class DeviceMapper extends Mapper<Device> {

    private Logger logger;

    protected DeviceMapper(DatabaseHelper helper) throws SQLException {
        super(helper, Device.class);

        logger = Logger.getLogger(this.getClass().getCanonicalName());
    }

    @Override
    public void insert(Device model) {

        databaseHelper.executeQuery("INSERT INTO " + getTableName(Device.class) +
                "(id, user_id, name, salt, type, updated_at) values (" +
                model.getId() + ", " +
                model.getUser().getId() + ", " +
                model.getName() + ", " +
                model.getType() + ", " +
                model.getDateCreated() + ", " +
                model.getDateUpdated() +
                ");"
        );
    }

    @Override
    public void update(Device model) {

    }

    @Override
    public void delete(Device model) {
        databaseHelper.executeQuery("DELETE FROM " + getTableName(Device.class) +
                " WHERE id = " + model.getId() + ";"
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
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(Device.class) +
                " WHERE id = " + id + " LIMIT 1");

        List<Device> devices = createModels(resultSet);
        return devices.size() > 0 ? devices.get(0) : null;
    }

    public Device get(String name) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(Device.class) +
                " WHERE username = " + name + " LIMIT 1");

        List<Device> devices = createModels(resultSet);
        return devices.size() > 0 ? devices.get(0) : null;
    }
}
