package de.rubeen.apps.android.passwortgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import de.rubeen.apps.android.passwortgenerator.Persistence.PasswordCatalogDatabase;
import de.rubeen.apps.android.passwortgenerator.Persistence.PasswordHistoryDatabase;
import de.rubeen.apps.android.passwortgenerator.logic.IPasswordObject;
import de.rubeen.apps.android.passwortgenerator.logic.PasswordGenerator;
import de.rubeen.apps.android.passwortgenerator.logic.PasswordObject;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    //TODO: Expand

    private final History history = History.getInstance();
    private final Catalog catalog = Catalog.getInstance();
    private View layout_home, layout_history, layout_settings, layout_catalog;
    private String mToolbarTitle;
    private BottomNavigationView navigation;
    private EditText passwordBox, saveBox;
    private PasswordGenerator generator = new PasswordGenerator();
    private PasswordHistoryDatabase databaseHistory;
    private PasswordCatalogDatabase databaseCatalog;
    private HistoryAdapter historyAdapter;
    private CatalogAdapter catalogAdapter;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupStrings();
        setupWidgets();
        setupActionBars();
        setupView();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupDatabase();
        //TEST:
//        PasswordGenerator generator = new PasswordGenerator();
//        for (int i = 0; i < 10; i++) {
//            databaseHistory.put(generator.generatePassword(i));
//        }

        loadContents();
    }

    private void setupActionBars() {
        setSupportActionBar(setupToolbar(findViewById(R.id.mainActivityToolbar)));
        setupCABHistory();
    }

    private void setupCABHistory() {
        ListView listView = findViewById(R.id.expandedHistoryList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                getMenuInflater().inflate(R.menu.history_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.cab_delete:
                        SparseBooleanArray touchedPositions = listView.getCheckedItemPositions();
                        for (int i = 0; i < touchedPositions.size(); i++) {
                            if (touchedPositions.valueAt(i)) {
                                int position = touchedPositions.keyAt(i);
                                IPasswordObject passwordObject = (IPasswordObject) listView.getItemAtPosition(position);
                                databaseHistory.delete(passwordObject);
                            }
                        }
                        history.resetPasswordObjects();
                        loadContents();
                        actionMode.finish();
                        collapseHistory();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }

    private void collapseHistory() {
        int count = historyAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ExpandableListView list = findViewById(R.id.expandedHistoryList);
            list.collapseGroup(i);
        }

        count = catalogAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ExpandableListView listView = findViewById(R.id.expandedCatalogList);
            listView.collapseGroup(i);
        }
    }

    private void setupDatabase() {
        databaseHistory = history.getDatabase(this);
        databaseHistory.open();

        databaseCatalog = catalog.getDatabase(this);
        databaseCatalog.open();
    }

    private void loadContents() {
        history.addPasswordObjects(databaseHistory.getAll(), this);
        catalog.addPasswordObjects(databaseCatalog.getAll(), this);
    }

    private Toolbar setupToolbar(Toolbar toolbar) {
        toolbar.setTitle(mToolbarTitle);
        return toolbar;
    }

    private void setupStrings() {
        mToolbarTitle = getResources().getString(R.string.app_name_heading);
    }

    private void setupWidgetListener() {
        passwordBox.setKeyListener(null);
        passwordBox.setOnClickListener(copyPasswordListener());
        findViewById(R.id.contentHome_passwordCopy).setOnClickListener(copyPasswordListener());

        findViewById(R.id.contentHome_passwordCreate).setOnClickListener(view -> fillPasswordBox());
        findViewById(R.id.contentHome_btnStart).setOnClickListener(view -> {
            fillPasswordBox();
            new Thread(() -> {
                try {
                    synchronized (this) {
                        this.wait(700);
                    }
                } catch (InterruptedException e) {
                    Log.e("interruption exception", e.getMessage());
                }
                view.post(() -> {
                    findViewById(R.id.contentHome_btnStart).setVisibility(View.GONE);
                    findViewById(R.id.contentHome_generatedPasswordLayout).setVisibility(View.VISIBLE);
                });
            }).start();
            view.animate().rotationBy(360).setDuration(800).start();
        });
        findViewById(R.id.contentHome_passwordSave).setOnClickListener(view -> {
            findViewById(R.id.content_new_password).setVisibility(View.VISIBLE);
        });
        findViewById(R.id.content_new_password_save).setOnClickListener(view -> {
            savePassword();
            findViewById(R.id.content_new_password).setVisibility(View.GONE);
        });
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            layout_home.setVisibility(View.GONE);
            layout_history.setVisibility(View.GONE);
            layout_settings.setVisibility(View.GONE);
            layout_catalog.setVisibility(View.GONE);

            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    layout_home.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_generatedPasswords:
                    layout_history.setVisibility(View.VISIBLE);
                    collapseHistory();
                    return true;
                case R.id.navigation_catalog:
                    layout_catalog.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_settings:
                    layout_settings.setVisibility(View.VISIBLE);
                    //Intent intent = new Intent();
                    //intent.setClassName(this, Settings.class.getSimpleName());
                    //startActivity(intent);
                    //setContentView(R.layout.content_settings);
                    return true;
            }
            return false;
        });
    }

    private void savePassword() {
        String title = ((EditText) findViewById(R.id.content_new_password_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.content_new_password_description)).getText().toString();
        String password = ((EditText) findViewById(R.id.content_new_password_password)).getText().toString();
        PasswordObject object = new PasswordObject(title, description, password, LocalDateTime.now(), -1);
        Catalog.getInstance().addPasswordObject(object, this);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (findViewById(R.id.contentHome_generatedPasswordLayout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.contentHome_generatedPasswordLayout).setVisibility(View.GONE);
            findViewById(R.id.contentHome_btnStart).setVisibility(View.VISIBLE);
        } else super.onBackPressed();
    }

    private OnClickListener copyPasswordListener() {
        return view -> {
            Toast.makeText(this, R.string.toast_passwordCopied, Toast.LENGTH_SHORT).show();
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText("password",
                            (((EditText) findViewById(R.id.contentHome_passwordBox)).getText())));
        };

    }

    private void setupWidgets() {
        layout_home = findViewById(R.id.content_home);
        layout_history = findViewById(R.id.content_savedPasswords);
        layout_settings = findViewById(R.id.content_settings);
        layout_catalog = findViewById(R.id.content_catalog);
        navigation = findViewById(R.id.navigation);
        passwordBox = findViewById(R.id.contentHome_passwordBox);
        saveBox = findViewById(R.id.content_new_password_password);
        setupWidgetListener();
    }

    private void setupView() {
        navigation.setSelectedItemId(R.id.navigation_home);

        //Set List adapters
        if (BuildConfig.DEBUG)
            loadPasswords(0);
        ExpandableListView listView = findViewById(R.id.expandedHistoryList);
        historyAdapter = new HistoryAdapter(this);
        listView.setAdapter(historyAdapter);

        listView = findViewById(R.id.expandedCatalogList);
        catalogAdapter = new CatalogAdapter(this);
        listView.setAdapter(catalogAdapter);
    }

    private void loadPasswords(int size) {
        PasswordGenerator generator = new PasswordGenerator();

        for (size = Math.abs(size); size > 0; size--) {
            IPasswordObject passwordObject = generator.generatePassword(size + 23);
            passwordObject.setDescription("DEBUG-Element: #" + size);
            history.addPasswordObject(passwordObject, this);
        }
    }

    private void fillPasswordBox() {
        String letters = preferences.getString("password_Letters", null);
        String letters_small = getResources().getStringArray(R.array.pref_password_letters_list_values)[0];
        String letters_large = getResources().getStringArray(R.array.pref_password_letters_list_values)[1];
        String letters_mixed = getResources().getStringArray(R.array.pref_password_letters_list_values)[2];

        int length = Integer.parseInt(preferences.getString("password_length", "0"));
        boolean isNumbers = preferences.getBoolean("password_numbers", false);
        boolean isSmallChars = letters.equals(letters_small) || letters.equals(letters_mixed);
        boolean isLargeChars = letters.equals(letters_large) || letters.equals(letters_mixed);
        boolean isSpecialChars = preferences.getBoolean("password_specialChars", false);
        String extraChars = preferences.getString("password_extra_specialChars", null);
        IPasswordObject passwordObject =
                generator.generatePassword(length, isNumbers, isSmallChars, isLargeChars, isSpecialChars, extraChars);
        history.addPasswordObject(passwordObject, this);
        passwordBox.setText(passwordObject.getPassword());
        saveBox.setText(passwordObject.getPassword());
    }
}
