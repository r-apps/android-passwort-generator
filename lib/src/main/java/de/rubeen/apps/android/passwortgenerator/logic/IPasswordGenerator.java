package de.rubeen.apps.android.passwortgenerator.logic;

public interface IPasswordGenerator {
    /**
     * Generates a random password with no parameter
     *
     * @return
     */
    IPasswordObject generatePassword(final int length);

    /**
     * Generates a random password without specialChars
     *
     * @param isNumbers      numbers shall be available in password
     * @param isSmallChars   small chars shall be available in password
     * @param isLargeChars   large chars shall be available in password
     * @param isSpecialChars default specialChars shall be available in password
     * @return you're random password
     */
    IPasswordObject generatePassword(final int length, final boolean isNumbers, final boolean isSmallChars, final boolean isLargeChars, final boolean isSpecialChars);

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
    IPasswordObject generatePassword(final int length, final boolean isNumbers, final boolean isSmallChars, final boolean isLargeChars, final boolean isSpecialChars, final String specialChars);

    /**
     * Returns the specialChars that are used by default
     *
     * @return specialChars that are used by default
     */
    String getSpecialChars();
}
