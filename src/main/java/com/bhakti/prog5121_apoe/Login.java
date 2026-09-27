package com.bhakti.prog5121_apoe;

/**
 * Handles user registration and login for the PROG5121 APOE application.
 * Validates username, password, and South African cell phone number
 * formatting, and manages login state for a single user session.
 *
 * @author Bhakti
 */
public class Login {
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;
    private boolean loggedIn = false;

    /**
     * Checks that a username contains an underscore and is no more
     * than five characters long.
     */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks that a password is at least 8 characters long and contains
     * at least one capital letter, one number, and one special character.
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasCapital = false, hasNumber = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasCapital = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Checks that a cell phone number contains the South African
     * international dialling code (+27) followed by 9 digits.
     * Regex format follows the general E.164 international number
     * convention (country code + subscriber number, no spaces).
     * Reference: ITU-T E.164 numbering plan.
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && cellPhoneNumber.matches("^\\+27[0-9]{9}$");
    }

    /**
     * Registers a new user if the username, password, and cell number
     * all pass validation. Stores the user's details on success.
     * Returns a success message, or a combined message listing every
     * field that failed validation.
     */
    public String registerUser(String username, String password, String cellPhoneNumber,
                                String firstName, String lastName) {
        boolean usernameOk = checkUserName(username);
        boolean passwordOk = checkPasswordComplexity(password);
        boolean cellOk = checkCellPhoneNumber(cellPhoneNumber);

        if (usernameOk && passwordOk && cellOk) {
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            return "User registered successfully";
        }

        StringBuilder msg = new StringBuilder();
        if (!usernameOk) msg.append("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.\n");
        if (!passwordOk) msg.append("Password is not correctly formatted; please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.\n");
        if (!cellOk) msg.append("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.\n");
        return msg.toString().trim();
    }

    /**
     * Verifies the entered username and password match the details
     * captured during registration, and updates the login state.
     */
    public boolean loginUser(String username, String password) {
        loggedIn = this.username != null
                && this.username.equals(username)
                && this.password.equals(password);
        return loggedIn;
    }

    /**
     * Returns a welcome message if the user is currently logged in,
     * or an error message if the login attempt failed.
     */
    public String returnLoginStatus() {
        if (loggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}