package com.klimalakamil.channel_broadcaster.core.database.models;

import com.klimalakamil.channel_broadcaster.core.database.TableBuilder;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by kamil on 15.01.17.
 */
public abstract class AbstractModel {
    private int id;

    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    public AbstractModel() {

    }

    public AbstractModel(int id) {
        this.id = id;
        setDateCreated();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated() {
        this.dateUpdated = LocalDateTime.now();
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setDateCreated() {
        setDateCreated(LocalDateTime.now());
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
        this.dateUpdated = dateCreated;
    }
}
