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

public class SubjectIncludesConditionTest {

    private final SubjectIncludesCondition subjectIncludesCondition = new SubjectIncludesCondition();

    @Test
    void testSetValuesOneLiner() {
        String text = "subject-includes: mjt, izpit, 2022";
        Set<String> wanted = Set.of("mjt", "izpit", "2022");

        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner");
    }

    @Test
    void testSetValuesNoMatch() {
        String text = "test sentence no match";
        subjectIncludesCondition.setValues(text);

        assertIterableEquals(Collections.emptySet(), subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly when there is no match");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEnd() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");

        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner with line separator at the end");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEndAndStart() {
        String text = System.lineSeparator() + "subject-includes: mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner surrounded with line separators");
    }

    @Test
    void testSetValuesOneLinerNoSpace() {
        String text = System.lineSeparator() + "subject-includes:mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner with no space after key");
    }

    @Test
    void testSetValuesOneLinerSpacedWordsLineSeparators() {
        String text = System.lineSeparator() + "subject-includes: mjt a, izpit v, 2022 ab" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt a", "izpit v", "2022 ab");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner with spaced keywords");
    }

    @Test
    void testSetValuesOneLinerSpacedWords() {
        String text = "subject-includes: mjt a, izpit v, 2022 ab";

        Set<String> wanted = Set.of("mjt a", "izpit v", "2022 ab");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from oneliner with spaced keywords");
    }

    @Test
    void testSetValuesMultipleLinesSubjectAtTheEnd() {
        String text =
            "subject-or-body-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-includes: mjt, izpit, 2022" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesSubjectAtTheBeginning() {
        String text =
            "subject-or-body-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-includes: mjt, izpit, 2022" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesSubjectInTheMiddle() {
        String text =
            "subject-or-body-includes: izpit" + System.lineSeparator() + "subject-includes: mjt, izpit, 2022" +
                System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectIncludesCondition.setValues(text);

        assertEquals(wanted, subjectIncludesCondition.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testIsTextCorrectMultipleLines() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator();

        assertTrue(subjectIncludesCondition.isTextCorrect(text), "Condition text is correct from multiple lines text");
    }

    @Test
    void testIsTextCorrectOneLine() {
        String text = "subject-includes: mjt, izpit, 2022";
        assertTrue(subjectIncludesCondition.isTextCorrect(text), "Condition text is correct from oneliner");
    }

    @Test
    void testIsTextCorrectMultipleLinesDuplicate() {
        String text = "subject-includes: mjt" + System.lineSeparator() + "subject-includes: mjt, izpit, 2022" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg";

        assertFalse(subjectIncludesCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false from multiple lines text with duplicate");
    }

    @Test
    void testIsTextCorrectNextToOneAnother() {
        String text = "subject-includes: mjt, izpit, 2022" + "subject-includes: mjt, izpit, 2022";

        assertFalse(subjectIncludesCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNextToOneAnotherWithLineSeparator() {
        String text =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "subject-includes: mjt, izpit, 2022";

        assertFalse(subjectIncludesCondition.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNoMatch() {
        String text = "this is a no match sentence";

        assertTrue(subjectIncludesCondition.isTextCorrect(text), "Condition text is correct when no match is found");
    }

    @Test
    void testIsEmptyConditionNoMatch() {
        String text = "test sentence no match";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.isEmptyCondition(),
            "Condition expected to be marked as empty when there is no match");
    }

    @Test
    void testIsEmptyConditionMatch() {
        String text = "subject-includes: mjt, izpit, 2022";
        subjectIncludesCondition.setValues(text);

        assertFalse(subjectIncludesCondition.isEmptyCondition(),
            "Condition expected not to be marked as empty when there is a match");
    }

    private Mail createMailFrom(String subject) {
        Account sender = new Account("test@email.com", "Test Name");
        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");
        LocalDateTime received = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(sender, recipients, subject, "This a test body!", received);
    }

    @Test
    void testDoesConditionFitMailThreeOutOfThree() {
        String text = "subject-includes: mjt, izpit, 2022";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.doesConditionFitMail(
                createMailFrom("This sentence includes mjt, also izpit and 2022")),
            "Condition should fit mail when three out of three subject keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfThree() {
        String text = "subject-includes: mjt, izpit, 2022";
        subjectIncludesCondition.setValues(text);

        assertFalse(
            subjectIncludesCondition.doesConditionFitMail(createMailFrom("This sentence includes mjt but only izpit")),
            "Condition should not fit mail when two out of three subject keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfThree() {
        String text = "subject-includes: mjt, izpit, 2022";
        subjectIncludesCondition.setValues(text);

        assertFalse(subjectIncludesCondition.doesConditionFitMail(createMailFrom("empty sentence")),
            "Condition should not fit mail when zero out of three subject keywords match");
    }

    @Test
    void testDoesConditionFitMailThreeOfThreeWithSpaces() {
        String text = "subject-includes: mjt a, izpit v, 2022 ab";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.doesConditionFitMail(
                createMailFrom("This sentence includes mjt a, also izpit v and 2022 ab")),
            "Condition should fit mail when three out of three spaced subject keywords match");
    }

    @Test
    void testDoesConditionFitMailSubstring() {
        String text = "subject-includes: other";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.doesConditionFitMail(createMailFrom("This is another sentence")),
            "Condition should fit mail when substring of existing word is given");
    }

    @Test
    void testDoesConditionFitMailSenderEmptyCondition() {
        String text = "no match sentence";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.doesConditionFitMail(createMailFrom("empty sentence")),
            "Condition should fit mail when condition is empty");
    }

    @Test
    void testDoesConditionFitMailOneOutOfOne() {
        String text = "subject-includes: mjt";
        subjectIncludesCondition.setValues(text);

        assertTrue(subjectIncludesCondition.doesConditionFitMail(createMailFrom("This sentence includes mjt")),
            "Condition should fit mail when one out of one subject keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfOne() {
        String text = "subject-includes: mjt";
        subjectIncludesCondition.setValues(text);

        assertFalse(subjectIncludesCondition.doesConditionFitMail(createMailFrom("empty sentence")),
            "Condition should not fit mail when zero out of one subject keywords match");
    }

    @Test
    void testDoesConditionFitMailEmptySubject() {
        String text = "subject-includes: mjt";
        subjectIncludesCondition.setValues(text);

        assertFalse(subjectIncludesCondition.doesConditionFitMail(createMailFrom("")),
            "Condition should not fit mail when the subject is empty");
    }
}

