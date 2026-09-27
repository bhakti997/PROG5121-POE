package com.bhakti.prog5121_apoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Login class, using the test data and expected
 * messages specified in the PROG5121 APOE brief.
 *
 * @author Bhakti
 */
public class LoginTest {

    private Login login;

    /** Creates a fresh Login instance before every test. */
    @BeforeEach
    void setUp() {
        login = new Login();
    }

    /** Username with underscore and 5 or fewer characters should pass. */
    @Test
    void testCheckUserName_valid() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    /** Username with no underscore and too many characters should fail. */
    @Test
    void testCheckUserName_invalid() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    /** Password meeting all complexity rules should pass. */
    @Test
    void testCheckPasswordComplexity_valid() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    /** Password missing complexity rules should fail. */
    @Test
    void testCheckPasswordComplexity_invalid() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    /** Cell number with +27 international code should pass. */
    @Test
    void testCheckCellPhoneNumber_valid() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    /** Cell number without international code should fail. */
    @Test
    void testCheckCellPhoneNumber_invalid() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    /** Registration with all valid fields should succeed. */
    @Test
    void testRegisterUser_success() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals("User registered successfully", result);
    }

    /** Registration with invalid fields should not return the success message. */
    @Test
    void testRegisterUser_failure() {
        String result = login.registerUser("kyle!!!!!!!", "password", "08966553", "Kyle", "Smith");
        assertNotEquals("User registered successfully", result);
    }

    /** Logging in with correct username and password should succeed. */
    @Test
    void testLoginUser_success() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    /** Logging in with an incorrect password should fail. */
    @Test
    void testLoginUser_failure() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.loginUser("kyl_1", "wrongpassword"));
    }

    /** A successful login should return the correct welcome message. */
    @Test
    void testReturnLoginStatus_success() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus());
    }

    /** A failed login should return the correct error message. */
    @Test
    void testReturnLoginStatus_failure() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        login.loginUser("kyl_1", "wrongpassword");
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus());
    }
}