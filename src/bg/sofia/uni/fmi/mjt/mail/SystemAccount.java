package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.comparators.RuleByPriorityComparator;
import bg.sofia.uni.fmi.mjt.mail.directory.Directory;
import bg.sofia.uni.fmi.mjt.mail.rule.Rule;

import java.util.Set;
import java.util.TreeSet;

public record SystemAccount(Account account, Directory directory, Set<Rule> rules) {
    public static SystemAccount of(Account account) {
        return (new SystemAccount(account, new Directory(), new TreeSet<>(new RuleByPriorityComparator())));
    }
}
