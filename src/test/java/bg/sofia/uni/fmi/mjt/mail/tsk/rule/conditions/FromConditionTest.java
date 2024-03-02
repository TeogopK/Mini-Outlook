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

public class FromConditionTest {

    private final FromCondition fromCondition = new FromCondition();

    @Test
    void testSetValuesOneLiner() {
        String text = "from: stoyo@fmi.bg";
        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values, "Condition text expected to be parsed correctly from oneliner");
    }

    @Test
    void testSetValuesNoMatch() {
        String text = "test sentence no match";
        fromCondition.setValues(text);

        assertIterableEquals(Collections.emptySet(), fromCondition.values,
            "Condition text expected to be parsed correctly when there is no match");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEnd() {
        String text = "from: stoyo@fmi.bg" + System.lineSeparator();
        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from oneliner with line separator at the end");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEndAndStart() {
        String text = System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator();
        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from oneliner surrounded with line separators");
    }

    @Test
    void testSetValuesOneLinerNoSpace() {
        String text = System.lineSeparator() + "from:stoyo@fmi.bg" + System.lineSeparator();
        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from oneliner with no space after key");
    }

    @Test
    void testSetValuesMultipleLinesFromAtTheEnd() {
        String text =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "subject-or-body-includes: izpit" +
                System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator();

        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesFromAtTheBeginning() {
        String text = "from: stoyo@fmi.bg" + System.lineSeparator() + "subject-includes: mjt, izpit, 2022" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator();

        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesFromInTheMiddle() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator();

        Set<String> wanted = Set.of("stoyo@fmi.bg");
        fromCondition.setValues(text);

        assertEquals(wanted, fromCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testIsTextCorrectMultipleLines() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator();

        assertTrue(fromCondition.isTextCorrect(text), "Condition text is correct from multiple lines text");
    }

    @Test
    void testIsTextCorrectOneLine() {
        String text = "from: stoyo@fmi.bg";
        assertTrue(fromCondition.isTextCorrect(text), "Condition text is correct from oneliner");
    }

    @Test
    void testIsTextCorrectMultipleLinesDuplicate() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: test@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg";

        assertFalse(fromCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false from multiple lines text with duplicate");
    }

    @Test
    void testIsTextCorrectNextToOneAnother() {
        String text = "from: stoyo@fmi.bg" + "from: stoyo@fmi.bg";

        assertFalse(fromCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNextToOneAnotherWithLineSeparator() {
        String text = "from: stoyo@fmi.bg" + System.lineSeparator() + "from: stoyo@fmi.bg";

        assertFalse(fromCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNoMatch() {
        String text = "this is a no match sentence";

        assertTrue(fromCondition.isTextCorrect(text), "Condition text is correct when no match is found");
    }

    @Test
    void testIsEmptyConditionNoMatch() {
        String text = "test sentence no match";
        fromCondition.setValues(text);

        assertTrue(fromCondition.isEmptyCondition(), "Condition expected to be marked as empty when there is no match");
    }

    @Test
    void testIsEmptyConditionMatch() {
        String text = "from: stoyo@fmi.bg";
        fromCondition.setValues(text);

        assertFalse(fromCondition.isEmptyCondition(),
            "Condition expected not to be marked as empty when there is a match");
    }

    private final Mail mail = getMail();

    private Mail getMail() {
        Account sender = new Account("test@email.com", "Test Name");
        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");
        LocalDateTime received = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(sender, recipients, "testSubject", "This a test body!", received);
    }

    @Test
    void testDoesConditionFitMailWrongSender() {
        String text = "from: stoyo@fmi.bg";
        fromCondition.setValues(text);

        assertFalse(fromCondition.doesConditionFitMail(mail),
            "Condition should not fit mail when senders are different");
    }

    @Test
    void testDoesConditionFitMailCorrectSender() {
        String text = "from: test@email.com";
        fromCondition.setValues(text);

        assertTrue(fromCondition.doesConditionFitMail(mail),
            "Condition should fit mail when sender matches the expected one");
    }

    @Test
    void testDoesConditionFitMailSenderIsName() {
        String text = "from: Test Name";
        fromCondition.setValues(text);

        assertFalse(fromCondition.doesConditionFitMail(mail),
            "Condition should not fit mail when sender is specified by name and not email");
    }

    @Test
    void testDoesConditionFitMailSenderIsSubstring() {
        String text = "from: test";
        fromCondition.setValues(text);

        assertFalse(fromCondition.doesConditionFitMail(mail),
            "Condition should not fit mail when sender is a substring of the email");
    }

    @Test
    void testDoesConditionFitMailSenderEmptyCondition() {
        String text = "no match sentence";
        fromCondition.setValues(text);

        assertTrue(fromCondition.doesConditionFitMail(mail), "Condition should fit mail when condition is empty");
    }
}
