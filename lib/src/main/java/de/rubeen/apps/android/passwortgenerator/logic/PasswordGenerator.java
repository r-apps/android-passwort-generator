package de.rubeen.apps.android.passwortgenerator.logic;

import java.time.LocalDateTime;
import java.util.Random;

public class PasswordGenerator implements IPasswordGenerator {
    private boolean defaultIsNumbers = true, defaultIsSmallChars = true, isDefaultIsLargeChars = true,
            isDefaultSpecialChars = true;
    private final String defaultSpecialChars = null;
    private final String numbers = "1234567890", smallChars = "abcdefghijklmopqrstuvwxyz",
            largeChars = smallChars.toUpperCase(), specialChars = "*/-.,=";

    //***** INTERFACE IPasswordGenerator BEGIN *****//

    /**
     * Generates a random password with no parameter
     *
     * @return
     */
    @Override
    public IPasswordObject generatePassword(final int length) {
        return this.generatePassword(length, defaultIsNumbers, defaultIsSmallChars, isDefaultIsLargeChars, isDefaultSpecialChars);
    }

    /**
     * Generates a random password without specialChars
     *
     * @param isNumbers      numbers shall be available in password
     * @param isSmallChars   small chars shall be available in password
     * @param isLargeChars   large chars shall be available in password
     * @param isSpecialChars default specialChars shall be available in password
     * @return you're random password
     */
    @Override
    public IPasswordObject generatePassword(final int length, final boolean isNumbers, final boolean isSmallChars, final boolean isLargeChars,
                                   final boolean isSpecialChars) {
        return this.generatePassword(length, isNumbers, isSmallChars, isLargeChars, isSpecialChars, defaultSpecialChars);
    }

    /**
     * Generates a random password without specialChars
     *
     * @param isNumbers      numbers shall be available in password
     * @param isSmallChars   small chars shall be available in password
     * @param isLargeChars   large chars shall be available in password
     * @param isSpecialChars default specialChars shall be available in password
     * @param specialChars   chars that additionally shall be in password
     * @return
     */
    @Override
    public IPasswordObject generatePassword(final int length, final boolean isNumbers, final boolean isSmallChars, final boolean isLargeChars,
                                   final boolean isSpecialChars, final String specialChars) {
        return generate(length, isNumbers, isSmallChars, isLargeChars, isSpecialChars, specialChars);
    }

    /**
     * Returns the specialChars that are used by default
     *
     * @return specialChars that are used by default
     */
    @Override
    public String getSpecialChars() {
        return specialChars;
    }

    //***** INTERFACE IPasswordGenerator END *****//

    private IPasswordObject generate(final int lenght, final boolean isNumbers, final boolean isSmallChars, final boolean isLargeChars,
                            final boolean isSpecialChars, final String specialChars) {
        final StringBuilder result = new StringBuilder(),
                availableChars = new StringBuilder();
        final Random random = new Random();
        availableChars.append(isNumbers ? this.numbers : null);
        availableChars.append(isSmallChars ? this.smallChars : null);
        availableChars.append(isLargeChars ? this.largeChars : null);
        availableChars.append(isSpecialChars ? this.specialChars : null);
        availableChars.append(specialChars);

        for (int i = 0; i < lenght; i++)
            result.append(availableChars.charAt(random.nextInt(availableChars.length())));

        return new PasswordObject(result.toString(), LocalDateTime.now(), 0);
    }
}
