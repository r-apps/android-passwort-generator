package de.rubeen.apps.android.passwortgenerator;

import de.rubeen.apps.android.passwortgenerator.logic.PasswordObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class HistoryTest {
    PasswordObject passwordObject = new PasswordObject("myPassword",
            LocalDateTime.of(2011, 11, 11, 11, 11, 11, 11));
    PasswordObject passwordObject1 = new PasswordObject("myPassword2",
            LocalDateTime.of(2222, 2, 2, 2, 2, 2, 2));
    PasswordObject passwordObject2 = new PasswordObject("myPassword3",
            LocalDateTime.of(9090, 9, 9, 9, 9, 9, 9));

    @Before
    public void setUp() {
        History.getInstance().addPasswordObject(passwordObject, new MainActivity());
        History.getInstance().addPasswordObject(passwordObject1, new MainActivity());
        History.getInstance().addPasswordObject(passwordObject2, new MainActivity());
    }

    @Test
    public void getPasswordObjects() {
        if (BuildConfig.DEBUG) {
            History.getInstance().getPasswordObjects()
                    .forEach(item -> System.out.println(item.getId() + ": " + item.getPassword()));
            assert History.getInstance().getPasswordObjects().size() == 3;
        }
    }

    @Test
    public void getMaxId() {
        if (BuildConfig.DEBUG) {
            assert History.getInstance().getMaxId() == History.getInstance().getPasswordObjects().size() - 1;
            PasswordObject object = new PasswordObject("", null,
                    History.getInstance().getPasswordObjects().size());
            History.getInstance().addPasswordObject(object, new MainActivity());
            assert History.getInstance().getMaxId() == History.getInstance().getPasswordObjects().size() - 1;
            object = new PasswordObject("", null);
            History.getInstance().addPasswordObject(object, new MainActivity());
            assert History.getInstance().getMaxId() == object.getId();
        }
    }
}