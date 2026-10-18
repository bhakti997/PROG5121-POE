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

    @Test
    void testCheckMessageLength_success() {
        Message message = new Message();
        assertEquals("Message ready to send.",
                message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
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
    void testCreateMessageHash_testCase1() {
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    /** Loops through the remaining test messages, as instructed in the brief. */
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
    void testCheckMessageID_valid() {
        Message message = new Message();
        assertTrue(message.checkMessageID("0012345678"));
    }

    @Test
    void testCheckMessageID_invalid() {
        Message message = new Message();
        assertFalse(message.checkMessageID("00123456789"));
    }
}