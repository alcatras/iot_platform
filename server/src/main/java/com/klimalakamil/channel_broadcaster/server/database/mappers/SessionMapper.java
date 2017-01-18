package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public class SessionMapper extends Mapper<Session> {

    private Logger logger;

    SessionMapper(DatabaseHelper helper) throws SQLException {
        super(helper, Session.class);

        logger = Logger.getLogger(SessionMapper.class.getName());
    }

    @Override
    public void insert(Session model) {

        databaseHelper.executeQuery("INSERT INTO " + getTableName(Session.class) +
                "(id, device_id, ip, control_port, valid_before, created_at, updated_at) values (" +
                model.getId() + ", " +
                model.getDevice().getId() + ", " +
                model.getAddress().getHostAddress() + ", " +
                model.getValidBefore() + ", " +
                model.getDateCreated() + ", " +
                model.getDateUpdated() +
                ")"
        );
    }

    @Override
    public void update(Session model) {

    }

    @Override
    public void delete(Session model) {
        databaseHelper.executeQuery("DELETE FROM " + getTableName(Session.class) +
                " WHERE id = " + model.getId() + ";"
        );
    }

    @Override
    protected Session createModel(ResultSet resultSet) {
        Session session = new Session();

        try {
            DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Session.class);
            session.setDevice(deviceMapper.get(resultSet.getInt("device_id")));

            session.setAddress(InetAddress.getByName(resultSet.getString("ip")));
            session.setValidBefore(LocalDateTime.parse(resultSet.getString("valid_before")));

        } catch (SQLException | UnknownHostException e) {
            logger.log(Level.WARNING, "Unable to parse Session data: " + e.getMessage(), e);
        }

        return session;
    }

    @Override
    public Session get(int id) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(Session.class) +
                " WHERE id = " + id + " LIMIT 1");

        List<Session> sessions = createModels(resultSet);
        return sessions.size() > 0 ? sessions.get(0) : null;
    }

    public Session get(Device device) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(Session.class) +
                " WHERE device_id = " + device.getId() + " LIMIT 1");

        List<Session> sessions = createModels(resultSet);
        return sessions.size() > 0 ? sessions.get(0) : null;
    }
}
