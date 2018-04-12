package de.rubeen.apps.android.passwortgenerator.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.rubeen.apps.android.passwortgenerator.logic.IPasswordObject;
import de.rubeen.apps.android.passwortgenerator.logic.PasswordObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PasswordCatalogDatabase {
    private static final String LOG_TAG = PasswordCatalogDatabase.class.getSimpleName();
    private final PasswordCatalogDatabaseHelper databaseHelper;
    private final String[] columns = {
            PasswordCatalogDatabaseHelper.COLUMN_ID, PasswordCatalogDatabaseHelper.COLUMN_TITLE,
            PasswordCatalogDatabaseHelper.COLUMN_DESCRIPTION, PasswordCatalogDatabaseHelper.COLUMN_PASSWORD,
            PasswordCatalogDatabaseHelper.COLUMN_CREATION
    };

    private SQLiteDatabase database;

    public PasswordCatalogDatabase(Context context) {
        this.databaseHelper = new PasswordCatalogDatabaseHelper(context);
        Log.d(LOG_TAG, "DBHelper created");
    }

    public void open() {
        Log.d(LOG_TAG, "Reference is being called");
        database = databaseHelper.getWritableDatabase();
        Log.d(LOG_TAG, "got dbRef: " + database.getPath());
    }

    public void close() {
        databaseHelper.close();
        Log.d(LOG_TAG, "database closed.");
    }

    public IPasswordObject put(IPasswordObject passwordObject) {
        ContentValues values = new ContentValues();
        values.put(PasswordCatalogDatabaseHelper.COLUMN_TITLE, passwordObject.getTitle());
        values.put(PasswordCatalogDatabaseHelper.COLUMN_DESCRIPTION, passwordObject.getDescription());
        values.put(PasswordCatalogDatabaseHelper.COLUMN_PASSWORD, passwordObject.getPassword());
        values.put(PasswordCatalogDatabaseHelper.COLUMN_CREATION, passwordObject.getCreationDateTime().toString());

        long insertId = database.insert(PasswordCatalogDatabaseHelper.TABLE_PASSWORDS, null, values);

        Cursor cursor = database.query(PasswordCatalogDatabaseHelper.TABLE_PASSWORDS, columns,
                PasswordCatalogDatabaseHelper.COLUMN_ID + "=" + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        IPasswordObject result = cursorToIPasswordObject(cursor);
        cursor.close();
        return result;
    }

    public void delete(IPasswordObject passwordObject) {
        long id = passwordObject.getId();

        database.delete(PasswordCatalogDatabaseHelper.TABLE_PASSWORDS, PasswordCatalogDatabaseHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Item removed. ID=" + id + " | pw=" + passwordObject.getPassword());
    }

    public List<IPasswordObject> getAll() {
        List<IPasswordObject> passwordObjects = new ArrayList<>();
        Cursor cursor = database.query(PasswordCatalogDatabaseHelper.TABLE_PASSWORDS, columns, null,
                null, null, null, PasswordCatalogDatabaseHelper.COLUMN_CREATION + " DESC", "100");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            passwordObjects.add(cursorToIPasswordObject(cursor));
            Log.d(LOG_TAG, "GET: " + passwordObjects.get(passwordObjects.size() - 1).getPassword());
            cursor.moveToNext();
        }
        cursor.close();
        return passwordObjects;
    }

    private class PasswordCatalogDatabaseHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "passwordApp1.db";
        private static final int DB_VERSION = 1;

        private static final String TABLE_PASSWORDS = "passwords_catalog";

        private static final String COLUMN_ID = "_id";
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_PASSWORD = "password";
        private static final String COLUMN_CREATION = "creation";

        private static final String SQL_CREATE = "CREATE TABLE " + TABLE_PASSWORDS
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_CREATION + " TEXT)";

        private final String LOG_TAG = PasswordCatalogDatabaseHelper.class.getSimpleName();


        public PasswordCatalogDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            Log.d(LOG_TAG, "DB-Helper created database: " + getDatabaseName());
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            Log.d(LOG_TAG, "SQL: " + SQL_CREATE);
            database.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    private IPasswordObject cursorToIPasswordObject(Cursor cursor) {
        final int idId = cursor.getColumnIndex(PasswordCatalogDatabaseHelper.COLUMN_ID);
        final int idTitle = cursor.getColumnIndex(PasswordCatalogDatabaseHelper.COLUMN_TITLE);
        final int idDescription = cursor.getColumnIndex(PasswordCatalogDatabaseHelper.COLUMN_DESCRIPTION);
        final int idPassword = cursor.getColumnIndex(PasswordCatalogDatabaseHelper.COLUMN_PASSWORD);
        final int idCreation = cursor.getColumnIndex(PasswordCatalogDatabaseHelper.COLUMN_CREATION);

        final long id = cursor.getLong(idId);
        final String title = cursor.getString(idTitle);
        final String description = cursor.getString(idDescription);
        final String password = cursor.getString(idPassword);
        final LocalDateTime creation = LocalDateTime.parse(cursor.getString(idCreation));

        return new PasswordObject(title, description, password, creation, id);
    }
}
