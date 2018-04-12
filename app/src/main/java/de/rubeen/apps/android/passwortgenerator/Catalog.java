package de.rubeen.apps.android.passwortgenerator;

import android.content.Context;
import android.util.Log;
import de.rubeen.apps.android.passwortgenerator.Persistence.PasswordCatalogDatabase;
import de.rubeen.apps.android.passwortgenerator.logic.IPasswordObject;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private static Catalog instance;
    private final List<IPasswordObject> passwordObjects = new ArrayList<>(100);
    private PasswordCatalogDatabase database;

    private Catalog() {
    }

    static public Catalog getInstance() {
        if (instance == null)
            instance = new Catalog();
        return instance;
    }

    public List<IPasswordObject> getPasswordObjects() {
        return passwordObjects;
    }

    public void addPasswordObject(IPasswordObject object, Context context) {
        if (object.getTitle() == null)
            object.setTitle(context.getResources().getString(R.string.title_noPasswordName) + " #" + passwordObjects.size());
        try {
            database.put(object);
            passwordObjects.add(0, addPasswordObjectChecks(object));

        } catch (Catalog.DuplicationPasswordError duplicationPasswordError) {
            Log.e("DuplicateError", duplicationPasswordError.getMessage());
        }
    }

    public void resetPasswordObjects() {
        passwordObjects.clear();
    }

    private void addPasswordObjectLocal(IPasswordObject object, Context context) {
        if (object.getTitle() == null)
            object.setTitle(context.getResources().getString(R.string.title_noPasswordName) + " #" + passwordObjects.size());
        try {
            passwordObjects.add(addPasswordObjectChecks((object)));
        } catch (Catalog.DuplicationPasswordError duplicationPasswordError) {
            Log.e("DuplicateError", duplicationPasswordError.getMessage());
        }
    }

    public PasswordCatalogDatabase getDatabase(Context context) {
        if (database == null)
            database = new PasswordCatalogDatabase(context);
        return database;
    }

    public void addPasswordObjects(List<IPasswordObject> objects, Context context) {
        objects.forEach(element -> addPasswordObjectLocal(element, context));
    }

    public long getMaxId() {
        long max = 0;
        for (IPasswordObject passwordObject : passwordObjects)
            max = Math.max(passwordObject.getId(), max);
        return max;
    }

    private IPasswordObject addPasswordObjectChecks(IPasswordObject object) throws DuplicationPasswordError {
        if (passwordObjects.contains(object)) {
            throw new Catalog.DuplicationPasswordError("Password already exists.");
        }
        boolean idExists = false;
        for (IPasswordObject passwordObject : passwordObjects) {
            idExists = passwordObject.getId() == object.getId();
        }
        if (idExists)
            object.setId(getMaxId() + 1);
        return object;
    }

    private class DuplicationPasswordError extends Throwable {
        private String message;

        public DuplicationPasswordError(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}