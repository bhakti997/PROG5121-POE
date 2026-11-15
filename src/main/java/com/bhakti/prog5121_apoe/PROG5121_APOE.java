package com.bhakti.prog5121_apoe;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the PROG5121 APOE. Registers and logs in a user,
 * then lets them send, store and manage messages through QuickChat.
 *
 * @author Bhakti
 */
public class PROG5121_APOE {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        String username;
        while (true) {
            System.out.println("Enter a username (must contain an underscore and be no more than five characters):");
            username = scanner.nextLine();
            if (login.checkUserName(username)) {
                System.out.println("Username successfully captured.");
                break;
            }
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        String password;
        while (true) {
            System.out.println("Enter a password (at least eight characters, a capital letter, a number, and a special character):");
            password = scanner.nextLine();
            if (login.checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
                break;
            }
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        }

        String cellPhoneNumber;
        while (true) {
            System.out.println("Enter your South African cell number (e.g. +27838968976):");
            cellPhoneNumber = scanner.nextLine();
            if (login.checkCellPhoneNumber(cellPhoneNumber)) {
                System.out.println("Cell number successfully captured.");
                break;
            }
            System.out.println("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.");
        }

        System.out.println("Enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter your last name:");
        String lastName = scanner.nextLine();

        System.out.println(login.registerUser(username, password, cellPhoneNumber, firstName, lastName));

        System.out.println("\nNow please log in.");
        System.out.println("Enter your username:");
        String loginUsername = scanner.nextLine();
        System.out.println("Enter your password:");
        String loginPassword = scanner.nextLine();

        boolean loggedInSuccessfully = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus());

        if (loggedInSuccessfully) {
            Message.setSender(firstName + " " + lastName);
            runQuickChat(scanner);
        }

        scanner.close();
    }

    /** Runs the main QuickChat menu until the user chooses to quit. */
    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        int loaded = Message.loadStoredMessagesFromJson();
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from file.");
        }

        int numMessages = readNumber(scanner, "How many messages would you like to send?", 1, Integer.MAX_VALUE);
        boolean messagesEntered = false;
        boolean quit = false;

        while (!quit) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
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
                case "4":
                    runStoredMessagesMenu(scanner);
                    break;
                default:
                    System.out.println("Please select a valid option (1-4).");
            }
        }
    }

    /** Runs the Stored Messages menu, which works on the message arrays. */
    private static void runStoredMessagesMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nStored Messages");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its message hash");
            System.out.println("6) Display a report of all messages");
            System.out.println("7) Display stored messages read from the JSON file");
            System.out.println("0) Back to main menu");

            int choice = readNumber(scanner, "Choose an option:", 0, 7);

            switch (choice) {
                case 1:
                    System.out.println(Message.displaySenderAndRecipient());
                    break;
                case 2:
                    System.out.println("Longest message: " + Message.returnLongestMessage());
                    break;
                case 3:
                    System.out.println("Enter the message ID:");
                    System.out.println(Message.searchByMessageID(scanner.nextLine().trim()));
                    break;
                case 4:
                    System.out.println("Enter the recipient cell number:");
                    List<String> found = Message.searchByRecipient(scanner.nextLine().trim());
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String text : found) {
                        System.out.println("- " + text);
                    }
                    break;
                case 5:
                    System.out.println("Enter the message hash:");
                    System.out.println(Message.deleteMessageByHash(scanner.nextLine().trim().toUpperCase()));
                    break;
                case 6:
                    System.out.println(Message.displayReport());
                    break;
                case 7:
                    Message.loadStoredMessagesFromJson();
                    System.out.println(Message.displayStoredMessages());
                    break;
                default:
                    back = true;
            }
        }
    }

    /** Lets the user enter exactly numMessages messages; the loop counter is the message number. */
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

            if (sendChoice == 1) {
                System.out.println(message.printDetails());
            }
            if (sendChoice == 2) {
                System.out.println("Type 0 and press Enter to delete it, or just press Enter to keep it.");
                if (scanner.nextLine().trim().equals("0")) {
                    System.out.println(message.deleteMessage());
                }
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

    /** Reads a whole number between min and max, asking again if the input is invalid. */
    private static int readNumber(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.println(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Not a number, so ask again below.
            }
            System.out.println("Invalid input. Please try again.");
        }
    }
}