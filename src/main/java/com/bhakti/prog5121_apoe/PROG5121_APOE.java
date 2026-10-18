package com.bhakti.prog5121_apoe;

import java.util.Scanner;

/**
 * Console entry point for the PROG5121 APOE registration and login
 * feature. Prompts the user for their details, validates each field
 * immediately using the Login class, then allows the user to log in
 * and send messages through the QuickChat feature.
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

        boolean loggedInSuccessfully = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus());

        if (loggedInSuccessfully) {
            System.out.println("\nWelcome to QuickChat.");
            System.out.println("How many messages would you like to send?");
            int numMessages = Integer.parseInt(scanner.nextLine());

            boolean quit = false;
            int messageCounter = 0;

            while (!quit) {
                System.out.println("\n1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                String menuChoice = scanner.nextLine();

                switch (menuChoice) {
                    case "1":
                        for (int i = 0; i < numMessages; i++) {
                            Message validator = new Message();

                            String recipient;
                            while (true) {
                                System.out.println("Enter recipient cell number:");
                                recipient = scanner.nextLine();
                                String cellResult = validator.checkRecipientCell(recipient);
                                System.out.println(cellResult);
                                if (cellResult.equals("Cell phone number successfully captured.")) break;
                            }

                            String messageText;
                            while (true) {
                                System.out.println("Enter your message:");
                                messageText = scanner.nextLine();
                                String lengthResult = validator.checkMessageLength(messageText);
                                System.out.println(lengthResult);
                                if (lengthResult.equals("Message ready to send.")) break;
                            }

                            String messageID = Message.generateMessageID();
                            System.out.println("Message ID generated: " + messageID);

                            Message message = new Message(messageID, recipient, messageText, messageCounter);
                            messageCounter++;

                            System.out.println("1) Send Message");
                            System.out.println("2) Disregard Message");
                            System.out.println("3) Store Message to send later");
                            String sendChoice = scanner.nextLine();
                            String result = message.sentMessage(Integer.parseInt(sendChoice));
                            System.out.println(result);

                            if (sendChoice.equals("1")) {
                                System.out.println(message.printMessages());
                            }
                        }
                        System.out.println("\nTotal messages sent: " + new Message().returnTotalMessages());
                        break;
                    case "2":
                        System.out.println("Coming Soon.");
                        break;
                    case "3":
                        quit = true;
                        break;
                    default:
                        System.out.println("Please select a valid option (1-3).");
                }
            }
        }

        scanner.close();
    }
}