package com.klimalakamil.iot_platform.server.database.models;

/**
 * Created by kamil on 17.01.17.
 */
public class Device extends Model {

    public static final int TYPE_CONTROLLER = 0x1;
    public static final int TYPE_MONITOR = 0x2;
    public static final int TYPE_DRONE = 0x4;

    private User user;
    private String name;
    private int type;

    public Device() {
        super();
    }

    public Device(User user, String name, int type) {
        super();
        this.user = user;
        this.name = name;
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
