package bg.sofia.uni.fmi.mjt.mail.tsk.directory;

import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.tsk.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.tsk.rule.Rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Directory {
    private final static String ROOT = "/";
    private final static String PATH_SEPARATOR = "/";
    private final static String INBOX = "inbox";
    private final static String SENT = "sent";
    private final static String REGEX_CONSECUTIVE_PATH_SEPARATOR = ".*" + Pattern.quote(PATH_SEPARATOR) + "{2,}.*";
    private final Map<String, Set<Mail>> directoryPaths;

    public Directory() {
        this.directoryPaths = new TreeMap<>();
        setRoot();
    }

    private void setRoot() {
        directoryPaths.put(ROOT, new HashSet<>());

        directoryPaths.put(ROOT + INBOX, new HashSet<>());
        directoryPaths.put(ROOT + SENT, new HashSet<>());
    }

    private boolean isNewPathCorrect(String path) {

        // Path should start with root + inbox and should not end without specified folder
        if (!path.startsWith(ROOT + INBOX) || path.endsWith(PATH_SEPARATOR)) {
            return false;
        }

        // Paths such as /inbox//important are not allowed
        if (path.matches(REGEX_CONSECUTIVE_PATH_SEPARATOR)) {
            return false;
        }

        String father = getFather(path);

        //Path should contain all folders
        return directoryPaths.containsKey(father);
    }

    private String getFather(String path) {
        int index = path.lastIndexOf(PATH_SEPARATOR);

        return path.substring(0, index);
    }

    public Map<String, Set<Mail>> getDirectoryPaths() {
        return directoryPaths;
    }

    public Set<Mail> getMails(String path) {
        if (!isPathExisting(path)) {
            throw new FolderNotFoundException("Can not get emails from a folder, if the folder does not exist");
        }
        return new HashSet<>(directoryPaths.get(path));
    }

    public Set<Mail> getMailsFromInbox() {
        return new HashSet<>(directoryPaths.get(ROOT + INBOX));
    }

    public boolean isPathExisting(String path) {
        return directoryPaths.containsKey(path);
    }

    public void addPath(String path) {
        if (!isNewPathCorrect(path)) {
            throw new InvalidPathException("Invalid path in function call");
        }
        if (isPathExisting(path)) {
            throw new FolderAlreadyExistsException("Path already exists, can not create a new one");
        }
        directoryPaths.put(path, new HashSet<>());
    }

    public void addEmail(String path, Mail mail) {
        if (!isPathExisting(path)) {
            throw new FolderNotFoundException("Can not add email to a folder, if the folder does not exist");
        }
        directoryPaths.get(path).add(mail);
    }

    public void addEmailToInbox(Mail mail) {
        directoryPaths.get(ROOT + INBOX).add(mail);
    }

    public void addEmailToSent(Mail mail) {
        directoryPaths.get(ROOT + SENT).add(mail);
    }

    public void moveEmailsFromInboxByRule(Rule rule) {
        String toPath = rule.getFolderPath();
        if (!isPathExisting(toPath)) {
            throw new FolderNotFoundException("Can not move emails to a folder, if the folder does not exist");
        }

        var it = directoryPaths.get(ROOT + INBOX).iterator();
        while (it.hasNext()) {

            Mail mail = it.next();
            if (rule.doesRuleFitMail(mail)) {
                it.remove();
                directoryPaths.get(toPath).add(mail);
            }
        }
    }
}
