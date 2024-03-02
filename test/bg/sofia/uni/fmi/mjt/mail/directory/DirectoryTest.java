package bg.sofia.uni.fmi.mjt.mail.directory;

import bg.sofia.uni.fmi.mjt.mail.Account;
import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.rule.Rule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DirectoryTest {
    private final Directory directory = new Directory();

    @Test
    void testDirectoryHasRoot() {
        String path = "/";
        assertTrue(directory.getDirectoryPaths().containsKey(path),
            String.format("Expected (%s) path to exist in directory", path));
    }

    @Test
    void testDirectoryHasSent() {
        String path = "/sent";
        assertTrue(directory.getDirectoryPaths().containsKey(path),
            String.format("Expected (%s) path to exist in directory", path));
    }

    @Test
    void testDirectoryHasInbox() {
        String path = "/inbox";
        assertTrue(directory.getDirectoryPaths().containsKey(path),
            String.format("Expected (%s) path to exist in directory", path));
    }

    @Test
    void testAddPathPathIsRoot() {
        String path = "/";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not be root only", path));
    }

    @Test
    void testAddPathPathIsSent() {
        String path = "/sent";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not be sent at all", path));
    }

    @Test
    void testAddPathPathIsInbox() {
        String path = "/inbox";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not be inbox only", path));
    }

    @Test
    void testAddPathPathEndsInSeparator() {
        String path = "/inbox/";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not end in separator", path));
    }

    @Test
    void testAddPathPathDoesNotExistsFather() {
        String path = "/inbox/notFound/new";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, father path does not exists", path));
    }

    @Test
    void testAddPathPathIsInSent() {
        String path = "/sent/new";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not create in sent", path));
    }

    @Test
    void testAddPathPathIsInRoot() {
        String path = "/new";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not create in root", path));
    }

    @Test
    void testAddPathPathIncludesTwoHuggingSeparators() {
        String path = "/inbox//new";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not have two separators next to one another", path));
    }

    @Test
    void testAddPathPathIncludesHuggingSeparators() {
        String path = "/inbox///new";
        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, can not have separators next to one another", path));
    }

    @Test
    void testAddPathPathIsCorrect() {
        String path = "/inbox/new";
        assertDoesNotThrow(() -> directory.addPath(path), String.format("Path (%s) should be a correct path", path));
        assertEquals(4, directory.getDirectoryPaths().size(), "Incorrect number of paths in directory");
    }

    @Test
    void testAddPathPathIsAlreadyExisting() {
        String path = "/inbox/new";
        directory.addPath(path);
        assertThrows(FolderAlreadyExistsException.class, () -> directory.addPath(path),
            String.format("Path (%s) already exists, expected exception to be thrown", path));
    }

    @Test
    void testAddPathPathIsCorrectTwoFathers() {
        directory.addPath("/inbox/new");

        String path = "/inbox/new/second";

        assertDoesNotThrow(() -> directory.addPath(path), String.format("Path (%s) should be a correct path", path));
        assertEquals(5, directory.getDirectoryPaths().size(), "Incorrect number of paths in directory");
    }

    @Test
    void testAddPathPathIsMissingFather() {
        directory.addPath("/inbox/new");

        String path = "/inbox/new/second/child";

        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, father does not exists", path));
    }

    @Test
    void testAddPathPathIsMissingFatherAfterInbox() {
        directory.addPath("/inbox/second");

        String path = "/inbox/missing/second/child";

        assertThrows(InvalidPathException.class, () -> directory.addPath(path),
            String.format("Path (%s) is not a correct path, father does not exists", path));
    }

    @Test
    void testGetMailsPathIsNotExisting() {
        String path = "/inbox/where";
        assertThrows(FolderNotFoundException.class, () -> directory.getMails(path),
            "FolderNotFoundException expected to be thrown when path is not found");
    }

    @Test
    void testGetMailsNoMails() {
        String path = "/inbox/child";

        directory.addPath(path);

        assertIterableEquals(Collections.EMPTY_SET, directory.getMails(path),
            "Expected empty collection when path has no emails");
    }

    @Test
    void testGetMailsFromRoot() {
        String path = "/";

        assertIterableEquals(Collections.EMPTY_SET, directory.getMails(path),
            "Expected empty collection when path is root");
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

    private final Mail mail1 = getMail1();
    private final Mail mail2 = getMail2();

    @Test
    void testGetMailsOneDeepTwoEmails() {
        String path = "/inbox/child";
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        Set<Mail> mails = Set.of(mail1, mail2);

        assertEquals(mails, directory.getMails(path), "Expected mails to be returned successfully");
    }

    @Test
    void testGetMailsTwoDeepTwoEmails() {
        String path = "/inbox/child/new";
        directory.addPath("/inbox/child");
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        Set<Mail> mails = Set.of(mail1, mail2);

        assertEquals(mails, directory.getMails(path), "Expected mails to be returned successfully");
    }

    @Test
    void testGetMailsOneDeepHasChildrenZeroEmails() {
        String path = "/inbox/child/new";
        directory.addPath("/inbox/child");
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        Set<Mail> mails = Set.of();

        assertEquals(mails, directory.getMails("/inbox/child"), "Expected mails to be returned successfully");
    }

    @Test
    void testGetMailsFromInboxTwoMails() {
        String path = "/inbox";

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        Set<Mail> mails = Set.of(mail1, mail2);

        assertEquals(mails, directory.getMailsFromInbox(), "Expected mails to be returned successfully from inbox");
    }

    @Test
    void testGetMailsFromInboxZeroMails() {
        Set<Mail> mails = Set.of();

        assertEquals(mails, directory.getMailsFromInbox(),
            "Expected empty set of mails to be returned when inbox is empty");
    }

    @Test
    void testGetMailsFromInboxHasChildrenZeroEmails() {
        String path = "/inbox/child";
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        Set<Mail> mails = Set.of();

        assertEquals(mails, directory.getMailsFromInbox(), "Expected mails to be returned successfully");
    }

    @Test
    void testAddEmailOneMail() {
        String path = "/inbox/child";
        directory.addPath(path);

        directory.addEmail(path, mail1);

        assertEquals(1, directory.getDirectoryPaths().get(path).size(),
            "Expected mail to be added successfully in path");
    }

    @Test
    void testAddEmailTwoMails() {
        String path = "/inbox/child";
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        assertEquals(2, directory.getDirectoryPaths().get(path).size(),
            "Expected mails to be added successfully in path");
    }

    @Test
    void testAddEmailFolderNotFound() {
        String path = "/inbox/child";

        assertThrows(FolderNotFoundException.class, () -> directory.addEmail(path, mail1),
            "Expected exception when path is not found");
    }

    @Test
    void testAddEmailTwoMailsInChild() {
        String path = "/inbox/child";
        directory.addPath(path);

        directory.addEmail(path, mail1);
        directory.addEmail(path, mail2);

        assertEquals(0, directory.getDirectoryPaths().get("/inbox").size(), "Expected no mails to be found in inbox");
    }

    @Test
    void testAddEmailToInboxTwoMails() {
        String path = "/inbox";

        directory.addEmailToInbox(mail1);
        directory.addEmailToInbox(mail2);

        assertEquals(2, directory.getDirectoryPaths().get(path).size(),
            "Expected mails to be added successfully in inbox");
    }

    @Test
    void testAddEmailToSentTwoMails() {
        String path = "/sent";

        directory.addEmailToSent(mail1);
        directory.addEmailToSent(mail2);

        assertEquals(2, directory.getDirectoryPaths().get(path).size(),
            "Expected mails to be added successfully in sent");
    }

    final String definition1 =
        "subject-includes: mjt, izpit" + System.lineSeparator() + "from: ivan@fmi.bg" + System.lineSeparator() +
            "subject-or-body-includes: izpit, 2022" + System.lineSeparator() +
            "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator();

    final Rule rule1 = new Rule("/inbox/new", definition1, 1);
    final Rule rule2 = new Rule("/inbox/second", "subject-includes: This", 2);
    final Rule rule3 = new Rule("/inbox/third", "from: camel@dontexist.com", 2);

    @Test
    void testMoveEmailsFromInboxByRulePathNotFound() {
        assertThrows(FolderNotFoundException.class, () -> directory.moveEmailsFromInboxByRule(rule1),
            "Expected exception when path is not found");
    }

    @Test
    void testMoveEmailsFromInboxByRuleOneOfTwo() {
        String path = rule1.getFolderPath();

        directory.addPath(path);

        directory.addEmailToInbox(mail1);
        directory.addEmailToInbox(mail2);

        Set<Mail> mails = Set.of(mail1);

        directory.moveEmailsFromInboxByRule(rule1);

        assertEquals(1, directory.getDirectoryPaths().get("/inbox").size(),
            "Expected mails to be removed successfully from inbox");

        assertEquals(mails, directory.getDirectoryPaths().get(path), "Expected mails to be added successfully in path");
    }

    @Test
    void testMoveEmailsFromInboxByRuleTwoOfTwo() {
        String path = rule2.getFolderPath();

        directory.addPath(path);

        directory.addEmailToInbox(mail1);
        directory.addEmailToInbox(mail2);

        Set<Mail> mails = Set.of(mail1, mail2);

        directory.moveEmailsFromInboxByRule(rule2);

        assertEquals(0, directory.getDirectoryPaths().get("/inbox").size(),
            "Expected mails to be removed successfully from inbox");

        assertEquals(mails, directory.getDirectoryPaths().get(path), "Expected mails to be added successfully in path");
    }

    @Test
    void testMoveEmailsFromInboxByRuleNoneOfTwo() {
        String path = rule3.getFolderPath();

        directory.addPath(path);

        directory.addEmailToInbox(mail1);
        directory.addEmailToInbox(mail2);

        Set<Mail> mails = Set.of();

        directory.moveEmailsFromInboxByRule(rule3);

        assertEquals(2, directory.getDirectoryPaths().get("/inbox").size(),
            "Expected mails to be removed successfully from inbox");

        assertEquals(mails, directory.getDirectoryPaths().get(path), "Expected mails to be added successfully in path");
    }
}
