package com.klimalakamil.channel_broadcaster.core.database.mappers;

import com.klimalakamil.channel_broadcaster.core.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.core.database.TableBuilder;
import com.klimalakamil.channel_broadcaster.core.database.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 15.01.17.
 */
public class UserMapper extends AbstractDatabaseMapper<User> {

    private Logger logger;

    protected UserMapper(DatabaseHelper helper, Class<User> clazz) {
        super(helper, clazz);

        logger = Logger.getLogger(this.getClass().getCanonicalName());
    }

    @Override
    public void insert(User model) {

        Base64.Encoder encoder = Base64.getEncoder();

        String encodedSalt = encoder.encodeToString(model.getSalt());
        String encodedDigest = encoder.encodeToString(model.getPasswordDigest());

        databaseHelper.executeQuery("INSERT INTO " + getTableName(User.class) +
                "(id, username, psw_digest, salt, created_at, updated_at) values (" +
                model.getId() + ", " +
                model.getUsername() + ", " +
                encodedSalt + ", " +
                encodedDigest + ", " +
                model.getDateCreated() + ", " +
                model.getDateUpdated() +
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
    protected List<User> createModels(ResultSet resultSet) {
        Base64.Decoder decoder = Base64.getDecoder();
        List<User> result = new ArrayList<>();

        try {
            while (resultSet.next()) {
                User user = new User();

                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPasswordDigest(decoder.decode(resultSet.getString("psw_digest")));
                user.setSalt(decoder.decode(resultSet.getString("salt")));
                user.setDateCreated(LocalDateTime.parse(resultSet.getString("created_at")));
                user.setDateUpdated(LocalDateTime.parse(resultSet.getString("updated_at")));

                result.add(user);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public User get(int id) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(User.class) +
                " WHERE id = " + id);

        List<User> users = createModels(resultSet);
        return users.size() > 0 ? users.get(0) : null;
    }

    public User get(String username) {
        ResultSet resultSet = databaseHelper.executeQueryForResult("SELECT FROM " + getTableName(User.class) +
                " WHERE username = " + username);

        List<User> users = createModels(resultSet);
        return users.size() > 0 ? users.get(0) : null;
    }

    @Override
    protected TableBuilder getTableBuilder() {
        return new TableBuilder() {
            @Override
            public void create(DatabaseHelper databaseHelper) {
                databaseHelper.executeQuery("CREATE TABLE " + User.class.getCanonicalName() + " ( " +
                        "id integer primary key," +
                        "username varchar(255)," +
                        "psw_digest varchar(255)," +
                        "salt varchar(255)," +
                        "created_at datetime," +
                        "updated_at datetime" +
                        ");"
                );
            }

            @Override
            public void drop(DatabaseHelper databaseHelper) {
                databaseHelper.executeQuery("DROP TABLE " + User.class.getCanonicalName() + ";");
            }
        };
    }
}
