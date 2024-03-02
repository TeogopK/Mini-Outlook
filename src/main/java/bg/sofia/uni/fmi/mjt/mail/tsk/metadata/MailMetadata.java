package bg.sofia.uni.fmi.mjt.mail.tsk.metadata;

import bg.sofia.uni.fmi.mjt.mail.tsk.parsers.CommaSeparatedValuesToSetParser;
import bg.sofia.uni.fmi.mjt.mail.tsk.parsers.KeyFromTextToValueLineParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class MailMetadata {

    private static final String SENDER_KEY = "sender:";
    private static final String SUBJECT_KEY = "subject:";
    private static final String RECEIVED_KEY = "received:";
    private static final String RECIPIENTS_KEY = "recipients:";

    private static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    
    private final String sender;
    private final String subject;
    private final LocalDateTime received;
    private final Set<String> recipients;

    public MailMetadata(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text can not be null");
        }

        this.sender = KeyFromTextToValueLineParser.getValueLine(SENDER_KEY, text);
        this.subject = KeyFromTextToValueLineParser.getValueLine(SUBJECT_KEY, text);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_TIME);
        String receivedLine = KeyFromTextToValueLineParser.getValueLine(RECEIVED_KEY, text);
        this.received = LocalDateTime.parse(receivedLine, formatter);

        String recipientsLine = KeyFromTextToValueLineParser.getValueLine(RECIPIENTS_KEY, text);
        this.recipients = CommaSeparatedValuesToSetParser.getSet(recipientsLine);
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDateTime getReceived() {
        return received;
    }

    public Set<String> getRecipients() {
        return recipients;
    }
}
