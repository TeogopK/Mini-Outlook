package bg.sofia.uni.fmi.mjt.mail.metadata;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MailMetadataTest {

    private MailMetadata metadata;

    private final String definition = getDefinition();

    private String getDefinition() {
        return "this is not a key: something" + System.lineSeparator() + "sender: testy@gmail.com" +
            System.lineSeparator() + "subject: Hello, MJT!" + System.lineSeparator() +
            "recipients: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() + "received: 2022-12-08 14:14" +
            System.lineSeparator();
    }

    @Test
    void testTextIsFine() {
        assertDoesNotThrow(() -> new MailMetadata(definition), "Expected metadata to be parsed correctly");
    }

    @Test
    void testSubjectIsCorrect() {
        metadata = new MailMetadata(definition);
        assertEquals("testy@gmail.com", metadata.getSender(), "Sender expected to be parsed correctly");
    }

    @Test
    void testSenderIsCorrect() {
        metadata = new MailMetadata(definition);
        assertEquals("Hello, MJT!", metadata.getSubject(), "Subject expected to be parsed correctly");
    }

    @Test
    void testRecipientsAreCorrect() {
        metadata = new MailMetadata(definition);
        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");

        assertEquals(recipients, metadata.getRecipients(), "Recipients expected to be parsed correctly");
    }

    @Test
    void testReceivedIsCorrect() {
        metadata = new MailMetadata(definition);

        LocalDateTime received = LocalDateTime.of(2022, 12, 8, 14, 14);

        assertEquals(received, metadata.getReceived(), "Received data expected to be parsed correctly");
    }

    @Test
    void testTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new MailMetadata(null),
            "Expected an exception to be thrown when text is null");
    }
}
