package bg.sofia.uni.fmi.mjt.mail.rule;

import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.rule.conditions.FromCondition;
import bg.sofia.uni.fmi.mjt.mail.rule.conditions.RecipientsIncludesCondition;
import bg.sofia.uni.fmi.mjt.mail.rule.conditions.RuleConditionAbstract;
import bg.sofia.uni.fmi.mjt.mail.rule.conditions.SubjectIncludesCondition;
import bg.sofia.uni.fmi.mjt.mail.rule.conditions.SubjectOrBodyIncludesCondition;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Rule {
    private final Set<RuleConditionAbstract> conditions;

    private final int priority;
    private final String folderPath;

    public Rule(String folderPath, String definition, int priority) {
        this.folderPath = folderPath;
        this.priority = priority;
        this.conditions = new HashSet<>();

        initConditions();
        isDefinitionCorrect(definition);
        setConditions(definition);
    }

    private void initConditions() {
        conditions.add(new FromCondition());
        conditions.add(new RecipientsIncludesCondition());
        conditions.add(new SubjectIncludesCondition());
        conditions.add(new SubjectOrBodyIncludesCondition());
    }

    private void setConditions(String definition) {
        for (var condition : conditions) {
            condition.setValues(definition);
        }
    }

    private void isDefinitionCorrect(String definition) {
        for (var condition : conditions) {
            if (!condition.isTextCorrect(definition)) {
                throw new RuleAlreadyDefinedException("Rule condition exists more the one in the rule definition");
            }
        }
    }

    public boolean doesRuleFitMail(Mail mail) {
        for (var condition : conditions) {
            if (!condition.doesConditionFitMail(mail)) {
                return false;
            }
        }
        return true;
    }

    public int getPriority() {
        return priority;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public Set<RuleConditionAbstract> getConditions() {
        return conditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return priority == rule.priority && conditions.equals(rule.conditions) && folderPath.equals(rule.folderPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions, priority, folderPath);
    }
}
