package bg.sofia.uni.fmi.mjt.mail.tsk.comparators;

import bg.sofia.uni.fmi.mjt.mail.tsk.rule.Rule;

import java.util.Comparator;

public class RuleByPriorityComparator implements Comparator<Rule> {
    @Override
    public int compare(Rule o1, Rule o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}