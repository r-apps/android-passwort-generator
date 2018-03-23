package de.rubeen.apps.android.passwortgenerator.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordGeneratorTests {

    @Test
    void generatePassword() {
        PasswordGenerator generator = new PasswordGenerator();
        assert generator.generatePassword(1000).getPassword().length() == generator.generatePassword(1000).getPassword().length();
        Assertions.assertNotEquals(generator.generatePassword(10).getPassword(), generator.generatePassword(10).getPassword(),
                "Two passwords should not be equal.");
        assert generator.generatePassword(1000, false, false, false,
                true, "a")
                .equals(generator.generatePassword(1000, false, false, false,
                        true, "a"));
    }
}