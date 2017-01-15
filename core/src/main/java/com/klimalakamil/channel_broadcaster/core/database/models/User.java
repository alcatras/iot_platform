package com.klimalakamil.channel_broadcaster.core.database.models;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;

/**
 * Created by kamil on 15.01.17.
 */
public class User extends AbstractModel {

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

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setPasswordDigest(byte[] passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public byte[] getPasswordDigest() {
        return passwordDigest;
    }

    public void setPassword(char[] password) {
        salt = PasswordHelper.generateSalt();
        passwordDigest = PasswordHelper.createHash(password, salt);
    }

    public byte[] getSalt() {
        return salt;
    }
}
