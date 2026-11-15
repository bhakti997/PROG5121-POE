package com.bhakti.prog5121_apoe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creates, validates, sends, stores, searches and reports on QuickChat
 * messages for the PROG5121 APOE.
 *
 * @author Bhakti
 */
public class Message {

    public static final String CELL_CAPTURED = "Cell phone number successfully captured.";
    public static final String MESSAGE_READY = "Message ready to send.";
    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARD = "Disregard";

    private String messageID;
    private String recipientCell;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;
    private static final List<String> sentMessageLog = new ArrayList<>();

    private static final List<String> sentMessages = new ArrayList<>();
    private static final List<String> disregardedMessages = new ArrayList<>();
    private static final List<String> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    // Parallel to messageIDs: the same index describes the same message.
    private static final List<String> messageSenders = new ArrayList<>();
    private static final List<String> messageRecipients = new ArrayList<>();
    private static final List<String> messageTexts = new ArrayList<>();
    private static final List<String> messageFlags = new ArrayList<>();

    private static String sender = "Developer";
    private static String storageFile = "stored_messages.json";

    /** Creates an empty message, used when only the validation methods are needed. */
    public Message() {
    }

    /** Creates a complete message and generates its hash. */
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

    /** Returns the message ID confirmation text. */
    public String getMessageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    /** Checks the recipient number using the cell number rule in Login. */
    public String checkRecipientCell(String recipientCell) {
        Login login = new Login();
        if (login.checkCellPhoneNumber(recipientCell)) {
            return CELL_CAPTURED;
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    /** Checks that a message does not exceed 250 characters. */
    public String checkMessageLength(String message) {
        if (message == null) {
            return "Please enter a message.";
        }
        if (message.length() <= 250) {
            return MESSAGE_READY;
        }
        int excess = message.length() - 250;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /** Creates the hash: first two ID digits, message number, then first and last word in capitals. */
    public String createMessageHash() {
        String idPrefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    /** Applies the user's choice: 1 = send, 2 = disregard, 3 = store. */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                sentMessageLog.add(printDetails());
                sentMessages.add(messageText);
                recordMessage(SENT);
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(messageText);
                recordMessage(DISREGARD);
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(messageText);
                recordMessage(STORED);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    private void recordMessage(String flag) {
        messageIDs.add(messageID);
        messageHashes.add(messageHash);
        messageSenders.add(sender);
        messageRecipients.add(recipientCell);
        messageTexts.add(messageText);
        messageFlags.add(flag);
    }

    /** Returns the message details in the order: ID, hash, recipient, message. */
    public String printDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipientCell
                + "\nMessage: " + messageText;
    }

    /** Returns the details of every message sent during this run. */
    public String printMessages() {
        return String.join("\n\n", sentMessageLog);
    }

    /** Returns the total number of messages sent during this run. */
    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    private static JSONObject buildJson(String id, String hash, String from, String to, String text) {
        JSONObject obj = new JSONObject();
        obj.put("messageID", id);
        obj.put("messageHash", hash);
        obj.put("sender", from);
        obj.put("recipient", to);
        obj.put("message", text);
        return obj;
    }

    /**
     * Adds this message to the JSON file, creating the file if needed.
     * JSON usage adapted from JSON.org (2024).
     */
    public void storeMessage() {
        JSONObject obj = buildJson(messageID, messageHash, sender, recipientCell, messageText);
        File file = new File(storageFile);
        try {
            JSONArray array = new JSONArray();
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                if (!content.isBlank()) {
                    array = new JSONArray(content);
                }
            }
            array.put(obj);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(array.toString(2));
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    /** Generates a random ten-digit message ID. */
    public static String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Reads the JSON file into the stored messages array, skipping messages
     * already loaded, and returns how many were added.
     * JSON usage adapted from JSON.org (2024).
     */
    public static int loadStoredMessagesFromJson() {
        File file = new File(storageFile);
        if (!file.exists()) {
            return 0;
        }
        int loaded = 0;
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (content.isBlank()) {
                return 0;
            }
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("messageID");
                if (messageIDs.contains(id)) {
                    continue;
                }
                String text = obj.getString("message");
                storedMessages.add(text);
                messageIDs.add(id);
                messageHashes.add(obj.getString("messageHash"));
                messageSenders.add(obj.optString("sender", "Developer"));
                messageRecipients.add(obj.getString("recipient"));
                messageTexts.add(text);
                messageFlags.add(STORED);
                loaded++;
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error reading stored messages: " + e.getMessage());
        }
        return loaded;
    }

    /** Returns the stored messages as a numbered list. */
    public static String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            out.append(i + 1).append(") ").append(storedMessages.get(i)).append("\n");
        }
        return out.toString().trim();
    }

    /** Returns the sender and recipient of every stored message. */
    public static String displaySenderAndRecipient() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals(STORED)) {
                out.append("Sender: ").append(messageSenders.get(i))
                   .append(" | Recipient: ").append(messageRecipients.get(i)).append("\n");
            }
        }
        return out.length() == 0 ? "No stored messages." : out.toString().trim();
    }

    /** Returns the longest message of all the messages recorded. */
    public static String returnLongestMessage() {
        String longest = "";
        for (String text : messageTexts) {
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        return longest.isEmpty() ? "No messages available." : longest;
    }

    /** Finds a message by its ID and returns its recipient and message. */
    public static String searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return "Recipient: " + messageRecipients.get(index)
                + "\nMessage: " + messageTexts.get(index);
    }

    /** Returns every sent or stored message for the given recipient. */
    public static List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (int i = 0; i < messageRecipients.size(); i++) {
            if (messageRecipients.get(i).equals(recipient)
                    && !messageFlags.get(i).equals(DISREGARD)) {
                found.add(messageTexts.get(i));
            }
        }
        return found;
    }

    /** Deletes the message with the given hash, including from the JSON file if it was stored. */
    public static String deleteMessageByHash(String hash) {
        int index = messageHashes.indexOf(hash);
        if (index == -1) {
            return "Message hash not found.";
        }
        return removeMessage(index);
    }

    /** Deletes this message using its message ID. */
    public String deleteMessage() {
        int index = messageIDs.indexOf(messageID);
        if (index == -1) {
            return "Message not found.";
        }
        return removeMessage(index);
    }

    private static String removeMessage(int index) {
        String text = messageTexts.get(index);
        String flag = messageFlags.get(index);

        if (flag.equals(SENT)) {
            sentMessages.remove(text);
        } else if (flag.equals(DISREGARD)) {
            disregardedMessages.remove(text);
        } else {
            storedMessages.remove(text);
        }

        messageIDs.remove(index);
        messageHashes.remove(index);
        messageSenders.remove(index);
        messageRecipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        if (flag.equals(STORED)) {
            rewriteStorageFile();
        }
        return "Message: \"" + text + "\" successfully deleted.";
    }

    private static void rewriteStorageFile() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals(STORED)) {
                array.put(buildJson(messageIDs.get(i), messageHashes.get(i),
                        messageSenders.get(i), messageRecipients.get(i), messageTexts.get(i)));
            }
        }
        try (FileWriter writer = new FileWriter(storageFile)) {
            writer.write(array.toString(2));
        } catch (IOException e) {
            System.out.println("Error updating stored messages: " + e.getMessage());
        }
    }

    /** Returns a report of all sent and stored messages. */
    public static String displayReport() {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals(DISREGARD)) {
                continue;
            }
            report.append("Message ID: ").append(messageIDs.get(i))
                  .append("\nMessage Hash: ").append(messageHashes.get(i))
                  .append("\nSender: ").append(messageSenders.get(i))
                  .append("\nRecipient: ").append(messageRecipients.get(i))
                  .append("\nMessage: ").append(messageTexts.get(i))
                  .append("\nStatus: ").append(messageFlags.get(i))
                  .append("\n\n");
        }
        return report.length() == 0 ? "No messages to report." : report.toString().trim();
    }

    public static List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<String> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<String> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    /** Sets the name shown as the sender of new messages. */
    public static void setSender(String name) {
        sender = name;
    }

    /** Sets the JSON file path (the unit tests use a temporary file). */
    public static void setStorageFile(String path) {
        storageFile = path;
    }

    /** Clears all arrays and counters so each unit test starts clean. */
    public static void resetAll() {
        totalMessagesSent = 0;
        sentMessageLog.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        messageSenders.clear();
        messageRecipients.clear();
        messageTexts.clear();
        messageFlags.clear();
        sender = "Developer";
    }
}
/*
 * REFERENCES
 *
 * JSON.org, 2024. JSON in Java [Source code] Available at:
 * <https://github.com/stleary/JSON-java> [Accessed 17 October 2026].
 */