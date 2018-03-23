package de.rubeen.apps.android.passwortgenerator;

import android.content.Context;
import android.util.Log;
import de.rubeen.apps.android.passwortgenerator.Persistence.PasswordHistoryDatabase;
import de.rubeen.apps.android.passwortgenerator.logic.IPasswordObject;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static History instance;
    private PasswordHistoryDatabase database;

    private History() {
    }

    static public History getInstance() {
        if (instance == null)
            instance = new History();
        return instance;
    }

    private final List<IPasswordObject> passwordObjects = new ArrayList<>(100);

    public List<IPasswordObject> getPasswordObjects() {
        return passwordObjects;
    }

    public void addPasswordObject(IPasswordObject object, Context context) {
        if (object.getTitle() == null)
            object.setTitle(context.getResources().getString(R.string.title_noPasswordName) + " #" + passwordObjects.size());
        try {
            database.put(object);
            passwordObjects.add(0, addPasswordObjectChecks(object));

        } catch (DuplicationPasswordError duplicationPasswordError) {
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
        } catch (DuplicationPasswordError duplicationPasswordError) {
            Log.e("DuplicateError", duplicationPasswordError.getMessage());
        }
    }

    public PasswordHistoryDatabase getDatabase(Context context) {
        if (database == null)
            database = new PasswordHistoryDatabase(context);
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
            throw new DuplicationPasswordError("Password already exists.");
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
