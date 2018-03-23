package de.rubeen.apps.android.passwortgenerator.logic;

import java.time.LocalDateTime;

public interface IPasswordObject {
    String getTitle();
    String getDescription();
    String getPassword();
    LocalDateTime getCreationDateTime();
    long getId();
    void setTitle(String title);
    void setDescription(String description);

    void setId(long id);
}
