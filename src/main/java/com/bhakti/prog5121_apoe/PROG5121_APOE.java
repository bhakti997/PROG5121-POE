package com.bhakti.prog5121_apoe;

import java.util.Scanner;

/**
 * Console entry point for the PROG5121 APOE registration and login
 * feature. Prompts the user for their details, validates each field
 * immediately using the Login class, then allows the user to log in.
 *
 * @author Bhakti
 */
public class PROG5121_APOE {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        // Loop until a valid username is entered.
        String username;
        while (true) {
            System.out.println("Enter a username (must contain an underscore and be no more than five characters):");
            username = scanner.nextLine();
            if (login.checkUserName(username)) {
                System.out.println("Username successfully captured.");
                break;
            } else {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        }

        // Loop until a valid password is entered.
        String password;
        while (true) {
            System.out.println("Enter a password (at least eight characters, a capital letter, a number, and a special character):");
            password = scanner.nextLine();
            if (login.checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
                break;
            } else {
                System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        }

        // Loop until a valid cell number is entered.
        String cellPhoneNumber;
        while (true) {
            System.out.println("Enter your South African cell number (e.g. +27838968976):");
            cellPhoneNumber = scanner.nextLine();
            if (login.checkCellPhoneNumber(cellPhoneNumber)) {
                System.out.println("Cell number successfully captured.");
                break;
            } else {
                System.out.println("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.");
            }
        }

        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter your last name:");
        String lastName = scanner.nextLine();

        // Final registration check (also re-validates all fields together).
        String registerResult = login.registerUser(username, password, cellPhoneNumber, firstName, lastName);
        System.out.println(registerResult);

        // Prompt the user to log in with the details they just registered.
        System.out.println("\nNow please log in.");
        System.out.println("Enter your username:");
        String loginUsername = scanner.nextLine();
        System.out.println("Enter your password:");
        String loginPassword = scanner.nextLine();

        login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus());

        scanner.close();
    }
}