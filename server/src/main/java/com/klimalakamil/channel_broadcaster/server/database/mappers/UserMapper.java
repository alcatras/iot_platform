package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 15.01.17.
 */
public class UserMapper extends Mapper<User> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public UserMapper(DatabaseHelper helper) throws SQLException {
        super(helper, User.class);
    }

    @Override
    public void insert(User model) {

        Base64.Encoder encoder = Base64.getEncoder();

        String encodedSalt = encoder.encodeToString(model.getSalt());
        String encodedDigest = encoder.encodeToString(model.getPasswordDigest());

        databaseHelper.executeQuery("INSERT INTO " + getTableName(User.class) +
                "(username, salt, password_digest, created_at, updated_at) values ('" +
                model.getUsername() + "', '" +
                encodedSalt + "', '" +
                encodedDigest + "', '" +
                model.getDateCreated() + "', '" +
                model.getDateUpdated() + "'" +
                ");"
        );
    }

    @Override
    public void update(User model) {

    }

    @Override
    public void delete(User model) {
        databaseHelper.executeQuery("DELETE FROM " + getTableName(User.class) +
                " WHERE id = " + model.getId() + ";"
        );
    }

    @Override
    protected User createModel(ResultSet resultSet) {
        Base64.Decoder decoder = Base64.getDecoder();
        User user = new User();

        try {
            user.setUsername(resultSet.getString("username"));
            user.setPasswordDigest(decoder.decode(resultSet.getString("password_digest")));
            user.setSalt(decoder.decode(resultSet.getString("salt")));
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to parse User data: " + e.getMessage(), e);
        }

        return user;
    }

    @Override
    public User get(int id) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(User.class) +
                " WHERE id = " + id + " LIMIT 1");

        List<User> users = createModels(resultSet);
        return users.size() > 0 ? users.get(0) : null;
    }

    public User get(String username) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT * FROM " + getTableName(User.class) +
                " WHERE username = '" + username + "' LIMIT 1");

        List<User> users = createModels(resultSet);
        return users.size() > 0 ? users.get(0) : null;
    }
}
