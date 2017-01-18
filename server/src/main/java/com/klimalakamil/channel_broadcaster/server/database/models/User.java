package com.klimalakamil.channel_broadcaster.server.database.models;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;

/**
 * Created by kamil on 15.01.17.
 */
public class User extends Model {

    private String username;
    private byte[] passwordDigest;
    private byte[] salt;

    public User() {

    }

    public User(int id, String username, char[] password) {
        super(id);
        this.username = username;
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(byte[] passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public void setPassword(char[] password) {
        salt = PasswordHelper.generateSalt();
        passwordDigest = PasswordHelper.createHash(password, salt);
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
