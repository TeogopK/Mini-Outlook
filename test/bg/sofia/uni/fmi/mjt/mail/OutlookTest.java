package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutlookTest {
    private Outlook outlook = new Outlook();

    @Test
    void testAddNewAccountAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(null, "test@email.com"),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testAddNewAccountAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("", "test@email.com"),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testAddNewAccountAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(" ", "test@email.com"),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testAddNewAccountEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("testName", null),
            "IllegalArgumentException expected to be thrown when email is null");
    }

    @Test
    void testAddNewAccountEmailIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("testName", ""),
            "IllegalArgumentException expected to be thrown when email is empty");
    }

    @Test
    void testAddNewAccountEmailIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("testName", " "),
            "IllegalArgumentException expected to be thrown when email is blank");
    }

    @Test
    void testAddNewAccountNameExists() {
        String name = "testName";
        outlook.addNewAccount(name, "test@email.com");

        assertThrows(AccountAlreadyExistsException.class, () -> outlook.addNewAccount(name, "other@email.com"),
            "AccountAlreadyExistsException expected to be thrown when account name already exists");
    }

    @Test
    void testAddNewAccountEmailExists() {
        String email = "test@email.com";
        outlook.addNewAccount("testName", email);

        assertThrows(AccountAlreadyExistsException.class, () -> outlook.addNewAccount("otherName", email),
            "AccountAlreadyExistsException expected to be thrown when email already exists");
    }

    @Test
    void testAddNewAccountSuccessful() {
        outlook.addNewAccount("testName", "test@email.com");

        assertEquals(1, outlook.getSystemAccounts().size(), "Account expected to be added successfully");
    }

    @Test
    void testCreateFolderAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(null, "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testCreateFolderAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("", "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testCreateFolderAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(" ", "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testCreateFolderPathIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("testName", null),
            "IllegalArgumentException expected to be thrown when path is null");
    }

    @Test
    void testCreateFolderPathIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("testName", ""),
            "IllegalArgumentException expected to be thrown when path is empty");
    }

    @Test
    void testCreateFolderPathIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("testName", " "),
            "IllegalArgumentException expected to be thrown when path is blank");
    }

    @Test
    void testCreateFolderAccountDoesNotExist() {
        assertThrows(AccountNotFoundException.class, () -> outlook.createFolder("testName", "/inbox/new"),
            "AccountNotFoundException expected to be thrown when account does not exist");
    }

    @Test
    void testCreateFolderInvalidPathInbox() {
        String name = "testName";
        outlook.addNewAccount(name, "test@gmail.com");

        assertThrows(InvalidPathException.class, () -> outlook.createFolder(name, "/inbox"),
            "InvalidPathException expected to be thrown when path is illegal");
    }

    @Test
    void testCreateFolderInvalidPathNotCreated() {
        String name = "testName";
        outlook.addNewAccount(name, "test@gmail.com");

        assertThrows(InvalidPathException.class, () -> outlook.createFolder(name, "/inbox/notFound/new"),
            "InvalidPathException expected to be thrown when path is illegal");
    }

    @Test
    void testCreateFolderFolderAlreadyExists() {
        String name = "testName";
        outlook.addNewAccount(name, "test@gmail.com");
        outlook.createFolder(name, "/inbox/new");

        assertThrows(FolderAlreadyExistsException.class, () -> outlook.createFolder(name, "/inbox/new"),
            "FolderAlreadyExistsException expected to be thrown");
    }

    @Test
    void testCreateFolderTwoDeep() {
        outlook.addNewAccount("testName", "test@gmail.com");
        outlook.createFolder("testName", "/inbox/new");
        outlook.createFolder("testName", "/inbox/new/second");

        assertTrue(
            outlook.getSystemAccounts().get("testName").directory().getDirectoryPaths().containsKey("/inbox/new"),
            "Father path expected to be added in account directories");

        assertTrue(outlook.getSystemAccounts().get("testName").directory().getDirectoryPaths()
            .containsKey("/inbox/new/second"), "Child path expected to be added in account directories");
    }

    @Test
    void testCreateFolderTwoAccounts() {
        String path = "/inbox/new";
        outlook.addNewAccount("testName", "test@gmail.com");
        outlook.createFolder("testName", path);

        outlook.addNewAccount("anotherName", "another@gmail.com");
        outlook.createFolder("anotherName", path);

        assertEquals(2, outlook.getSystemAccounts().size(), "Number of accounts expected to be two");

        assertTrue(outlook.getSystemAccounts().get("testName").directory().getDirectoryPaths().containsKey(path),
            "Path expected to be added in account directories");

        assertTrue(outlook.getSystemAccounts().get("anotherName").directory().getDirectoryPaths().containsKey(path),
            "Path expected to be added in account directories");
    }

    @Test
    void testAddRuleAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule(null, "/inbox/new", "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testAddRuleAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("", "/inbox/new", "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testAddRuleAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule(" ", "/inbox/new", "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testAddRuleFolderPathIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", null, "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when folder path is null");
    }

    @Test
    void testAddRuleFolderPathIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "", "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when folder path is empty");
    }

    @Test
    void testAddRuleFolderPathIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", " ", "testDefinition", 1),
            "IllegalArgumentException expected to be thrown when folder path is blank");
    }

    @Test
    void testAddRuleRuleDefinitionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", null, 1),
            "IllegalArgumentException expected to be thrown when rule definition is null");
    }

    @Test
    void testAddRuleRuleDefinitionIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", "", 1),
            "IllegalArgumentException expected to be thrown when rule definition is empty");
    }

    @Test
    void testAddRuleRuleDefinitionIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", " ", 1),
            "IllegalArgumentException expected to be thrown when rule definition is blank");
    }

    @Test
    void testAddRulePriorityIsZero() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", "definition", 0),
            "IllegalArgumentException expected to be thrown when priority is zero");
    }

    @Test
    void testAddRulePriorityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", "definition", -6),
            "IllegalArgumentException expected to be thrown when priority is negative");
    }

    @Test
    void testAddRulePriorityIsAbove10() {
        assertThrows(IllegalArgumentException.class, () -> outlook.addRule("name", "/inbox/new", "definition", 17),
            "IllegalArgumentException expected to be thrown when priority is above 10");
    }

    @Test
    void testAddRuleAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> outlook.addRule("name", "/inbox/new", "definition", 5),
            "AccountNotFoundException expected to be thrown when account is not found");
    }

    @Test
    void testAddRuleFolderNotFound() {
        outlook.addNewAccount("name", "email@abv.bg");

        assertThrows(FolderNotFoundException.class, () -> outlook.addRule("name", "/inbox/new", "definition", 1),
            "FolderNotFoundException expected to be thrown when folder does not exist in account directory");
    }

    @Test
    void testAddRuleRuleDefinitionAlreadyDefined() {
        String definition = "subject-includes: mjt, izpit" + System.lineSeparator() + "subject-includes: other";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        assertThrows(RuleAlreadyDefinedException.class, () -> outlook.addRule("name", "/inbox/new", definition, 8),
            "RuleAlreadyDefinedException expected to be thrown when rule definition has duplicate");
    }

    @Test
    void testAddRuleRuleAlreadyDefinedConflict() {
        String definition = "subject-includes: mjt, izpit";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        outlook.addRule("name", "/inbox/new", definition, 8);

        assertThrows(RuleAlreadyDefinedException.class, () -> outlook.addRule("name", "/inbox/new", definition, 8),
            "RuleAlreadyDefinedException expected to be thrown when two rules are in conflict");
    }

    @Test
    void testAddRuleRuleAlreadyDefinedAlmostConflict() {
        String definition = "subject-includes: mjt, izpit";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        outlook.addRule("name", "/inbox/new", definition, 8);

        assertDoesNotThrow(() -> outlook.addRule("name", "/inbox/new", definition, 5),
            "Adding rules with same definition and path, but different priorities expected to work");

        assertEquals(2, outlook.getSystemAccounts().get("name").rules().size(), "Expected two rules to be added");
    }

    @Test
    void testAddRuleSuccessful() {
        String definition = "subject-includes: mjt, izpit";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        assertDoesNotThrow(() -> outlook.addRule("name", "/inbox/new", definition, 5),
            "Rule expected to be added successfully");

        assertEquals(1, outlook.getSystemAccounts().get("name").rules().size(), "Expected a rule to be added");
    }

    private Mail getMail1() {
        Account account1 = new Account("ivan@fmi.bg", "Ivan Ivanov");
        Set<String> recipients1 = Set.of("pesho@gmail.com", "gosho@gmail.com");
        LocalDateTime received1 = LocalDateTime.of(2017, 1, 14, 10, 34);

        return new Mail(account1, recipients1, "This email is for our mjt izpit", "It's gonna take place in 2022!",
            received1);
    }

    private Mail getMail2() {
        Account account2 = new Account("test@email.com", "Test Namov");
        Set<String> recipients2 = Set.of("another@gmail.com");
        LocalDateTime received2 = LocalDateTime.of(1878, 3, 3, 5, 55);

        return new Mail(account2, recipients2, "This is another subject", "Other body", received2);
    }

    Mail mail1 = getMail1();
    Mail mail2 = getMail2();

    @Test
    void testAddRuleMoveMailsTwoFromTwo() {
        String definition = "subject-includes: This";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of(mail1, mail2);

        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail2);

        assertDoesNotThrow(() -> outlook.addRule("name", "/inbox/new", definition, 5),
            "Rule expected to be added successfully");

        assertEquals(0, outlook.getSystemAccounts().get("name").directory().getMailsFromInbox().size(),
            "Expected no mails to remain in inbox");

        assertEquals(mails, outlook.getSystemAccounts().get("name").directory().getMails("/inbox/new"),
            "Expected two mails to have been moved in the path");
    }

    @Test
    void testAddRuleMoveMailsOneFromTwo() {
        String definition = "subject-includes: mjt";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of(mail1);

        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail2);

        assertDoesNotThrow(() -> outlook.addRule("name", "/inbox/new", definition, 5),
            "Rule expected to be added successfully");

        assertEquals(1, outlook.getSystemAccounts().get("name").directory().getMailsFromInbox().size(),
            "Expected one mail to remain in inbox");

        assertEquals(mails, outlook.getSystemAccounts().get("name").directory().getMails("/inbox/new"),
            "Expected one mail to have been moved in the path");
    }

    @Test
    void testAddRuleMoveMailsZeroFromTwo() {
        String definition = "subject-or-body-includes: help";
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of();

        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail2);

        assertDoesNotThrow(() -> outlook.addRule("name", "/inbox/new", definition, 5),
            "Rule expected to be added successfully");

        assertEquals(2, outlook.getSystemAccounts().get("name").directory().getMailsFromInbox().size(),
            "Expected no mails to remain in inbox");

        assertEquals(mails, outlook.getSystemAccounts().get("name").directory().getMails("/inbox/new"),
            "Expected two mails to have been moved in the path");
    }

    @Test
    void testGetMailsFromFolderAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(null, "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testGetMailsFromFolderAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("", "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testGetMailsFromFolderAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(" ", "/inbox/new"),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testGetMailsFromFolderPathIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("testName", null),
            "IllegalArgumentException expected to be thrown when path is null");
    }

    @Test
    void testGetMailsFromFolderPathIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("testName", ""),
            "IllegalArgumentException expected to be thrown when path is empty");
    }

    @Test
    void testGetMailsFromFolderPathIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("testName", " "),
            "IllegalArgumentException expected to be thrown when path is blank");
    }

    @Test
    void testGetMailsFromFolderAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> outlook.getMailsFromFolder("name", "/inbox/new"),
            "AccountNotFoundException expected to be thrown when account is not found");
    }

    @Test
    void testGetMailsFromFolderFolderNotFound() {
        outlook.addNewAccount("name", "email@abv.bg");

        assertThrows(FolderNotFoundException.class, () -> outlook.getMailsFromFolder("name", "/inbox/new"),
            "FolderNotFoundException expected to be thrown when folder does not exist in account directory");
    }

    @Test
    void testGetMailsFromFolderTwoMails() {
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of(mail1, mail2);

        outlook.getSystemAccounts().get("name").directory().addEmail("/inbox/new", mail1);
        outlook.getSystemAccounts().get("name").directory().addEmail("/inbox/new", mail2);

        assertEquals(mails, outlook.getMailsFromFolder("name", "/inbox/new"), "Expected to get two mails from path");
    }

    @Test
    void testGetMailsFromFolderTwoMailsFromInbox() {
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of(mail1, mail2);

        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail2);

        assertEquals(mails, outlook.getMailsFromFolder("name", "/inbox"), "Expected to get two mails from inbox");
    }

    @Test
    void testGetMailsFromFolderOneMailFromInbox() {
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of(mail1);

        outlook.getSystemAccounts().get("name").directory().addEmailToInbox(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmail("/inbox/new", mail2);

        assertEquals(mails, outlook.getMailsFromFolder("name", "/inbox"), "Expected to get one mails from inbox");
    }

    @Test
    void testGetMailsFromFolderZeroMailFromInbox() {
        outlook.addNewAccount("name", "email@abv.bg");
        outlook.createFolder("name", "/inbox/new");

        Set<Mail> mails = Set.of();

        outlook.getSystemAccounts().get("name").directory().addEmail("/inbox/new", mail1);
        outlook.getSystemAccounts().get("name").directory().addEmail("/inbox/new", mail2);

        assertEquals(mails, outlook.getMailsFromFolder("name", "/inbox"),
            "Expected to get zero mails from inbox when inbox is empty");
    }

    @Test
    void testGetMailsFromFolderFromSent() {
        outlook.addNewAccount("name", "email@abv.bg");

        Set<Mail> mails = Set.of(mail1, mail2);

        outlook.getSystemAccounts().get("name").directory().addEmailToSent(mail1);
        outlook.getSystemAccounts().get("name").directory().addEmailToSent(mail2);

        assertEquals(mails, outlook.getMailsFromFolder("name", "/sent"), "Expected to get two mails from sent");
    }

    @Test
    void testReceiveMailAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail(null, "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testReceiveMailAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("", "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testReceiveMailAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail(" ", "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testReceiveMailMailMetadataIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", null, "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is null");
    }

    @Test
    void testReceiveMailMailMetadataIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", "", "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is empty");
    }

    @Test
    void testReceiveMailMailMetadataIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", " ", "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is blank");
    }

    @Test
    void testReceiveMailMailContentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", "metaData", null),
            "IllegalArgumentException expected to be thrown when mail content is null");
    }

    @Test
    void testReceiveMailMailContentIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", "metaData", ""),
            "IllegalArgumentException expected to be thrown when mail content is empty");
    }

    @Test
    void testReceiveMailMailContentIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.receiveMail("name", "metaData", " "),
            "IllegalArgumentException expected to be thrown when mail content is blank");
    }

    @Test
    void testReceiveMailAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> outlook.receiveMail("name", "metaData", "metaContent"),
            "AccountNotFoundException expected to be thrown when account is not found");
    }

    @Test
    void testSendMailAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail(null, "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is null");
    }

    @Test
    void testSendMailAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("", "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is empty");
    }

    @Test
    void testSendMailAccountNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail(" ", "metaData", "content"),
            "IllegalArgumentException expected to be thrown when account name is blank");
    }

    @Test
    void testSendMailMailMetadataIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", null, "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is null");
    }

    @Test
    void testSendMailMailMetadataIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", "", "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is empty");
    }

    @Test
    void testSendMailMailMetadataIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", " ", "content"),
            "IllegalArgumentException expected to be thrown when mail metadata is blank");
    }

    @Test
    void testSendMailMailContentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", "metaData", null),
            "IllegalArgumentException expected to be thrown when mail content is null");
    }

    @Test
    void testSendMailMailContentIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", "metaData", ""),
            "IllegalArgumentException expected to be thrown when mail content is empty");
    }

    @Test
    void testSendMailMailContentIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> outlook.sendMail("name", "metaData", " "),
            "IllegalArgumentException expected to be thrown when mail content is blank");
    }

    @Test
    void testSendMailAccountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> outlook.sendMail("name", "metaData", "metaContent"),
            "AccountNotFoundException expected to be thrown when account is not found");
    }

    private String metaData = getMetaData();

    private String getMetaData() {
        return "this is not a key: something" + System.lineSeparator() + "sender: sender@gmail.com" +
            System.lineSeparator() + "subject: This is the subject MJT!" + System.lineSeparator() +
            "recipients: receiver@gmail.com, other@gmail.com," + System.lineSeparator() + "received: 2022-12-08 14:14" +
            System.lineSeparator();
    }

    private Mail getMailReceiver() {
        Account account = new Account("sender@gmail.com", "sender");
        Set<String> recipients = Set.of("receiver@gmail.com", "other@gmail.com");
        LocalDateTime received = LocalDateTime.of(2022, 12, 8, 14, 14);

        return new Mail(account, recipients, "This is the subject MJT!",
            "This is a sentence in the mailContent and it is the body, cat", received);
    }

    private Mail mailReceiver = getMailReceiver();

    @Test
    void testReceiveMailNoRules() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");

        outlook.receiveMail("receiver", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail to land in inbox correctly");
    }

    @Test
    void testReceiveMailOneFromTwoFittingRule() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");

        outlook.createFolder("receiver", "/inbox/first");
        outlook.createFolder("receiver", "/inbox/second");
        outlook.addRule("receiver", "/inbox/first", "subject-includes: MJT", 5);
        outlook.addRule("receiver", "/inbox/second", "subject-or-body-includes: cat", 3);

        outlook.receiveMail("receiver", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail inbox to be empty");

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox/second"),
            "Expected mail to land in path correctly");
    }

    @Test
    void testReceiveMailOneFromTwoFittingOutOfThreeRule() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");

        outlook.createFolder("receiver", "/inbox/first");
        outlook.createFolder("receiver", "/inbox/second");
        outlook.createFolder("receiver", "/inbox/third");

        outlook.addRule("receiver", "/inbox/third", "subject-includes: not me", 1);
        outlook.addRule("receiver", "/inbox/first", "subject-includes: MJT", 5);
        outlook.addRule("receiver", "/inbox/second", "subject-or-body-includes: cat", 3);

        outlook.receiveMail("receiver", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail inbox to be empty");

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox/second"),
            "Expected mail to land in path correctly");
    }

    @Test
    void testSendMailNoRecipients() {
        outlook.addNewAccount("sender", "sender@gmail.com");

        outlook.sendMail("sender", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(mails, outlook.getMailsFromFolder("sender", "/sent"),
            "Expected mail to land in sent of sender correctly");
    }

    @Test
    void testSendMailOneRecipient() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");
        outlook.addNewAccount("noOne", "noOne@gmail.com");

        outlook.sendMail("sender", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail to land in inbox of receiver correctly");

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("receiver", "/sent"),
            "Expected mail not to land in sent of receiver");

        assertEquals(mails, outlook.getMailsFromFolder("sender", "/sent"),
            "Expected mail to land in sent of sender correctly");

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("sender", "/inbox"),
            "Expected mail not to land in inbox of sender");

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("noOne", "/inbox"),
            "Expected mail not to land in other accounts inbox");

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("noOne", "/sent"),
            "Expected mail not to land in other accounts sent");
    }

    @Test
    void testSendMailTwoRecipient() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");
        outlook.addNewAccount("other", "other@gmail.com");

        outlook.sendMail("sender", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail to land in inbox of receiver correctly");

        assertEquals(mails, outlook.getMailsFromFolder("sender", "/sent"),
            "Expected mail to land in sent of sender correctly");

        assertEquals(mails, outlook.getMailsFromFolder("other", "/inbox"),
            "Expected mail to land in sent of second receiver correctly");
    }

    @Test
    void testSendMailRule() {
        outlook.addNewAccount("receiver", "receiver@gmail.com");
        outlook.addNewAccount("sender", "sender@gmail.com");

        outlook.createFolder("receiver", "/inbox/first");
        outlook.createFolder("receiver", "/inbox/second");

        outlook.addRule("receiver", "/inbox/first", "subject-includes: MJT", 5);
        outlook.addRule("receiver", "/inbox/second", "subject-or-body-includes: cat", 3);

        outlook.sendMail("sender", metaData, "This is a sentence in the mailContent and it is the body, cat");
        Set<Mail> mails = Set.of(mailReceiver);

        assertEquals(Collections.EMPTY_SET, outlook.getMailsFromFolder("receiver", "/inbox"),
            "Expected mail inbox to be empty");

        assertEquals(mails, outlook.getMailsFromFolder("receiver", "/inbox/second"),
            "Expected mail to land in path correctly");

        assertEquals(mails, outlook.getMailsFromFolder("sender", "/sent"),
            "Expected mail to land in sent of sender correctly");
    }
}
