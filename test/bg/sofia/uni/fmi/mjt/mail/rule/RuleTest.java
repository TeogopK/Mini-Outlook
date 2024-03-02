package bg.sofia.uni.fmi.mjt.mail.rule;

import bg.sofia.uni.fmi.mjt.mail.Account;
import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RuleTest {

    private Rule rule;

    @Test
    void testFourDefinitions() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator() +
            "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();

        rule = new Rule("path", definition, 1);

        var rules = rule.getConditions();
        for (var def : rules) {
            assertFalse(def.isEmptyCondition(), "Expected all four rule definitions to be parsed in the rule");
        }
    }

    @Test
    void testDuplicateDefinition() {
        String definition = "subject-includes: mjt, izpit, 2022" + System.lineSeparator() + "from: stoyo@fmi.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit" + System.lineSeparator() +
            "subject-includes: mjt" + System.lineSeparator();

        assertThrows(RuleAlreadyDefinedException.class, () -> new Rule("path", definition, 1),
            "Exception expected to be thrown when rule definition has a duplicate");
    }

    private final Mail mail = getMail();

    private Mail getMail() {
        Account sender = new Account("stoyo@fmi.bg", "Test Name");
        Set<String> recipients = Set.of("pesho@gmail.com", "gosho@gmail.com");
        LocalDateTime received = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(sender, recipients, "This email is for our mjt izpit", "It's gonna take place in 2022!",
            received);
    }

    @Test
    void testDoesRuleFitMailFourOutOfFour() {
        String definition =
            "subject-includes: mjt, izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-or-body-includes: izpit, 2022" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();

        rule = new Rule("path", definition, 1);

        assertTrue(rule.doesRuleFitMail(mail), "Expected the rule with all 4 matching definitions to match the email");
    }

    @Test
    void testDoesRuleFitMailThreeOutOfFour() {
        String definition = "subject-includes: mjt, izpit" + System.lineSeparator() + "from: someoneElse@email.bg" +
            System.lineSeparator() + "subject-or-body-includes: izpit, 2022" + System.lineSeparator() +
            "recipients-includes: pesho@gmail.com, else@gmail.com" + System.lineSeparator();

        rule = new Rule("path", definition, 1);

        assertFalse(rule.doesRuleFitMail(mail),
            "Expected the rule with 3 out of 4 matching definitions not to match the email");
    }

    @Test
    void testDoesRuleFitMailThreeOutOfThree() {
        String definition =
            "subject-includes: mjt, izpit" + System.lineSeparator() + "from: stoyo@fmi.bg" + System.lineSeparator() +
                "subject-or-body-includes: izpit, 2022" + System.lineSeparator();

        rule = new Rule("path", definition, 1);

        assertTrue(rule.doesRuleFitMail(mail),
            "Expected the rule with 3 out of 3 matching definitions to match the email");
    }

}
