package com.bhakti.prog5121_apoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class using the test data from the
 * PROG5121 APOE Part 2 and Part 3 briefs.
 *
 * @author Bhakti
 */
public class MessageTest {

    @TempDir
    Path tempDir;

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @BeforeEach
    void setUp() {
        Message.resetAll();
        Message.setStorageFile(tempDir.resolve("stored_messages.json").toString());
    }

    /** Creates the five Part 3 test messages: Sent, Stored, Disregard, Sent, Stored. */
    private void populateTestData() {
        message1 = new Message("0000000001", "+27834557896", "Did you get the cake?", 0);
        message2 = new Message("0000000002", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", 1);
        message3 = new Message("0000000003", "+27834484567", "Yohoooo, I am at your gate.", 2);
        message4 = new Message("0000000004", "0838884567", "It is dinner time!", 3);
        message5 = new Message("0000000005", "+27838884567", "Ok, I am leaving without you.", 4);

        message1.sentMessage(1);
        message2.sentMessage(3);
        message3.sentMessage(2);
        message4.sentMessage(1);
        message5.sentMessage(3);
    }

    @Test
    void testCheckMessageLength_success() {
        Message message = new Message();
        assertEquals("Message ready to send.",
                message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    void testCheckMessageLength_testCase2() {
        Message message = new Message();
        assertEquals("Message ready to send.",
                message.checkMessageLength("Hi Keegan, did you receive the payment?"));
    }

    @Test
    void testCheckMessageLength_failure() {
        Message message = new Message();
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength(longMessage));
    }

    @Test
    void testCheckRecipientCell_valid() {
        Message message = new Message();
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell("+27718693002"));
    }

    @Test
    void testCheckRecipientCell_invalid() {
        Message message = new Message();
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                message.checkRecipientCell("08575975889"));
    }

    @Test
    void testCheckMessageID_valid() {
        Message message = new Message();
        assertTrue(message.checkMessageID("0012345678"));
    }

    @Test
    void testCheckMessageID_invalid() {
        Message message = new Message();
        assertFalse(message.checkMessageID("00123456789"));
    }

    @Test
    void testGenerateMessageID_length() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(new Message().checkMessageID(id));
    }

    @Test
    void testMessageIDGenerated_text() {
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ID generated: 0012345678", message.getMessageIDGenerated());
    }

    @Test
    void testCreateMessageHash_testCase1() {
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void testCreateMessageHash_loop() {
        String[][] testData = {
            {"0012345678", "Hi Mike, can you join us for dinner tonight?", "0", "00:0:HITONIGHT"},
            {"0098765432", "Hi Keegan, did you receive the payment?", "1", "00:1:HIPAYMENT"}
        };
        for (String[] data : testData) {
            Message message = new Message(data[0], "+27718693002", data[1], Integer.parseInt(data[2]));
            assertEquals(data[3], message.createMessageHash());
        }
    }

    @Test
    void testSentMessage_send() {
        Message message = new Message(Message.generateMessageID(), "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }

    @Test
    void testSentMessage_disregard() {
        Message message = new Message(Message.generateMessageID(), "08575975889",
                "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }

    @Test
    void testSentMessage_store() {
        Message message = new Message(Message.generateMessageID(), "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 2);
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }

    @Test
    void testReturnTotalMessages() {
        Message counter = new Message();
        assertEquals(0, counter.returnTotalMessages());

        new Message(Message.generateMessageID(), "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0).sentMessage(1);
        assertEquals(1, counter.returnTotalMessages());

        new Message(Message.generateMessageID(), "08575975889",
                "Hi Keegan, did you receive the payment?", 1).sentMessage(2);
        assertEquals(1, counter.returnTotalMessages());
    }

    @Test
    void testPrintMessages() {
        String id = Message.generateMessageID();
        Message message = new Message(id, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        message.sentMessage(1);

        String printed = message.printMessages();
        assertTrue(printed.contains("Message ID: " + id));
        assertTrue(printed.contains("Message Hash: " + message.createMessageHash()));
        assertTrue(printed.contains("Recipient: +27718693002"));
        assertTrue(printed.contains("Message: Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    void testSentMessagesArrayPopulated() {
        populateTestData();
        assertEquals(List.of("Did you get the cake?", "It is dinner time!"),
                Message.getSentMessages());
    }

    @Test
    void testOtherArraysPopulated() {
        populateTestData();
        assertEquals(List.of("Yohoooo, I am at your gate."), Message.getDisregardedMessages());
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), Message.getStoredMessages());
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(5, Message.getMessageIDs().size());
        assertTrue(Message.getMessageHashes().contains(message1.createMessageHash()));
        assertTrue(Message.getMessageIDs().contains("0000000004"));
    }

    @Test
    void testDisplayLongestMessage() {
        populateTestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.returnLongestMessage());
    }

    @Test
    void testSearchForMessageID() {
        populateTestData();
        String result = Message.searchByMessageID("0000000004");
        assertTrue(result.contains("0838884567"));
        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    void testSearchForMessageID_notFound() {
        populateTestData();
        assertEquals("Message ID not found.", Message.searchByMessageID("9999999999"));
    }

    @Test
    void testSearchByRecipient() {
        populateTestData();
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    void testDeleteMessageByHash() {
        populateTestData();
        String result = Message.deleteMessageByHash(message2.createMessageHash());
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                result);
        assertEquals(List.of("Ok, I am leaving without you."), Message.getStoredMessages());
        assertEquals(4, Message.getMessageIDs().size());
    }

    @Test
    void testDeleteMessageByHash_updatesJsonFile() {
        populateTestData();
        Message.deleteMessageByHash(message2.createMessageHash());

        Message.resetAll();
        assertEquals(1, Message.loadStoredMessagesFromJson());
        assertEquals(List.of("Ok, I am leaving without you."), Message.getStoredMessages());
    }

    @Test
    void testDeleteDisregardedMessage() {
        populateTestData();
        assertEquals("Message: \"Yohoooo, I am at your gate.\" successfully deleted.",
                message3.deleteMessage());
        assertTrue(Message.getDisregardedMessages().isEmpty());
        assertEquals(4, Message.getMessageIDs().size());
    }

    @Test
    void testDeleteMessageByHash_notFound() {
        populateTestData();
        assertEquals("Message hash not found.", Message.deleteMessageByHash("00:9:NOPE"));
        assertEquals(5, Message.getMessageHashes().size());
    }

    @Test
    void testDisplayReport() {
        populateTestData();
        String report = Message.displayReport();
        assertTrue(report.contains("Message Hash: " + message1.createMessageHash()));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));
        assertFalse(report.contains("Yohoooo"));
    }

    @Test
    void testDisplaySenderAndRecipient() {
        populateTestData();
        String result = Message.displaySenderAndRecipient();
        assertTrue(result.contains("Sender: Developer | Recipient: +27838884567"));
        assertFalse(result.contains("+27834557896"));
    }

    @Test
    void testReadJsonFileIntoArray() {
        populateTestData();
        Message.resetAll();
        assertTrue(Message.getStoredMessages().isEmpty());

        assertEquals(2, Message.loadStoredMessagesFromJson());
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), Message.getStoredMessages());
        assertTrue(Message.displayStoredMessages().contains("Ok, I am leaving without you."));
    }

    @Test
    void testReadJsonFile_noDuplicates() {
        populateTestData();
        assertEquals(0, Message.loadStoredMessagesFromJson());
        assertEquals(2, Message.getStoredMessages().size());
    }
}