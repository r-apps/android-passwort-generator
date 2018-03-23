package de.rubeen.apps.android.passwortgenerator.logic;

import java.time.LocalDateTime;

public class PasswordObject implements IPasswordObject {
    long id;
    String title, description, password;
    LocalDateTime creationDateTime;

    public PasswordObject(String password, LocalDateTime creationDateTime) {
        this.password = password;
        this.creationDateTime = creationDateTime;
    }

    public PasswordObject(String password, LocalDateTime creationDateTime, long id) {
        this.password = password;
        this.creationDateTime = creationDateTime;
        this.id = id;
    }

    public PasswordObject(String title, String description, String password, LocalDateTime creationDateTime, long id) {
        this.title = title;
        this.description = description;
        this.password = password;
        this.creationDateTime = creationDateTime;
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
