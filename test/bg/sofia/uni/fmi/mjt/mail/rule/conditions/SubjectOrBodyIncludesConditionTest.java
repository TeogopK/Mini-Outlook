package bg.sofia.uni.fmi.mjt.mail.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.Account;
import bg.sofia.uni.fmi.mjt.mail.Mail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubjectOrBodyIncludesConditionTest {

    private SubjectOrBodyIncludesCondition subjectOrBodyIncludes = new SubjectOrBodyIncludesCondition();

    @Test
    void testSetValuesOneLiner() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        Set<String> wanted = Set.of("mjt", "izpit", "2022");

        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from oneliner");
    }

    @Test
    void testSetValuesNoMatch() {
        String text = "test sentence no match";
        subjectOrBodyIncludes.setValues(text);

        assertIterableEquals(Collections.emptySet(), subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly when there is no match");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEnd() {
        String text = "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");

        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from oneliner with line separator at the end");
    }

    @Test
    void testSetValuesOneLinerLineSeparatorAtEndAndStart() {
        String text = System.lineSeparator() + "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from oneliner surrounded with line separators");
    }

    @Test
    void testSetValuesOneLinerNoSpace() {
        String text = System.lineSeparator() + "subject-or-body-includes:mjt, izpit, 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from oneliner with no space after key");
    }

    @Test
    void testSetValuesOneLinerSpacedWords() {
        String text = System.lineSeparator() + "subject-or-body-includes: mjt, izpit 2022" + System.lineSeparator();
        Set<String> wanted = Set.of("mjt", "izpit 2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from oneliner with spaced keywords");
    }

    @Test
    void testSetValuesMultipleLinesSubjectOrBodyAtTheEnd() {
        String text =
            "subject-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesSubjectOrBodyAtTheBeginning() {
        String text = "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-include: izpit" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testSetValuesMultipleLinesSubjectOrBodyInTheMiddle() {
        String text =
            "subject-includes: izpit" + System.lineSeparator() + "subject-or-body-includes: mjt, izpit, 2022" +
                System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator();

        Set<String> wanted = Set.of("mjt", "izpit", "2022");
        subjectOrBodyIncludes.setValues(text);

        assertEquals(wanted, subjectOrBodyIncludes.values,
            "Condition text expected to be parsed correctly from multiple lines text");
    }

    @Test
    void testIsTextCorrectMultipleLines() {
        String text = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator();

        assertTrue(subjectOrBodyIncludes.isTextCorrect(text), "Condition text is correct from multiple lines text");
    }

    @Test
    void testIsTextCorrectOneLine() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        assertTrue(subjectOrBodyIncludes.isTextCorrect(text), "Condition text is correct from oneliner");
    }

    @Test
    void testIsTextCorrectMultipleLinesDuplicate() {
        String text = "subject-or-body-includes: mjt" + System.lineSeparator() + "subject-includes: mjt, izpit, 2022" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator() + "from: stoyo@fmi.bg";

        assertFalse(subjectOrBodyIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false from multiple lines text with duplicate");
    }

    @Test
    void testIsTextCorrectNextToOneAnother() {
        String text = "subject-or-body-includes: mjt, izpit, 2022" + "subject-or-body-includes: mjt, izpit, 2022";

        assertFalse(subjectOrBodyIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNextToOneAnotherWithLineSeparator() {
        String text = "subject-or-body-includes: mjt, izpit, 2022" + System.lineSeparator() +
            "subject-or-body-includes: mjt, izpit, 2022";

        assertFalse(subjectOrBodyIncludes.isTextCorrect(text),
            "Correctness of condition text expected to be false when two duplicates are next to each other");
    }

    @Test
    void testIsTextCorrectNoMatch() {
        String text = "this is a no match sentence";

        assertTrue(subjectOrBodyIncludes.isTextCorrect(text), "Condition text is correct when no match is found");
    }

    @Test
    void testIsEmptyConditionNoMatch() {
        String text = "subject-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.isEmptyCondition(),
            "Condition expected to be marked as empty when there is no match");
    }

    @Test
    void testIsEmptyConditionMatch() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.isEmptyCondition(),
            "Condition expected not to be marked as empty when there is a match");
    }

    private Mail createMailFrom(String subject, String body) {
        Account sender = new Account("test@email.com", "Test Name");
        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");
        LocalDateTime received = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(sender, recipients, subject, body, received);
    }

    @Test
    void testDoesConditionFitMailThreeOutOfThreeBody() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.doesConditionFitMail(
                createMailFrom("subject", "This sentence includes mjt, also izpit and 2022")),
            "Condition should fit mail when three out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailThreeOutOfThreeSplit() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.doesConditionFitMail(
                createMailFrom("Subject sentence includes mjt", "and this includes izpit and 2022")),
            "Condition should fit mail when three out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfThreeBody() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.doesConditionFitMail(
                createMailFrom("subject", "This sentence includes mjt but only izpit")),
            "Condition should not fit mail when two out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfThreeSplit() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(
            subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("This sentence includes mjt", "And this izpit")),
            "Condition should not fit mail when two out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailTwoOutOfTwoSplit() {
        String text = "subject-or-body-includes: mjt, izpit";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(
            subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("This sentence includes mjt", "And this izpit")),
            "Condition should fit mail when two out of two keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfThree() {
        String text = "subject-or-body-includes: mjt, izpit, 2022";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("subject", "empty sentence")),
            "Condition should not fit mail when zero out of three keywords match");
    }

    @Test
    void testDoesConditionFitMailSenderEmptyCondition() {
        String text = "no match sentence";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("subject", "empty sentence")),
            "Condition should fit mail when condition is empty");
    }

    @Test
    void testDoesConditionFitMailOneOutOfOneBody() {
        String text = "subject-or-body-includes: mjt";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("subject", "This sentence includes mjt")),
            "Condition should fit mail when one out of one keywords match");
    }

    @Test
    void testDoesConditionFitMailOneOutOfOneSubject() {
        String text = "subject-or-body-includes: mjt";
        subjectOrBodyIncludes.setValues(text);

        assertTrue(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("This sentence includes mjt", "body")),
            "Condition should fit mail when one out of one keywords match");
    }

    @Test
    void testDoesConditionFitMailNoneOfOne() {
        String text = "subject-or-body-includes: mjt";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("subject", "empty sentence")),
            "Condition should not fit mail when zero out of one keywords match");
    }

    @Test
    void testDoesConditionFitMailEmptySubjectAndBody() {
        String text = "subject-or-body-includes: mjt";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("", "")),
            "Condition should not fit mail when the subject and body are empty");
    }

    @Test
    void testDoesConditionFitMailEmptyBody() {
        String text = "subject-or-body-includes: mjt";
        subjectOrBodyIncludes.setValues(text);

        assertFalse(subjectOrBodyIncludes.doesConditionFitMail(createMailFrom("subject", "")),
            "Condition should not fit mail when the body is empty");
    }
}

