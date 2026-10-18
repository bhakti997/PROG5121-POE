package com.bhakti.prog5121_apoe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles creation, validation, and sending of QuickChat messages for
 * the PROG5121 APOE Part 2 feature. Tracks every message sent during
 * the current run so totals and a running log can be reported back.
 *
 * @author Bhakti
 */
public class Message {

    /** Success text returned when a recipient cell number is valid. */
    public static final String CELL_CAPTURED = "Cell phone number successfully captured.";

    /** Success text returned when a message is within the length limit. */
    public static final String MESSAGE_READY = "Message ready to send.";

    private String messageID;
    private String recipientCell;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;
    private static final List<String> sentMessageLog = new ArrayList<>();

    /** Default constructor, used when only the validation methods are needed. */
    public Message() {
    }

    /** Builds a complete message and generates its hash immediately. */
    public Message(String messageID, String recipientCell, String messageText, int messageNumber) {
        this.messageID = messageID;
        this.recipientCell = recipientCell;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageHash = createMessageHash();
    }

    /** Checks that a message ID is no more than ten characters long. */
    public boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10;
    }

    /** Returns the message ID confirmation text required by the brief. */
    public String getMessageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    /**
     * Checks that the recipient cell number starts with the South African
     * international code. Reuses Login.checkCellPhoneNumber() so the
     * validation rule only exists in one place.
     */
    public String checkRecipientCell(String recipientCell) {
        Login login = new Login();
        if (login.checkCellPhoneNumber(recipientCell)) {
            return CELL_CAPTURED;
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    /** Checks that a message does not exceed 250 characters. */
    public String checkMessageLength(String message) {
        if (message != null && message.length() <= 250) {
            return MESSAGE_READY;
        }
        int excess = message.length() - 250;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /**
     * Creates the message hash: the first two digits of the message ID,
     * a colon, the message's number in the sequence, a colon, and the
     * first and last words of the message in uppercase with punctuation
     * stripped, e.g. 00:0:HITONIGHT.
     */
    public String createMessageHash() {
        String idPrefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    /**
     * Applies the user's choice for this message: 1 = Send, 2 = Disregard,
     * 3 = Store. Updates the running total and log when sent, and writes
     * to the JSON file when stored.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                sentMessageLog.add(printDetails());
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    /** Returns this message's details in the order: ID, hash, recipient, message. */
    public String printDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipientCell
                + "\nMessage: " + messageText;
    }

    /** Returns the full details of every message sent so far this run. */
    public String printMessages() {
        return String.join("\n\n", sentMessageLog);
    }

    /** Returns the total number of messages sent so far this run. */
    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    /**
     * Appends this message's details to stored_messages.json as a JSON
     * object inside a JSON array, creating the file if it doesn't exist.
     *
     * JSONObject/JSONArray usage pattern adapted from JSON.org (2024).
     */
    public void storeMessage() {
        JSONObject obj = new JSONObject();
        obj.put("messageID", messageID);
        obj.put("messageHash", messageHash);
        obj.put("recipient", recipientCell);
        obj.put("message", messageText);

        File file = new File("stored_messages.json");
        JSONArray array;
        try {
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                array = content.isBlank() ? new JSONArray() : new JSONArray(content);
            } else {
                array = new JSONArray();
            }
            array.put(obj);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(array.toString(2));
            }
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    /** Generates a random 10-digit message ID. */
    public static String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }
}
/*
 * REFERENCES
 *
 * JSON.org, 2024. JSON in Java [Source code] Available at:
 * <https://github.com/stleary/JSON-java> [Accessed 17 October 2026].
 */