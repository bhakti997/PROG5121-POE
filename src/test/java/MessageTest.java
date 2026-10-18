package com.bhakti.prog5121_apoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class, using the test data and expected
 * messages specified in the PROG5121 APOE Part 2 brief.
 *
 * @author Bhakti
 */
public class MessageTest {

    // ---------- Message length (250 characters) ----------

    /** Test Data message 1 is within the limit. */
    @Test
    void testCheckMessageLength_success() {
        Message message = new Message();
        assertEquals("Message ready to send.",
                message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    /** Test Data message 2 is within the limit. */
    @Test
    void testCheckMessageLength_testCase2() {
        Message message = new Message();
        assertEquals("Message ready to send.",
                message.checkMessageLength("Hi Keegan, did you receive the payment?"));
    }

    /** A message over 250 characters returns the excess count. */
    @Test
    void testCheckMessageLength_failure() {
        Message message = new Message();
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength(longMessage));
    }

    // ---------- Recipient cell number ----------

    @Test
    void testCheckRecipientCell_valid() {
        Message message = new Message();
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell("+27718693002"));
    }

    /** Test Data message 2 recipient has no international code. */
    @Test
    void testCheckRecipientCell_invalid() {
        Message message = new Message();
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                message.checkRecipientCell("08575975889"));
    }

    // ---------- Message ID ----------

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

    /** A generated ID is exactly ten characters and passes the ID check. */
    @Test
    void testGenerateMessageID_length() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(new Message().checkMessageID(id));
    }

    /** The confirmation text matches the brief. */
    @Test
    void testMessageIDGenerated_text() {
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ID generated: 0012345678", message.getMessageIDGenerated());
    }

    // ---------- Message hash ----------

    @Test
    void testCreateMessageHash_testCase1() {
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    /** Loops through the test messages, as instructed in the brief. */
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

    // ---------- Send / Disregard / Store ----------

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

    // ---------- Totals and printing ----------

    /**
     * Only sent messages increase the total. The total is static and shared
     * between tests, so this compares against the value before sending.
     */
    @Test
    void testReturnTotalMessages() {
        Message counter = new Message();
        int before = counter.returnTotalMessages();

        new Message(Message.generateMessageID(), "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0).sentMessage(1);
        assertEquals(before + 1, counter.returnTotalMessages());

        new Message(Message.generateMessageID(), "08575975889",
                "Hi Keegan, did you receive the payment?", 1).sentMessage(2);
        assertEquals(before + 1, counter.returnTotalMessages());
    }

    /** printMessages shows ID, hash, recipient and message for sent messages. */
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
}