package bg.sofia.uni.fmi.mjt.mail.tsk.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.tsk.Account;
import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipientsIncludesConditionTest {

    private final RecipientsIncludesCondition recipientsIncludes = new RecipientsIncludesCondition();

    @Test
    void testSetValuesOneLiner() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com,";
        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");

        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values, "Condition text expected to be parsed correctly from oneliner");
    }

    @Test
    void testSetValuesNoMatch() {
        String text = "test sentence no match";
        recipientsIncludes.setValues(text);

        assertIterableEquals(Collections.emptySet(), recipientsIncludes.values,
            "Condition text expected to be parsed correctly when there is no match");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEnd() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator();
        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");

        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from oneliner with line separator at the end");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEndAndStart() {
        String text =
            System.lineSeparator() + "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();
        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");
        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from oneliner surrounded with line separators");
    }

    @Test
    void testSetValuesOneLinerNoSpace() {
        String text =
            System.lineSeparator() + "recipients-includes:pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();
        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");
        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from oneliner with no space after key");
    }

    @Test
    void testSetValuesMultipleLinesRecipientsAtTheEnd() {
        String text =
            "subject-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator();

        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");
        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesRecipientsAtTheBeginning() {
        String text =
            "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() + "from: stoyo@fmi.bg" +
                System.lineSeparator() + "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator();

        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");
        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesRecipientsInTheMiddle() {
        String text = "subject-includes: izpit" + System.lineSeparator() +
            "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator();

        Set<String> wanted = Set.of("pesho@gmail.com", "gosho@gmail.com");
        recipientsIncludes.setValues(text);

        assertEquals(wanted, recipientsIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testIsTextCorrectMultipleLines() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();

        assertTrue(recipientsIncludes.isTextCorrect(text), "Condition text is correct from multiple lines text");
    }

    @Test
    void testIsTextCorrectOneLine() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com,";
        assertTrue(recipientsIncludes.isTextCorrect(text), "Condition text is correct from oneliner");
    }

    @Test
    void testIsTextCorrectMultipleLinesDuplicate() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "recipients-includes: pesho@gmail.com" +
            System.lineSeparator() + "from: stoyo@fmi.bg";

        assertFalse(recipientsIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false from multiple lines text with duplicate");
    }

    @Test
    void testIsTextCorrectNextToOneAnother() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com," +
            "recipients-includes: pesho@gmail.com, gosho@gmail.com,";

        assertFalse(recipientsIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNextToOneAnotherWithLineSeparator() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com," + System.lineSeparator() +
            "recipients-includes: gosho@gmail.com,";

        assertFalse(recipientsIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNoMatch() {
        String text = "this is a no match sentence";

        assertTrue(recipientsIncludes.isTextCorrect(text), "Condition text is correct when no match is found");
    }

    @Test
    void testIsEmptyConditionNoMatch() {
        String text = "subject-includes: mjt, izpit, 2022";
        recipientsIncludes.setValues(text);

        assertTrue(recipientsIncludes.isEmptyCondition(),
            "Condition expected to be marked as empty when there is no match");
    }

    @Test
    void testIsEmptyConditionMatch() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com,";
        recipientsIncludes.setValues(text);

        assertFalse(recipientsIncludes.isEmptyCondition(),
            "Condition expected not to be marked as empty when there is a match");
    }

    private Mail createMailFrom(Set<String> recipients) {
        Account sender = new Account("test@email.com", "Test Name");
        LocalDateTime received = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(sender, recipients, "subject", "body huge", received);
    }

    @Test
    void testDoesConditionFitMailThreeOutOfThree() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com, ivan@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com", "ivan@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when three out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfThree() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com, ivan@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("pesho@gmail.com", "ivan@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when two out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailOneOutOfThree() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com, ivan@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("pesho@gmail.com", "other@gmail.com", "another@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when one out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfTwo() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when two out of two keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfTwo() {
        String text = "recipients-includes: pesho@gmail.com, gosho@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("other@gmail.com", "another@gmail.com");

        assertFalse(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should not fit mail when zero out of two keywords match");
    }

    @Test
    void testDoesConditionFitMailSenderEmptyCondition() {
        String text = "no match sentence";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("other@gmail.com", "another@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when condition is empty");
    }

    @Test
    void testDoesConditionFitMailOneOutOfOne() {
        String text = "recipients-includes: other@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("other@gmail.com");

        assertTrue(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should fit mail when one out of one keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfOne() {
        String text = "recipients-includes: pesho@gmail.com";
        recipientsIncludes.setValues(text);

        Set<String> recipients = Set.of("other@gmail.com");

        assertFalse(recipientsIncludes.doesConditionFitMail(createMailFrom(recipients)),
            "Condition should not fit mail when zero out of one keywords match");
    }

    @Test
    void testDoesConditionFitMailEmptyRecipients() {
        String text = "recipients-includes: pesho@gmail.com";
        recipientsIncludes.setValues(text);

        assertFalse(recipientsIncludes.doesConditionFitMail(createMailFrom(Collections.emptySet())),
            "Condition should not fit mail when recipients are empty set");
    }

}

