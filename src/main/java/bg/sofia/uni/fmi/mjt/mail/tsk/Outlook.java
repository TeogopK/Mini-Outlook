package bg.sofia.uni.fmi.mjt.mail.tsk;

import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.tsk.metadata.MailMetadata;
import bg.sofia.uni.fmi.mjt.mail.tsk.rule.Rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Outlook implements MailClient {
    private static final int UPPER_BOUND_PRIORITY = 10;
    private static final int LOWER_BOUND_PRIORITY = 1;

    private final Map<String, SystemAccount> systemAccounts;

    public Outlook() {
        this.systemAccounts = new HashMap<>();
    }

    private boolean doesAccountExistsByEmail(String email) {
        for (var systemAccount : systemAccounts.values()) {
            if (systemAccount.account().emailAddress().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, SystemAccount> getSystemAccounts() {
        return systemAccounts;
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can not be null, empty or blank");
        }
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException("Email can not be null, empty or blank");
        }

        if (systemAccounts.containsKey(accountName)) {
            throw new AccountAlreadyExistsException("Account with the same name already exists");
        }
        if (doesAccountExistsByEmail(email)) {
            throw new AccountAlreadyExistsException("Account with the same email address already exists");
        }

        Account newAccount = new Account(email, accountName);
        systemAccounts.put(accountName, SystemAccount.of(newAccount));

        return newAccount;
    }

    @Override
    public void createFolder(String accountName, String path) {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can not be null, empty or blank");
        }
        if (path == null || path.isEmpty() || path.isBlank()) {
            throw new IllegalArgumentException("Path can not be null, empty or blank");
        }

        if (!systemAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Can not create a folder if the account does not exist");
        }

        systemAccounts.get(accountName).directory().addPath(path);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can not be null, empty or blank");
        }
        if (folderPath == null || folderPath.isEmpty() || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path can not be null, empty or blank");
        }
        if (ruleDefinition == null || ruleDefinition.isEmpty() || ruleDefinition.isBlank()) {
            throw new IllegalArgumentException("Rule definition can not be null, empty or blank");
        }
        if (priority < LOWER_BOUND_PRIORITY || priority > UPPER_BOUND_PRIORITY) {
            throw new IllegalArgumentException("Priority out of bounds");
        }

        if (!systemAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Can not add a rule to an account if the account does not exist");
        }

        if (!systemAccounts.get(accountName).directory().isPathExisting(folderPath)) {
            throw new FolderNotFoundException("Can not create a rule with a folder, if the folder does not exist");
        }

        Rule rule = new Rule(folderPath, ruleDefinition, priority);

        if (isConflictRule(accountName, rule)) {
            //also true for equal rules
            throw new RuleAlreadyDefinedException("Conflict rule, because of equal priority and definition");
        }

        systemAccounts.get(accountName).rules().add(rule);

        systemAccounts.get(accountName).directory().moveEmailsFromInboxByRule(rule);
    }

    private boolean isConflictRule(String accountName, Rule ruleToAdd) {
        var rules = systemAccounts.get(accountName).rules();
        for (Rule rule : rules) {
            if (rule.getPriority() == ruleToAdd.getPriority() &&
                rule.getConditions().equals(ruleToAdd.getConditions())) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can not be null, empty or blank");
        }
        if (mailMetadata == null || mailMetadata.isEmpty() || mailMetadata.isBlank()) {
            throw new IllegalArgumentException("Mail metadata can not be null, empty or blank");
        }
        if (mailContent == null || mailContent.isEmpty() || mailContent.isBlank()) {
            throw new IllegalArgumentException("Mail content can not be null, empty or blank");
        }
        if (!systemAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Can not get emails from an account, if the account does not exist");
        }

        Mail mail = getMailFrom(mailMetadata, mailContent);

        Rule rule = findTheRule(accountName, mail);
        if (rule == null) {
            systemAccounts.get(accountName).directory().addEmailToInbox(mail);
        } else {
            systemAccounts.get(accountName).directory().addEmail(rule.getFolderPath(), mail);
        }

    }

    private Rule findTheRule(String accountName, Mail mail) {
        var rules = systemAccounts.get(accountName).rules();

        for (var rule : rules) {
            if (rule.doesRuleFitMail(mail)) {
                return rule;
            }
        }
        return null;
    }

    private Mail getMailFrom(String mailMetadata, String mailContent) {
        MailMetadata metadata = new MailMetadata(mailMetadata);

        return new Mail(getAccountFromEmail(metadata.getSender()), metadata.getRecipients(), metadata.getSubject(),
            mailContent, metadata.getReceived());
    }

    private Account getAccountFromEmail(String email) {
        for (var systemAccount : systemAccounts.values()) {
            if (systemAccount.account().emailAddress().equals(email)) {
                return systemAccount.account();
            }
        }
        return null;
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        if (account == null || account.isEmpty() || account.isBlank()) {
            throw new IllegalArgumentException("Account can not be null, empty or blank");
        }
        if (folderPath == null || folderPath.isEmpty() || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path can not be null, empty or blank");
        }
        if (!systemAccounts.containsKey(account)) {
            throw new AccountNotFoundException(
                "Can not get emails from a folder of an account, if the account does not exist");
        }

        return systemAccounts.get(account).directory().getMails(folderPath);
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name can not be null, empty or blank");
        }
        if (mailMetadata == null || mailMetadata.isEmpty() || mailMetadata.isBlank()) {
            throw new IllegalArgumentException("Mail metadata can not be null, empty or blank");
        }
        if (mailContent == null || mailContent.isEmpty() || mailContent.isBlank()) {
            throw new IllegalArgumentException("Mail content can not be null, empty or blank");
        }
        if (!systemAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Can not send emails from an account, if the account does not exist");
        }

        Mail mail = sentMailFrom(accountName, mailMetadata, mailContent);
        systemAccounts.get(accountName).directory().addEmailToSent(mail);

        for (String name : mail.recipients()) {
            Account account = getAccountFromEmail(name);
            if (account != null) {
                receiveMail(account.name(), mailMetadata, mailContent);
            }
        }
    }

    private Mail sentMailFrom(String sender, String mailMetadata, String mailContent) {
        MailMetadata metadata = new MailMetadata(mailMetadata);

        return new Mail(systemAccounts.get(sender).account(), metadata.getRecipients(), metadata.getSubject(),
            mailContent, metadata.getReceived());
    }
}
