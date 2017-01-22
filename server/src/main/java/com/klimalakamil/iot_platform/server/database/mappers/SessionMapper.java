package com.klimalakamil.iot_platform.server.database.mappers;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.server.database.DatabaseHelper;
import com.klimalakamil.iot_platform.server.database.models.Device;
import com.klimalakamil.iot_platform.server.database.models.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public class SessionMapper extends Mapper<Session> {

    private Logger logger = Logger.getLogger(SessionMapper.class.getName());

    public SessionMapper(DatabaseHelper helper) throws SQLException {
        super(helper, Session.class);
    }

    @Override
    public void insert(Session model) {

        databaseHelper.executeQuery("INSERT INTO " + getTableName(Session.class) +
                "(device_id, ip, control_port, valid_before, " + getInsertQueryDatesNames() + ") values ('" +
                model.getDevice().getId() + "', '" +
                model.getAddress().getHostAddress() + "', '" +
                model.getControlPort() + "', '" +
                model.getValidTo().format(formatter) + "', " +
                getInsertQueryDates(model) +
                ")"
        );
    }

    @Override
    public void update(Session model) {
        databaseHelper.executeQuery("UPDATE " + getTableName(Session.class) +
                " SET " +
                "device_id = '" + model.getDevice().getId() + "', " +
                "ip = '" + model.getAddress().getHostAddress() + "', " +
                "control_port = '" + model.getControlPort() + "', " +
                "valid_before = '" + model.getValidTo().format(formatter) + "', " +
                getUpdateQueryDates(model) +
                " WHERE " +
                "id = '" + model.getId() + "'"
        );
    }

    @Override
    public void delete(Session model) {
        databaseHelper.executeQuery("DELETE FROM " + getTableName(Session.class) +
                " WHERE id = '" + model.getId() + "';"
        );
    }

    @Override
    protected Session createModel(ResultSet resultSet) {
        Session session = new Session();

        try {
            DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
            session.setDevice(deviceMapper.get(resultSet.getInt("device_id")));

            session.setAddress(InetAddress.getByName(resultSet.getString("ip")));
            session.setControlPort(resultSet.getInt("control_port"));
            session.setValidTo(LocalDateTime.parse(resultSet.getString("valid_before"), formatter));

        } catch (SQLException | UnknownHostException e) {
            logger.log(Level.WARNING, "Unable to parse Session data: " + e.getMessage(), e);
        }

        return session;
    }

    @Override
    public Session get(int id) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(Session.class) +
                " WHERE id = '" + id + "' LIMIT 1");

        return getOne(resultSet);
    }

    public Session get(Device device) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(Session.class) +
                " WHERE device_id = '" + device.getId() + "' LIMIT 1");

        return getOne(resultSet);
    }

    public Session get(ClientConnection connection) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(Session.class) +
                " WHERE ip = '" + connection.getAddress().getHostAddress() + "' AND control_port = '" + connection.getPort() + "' LIMIT 1");

        return getOne(resultSet);
    }
}
