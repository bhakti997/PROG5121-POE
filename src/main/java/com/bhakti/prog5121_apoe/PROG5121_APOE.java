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

        // Messages can only be sent after a successful login.
        if (loggedInSuccessfully) {
            runQuickChat(scanner);
        }

        scanner.close();
    }

    /**
     * Runs the QuickChat menu. The user chooses how many messages they want
     * to send, then the menu loops until they choose to quit.
     */
    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");
        int numMessages = readNumber(scanner, "How many messages would you like to send?", 1, Integer.MAX_VALUE);

        boolean messagesEntered = false;
        boolean quit = false;

        while (!quit) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            String menuChoice = scanner.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    if (messagesEntered) {
                        System.out.println("You have already entered the " + numMessages + " message(s) you chose at the start.");
                    } else {
                        sendMessages(scanner, numMessages);
                        messagesEntered = true;
                    }
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

    /**
     * Lets the user enter exactly numMessages messages. The loop counter
     * doubles as the message number used in the message hash.
     */
    private static void sendMessages(Scanner scanner, int numMessages) {
        for (int i = 0; i < numMessages; i++) {
            Message validator = new Message();
            String recipient = readRecipient(scanner, validator);
            String messageText = readMessageText(scanner, validator);

            Message message = new Message(Message.generateMessageID(), recipient, messageText, i);
            System.out.println(message.getMessageIDGenerated());

            int sendChoice = readNumber(scanner,
                    "1) Send Message\n2) Disregard Message\n3) Store Message to send later", 1, 3);
            System.out.println(message.sentMessage(sendChoice));

            // Only a sent message has its full details displayed.
            if (sendChoice == 1) {
                System.out.println(message.printDetails());
            }
        }
        System.out.println("\nTotal messages sent: " + new Message().returnTotalMessages());
    }

    /** Asks for a recipient cell number until a valid one is entered. */
    private static String readRecipient(Scanner scanner, Message validator) {
        while (true) {
            System.out.println("Enter recipient cell number:");
            String recipient = scanner.nextLine();
            String result = validator.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.equals(Message.CELL_CAPTURED)) {
                return recipient;
            }
        }
    }

    /** Asks for a message until one within the 250 character limit is entered. */
    private static String readMessageText(Scanner scanner, Message validator) {
        while (true) {
            System.out.println("Enter your message:");
            String messageText = scanner.nextLine();
            String result = validator.checkMessageLength(messageText);
            System.out.println(result);
            if (result.equals(Message.MESSAGE_READY)) {
                return messageText;
            }
        }
    }

    /**
     * Shows the prompt and reads a whole number between min and max,
     * asking again if the user types letters or a number out of range.
     */
    private static int readNumber(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.println(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Not a number: fall through to the retry message below.
            }
            System.out.println("Invalid input. Please try again.");
        }
    }
}