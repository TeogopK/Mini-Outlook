package bg.sofia.uni.fmi.mjt.mail.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.parsers.CommaSeparatedValuesToSetParser;
import bg.sofia.uni.fmi.mjt.mail.parsers.KeyFromTextToValueLineParser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class RuleConditionAbstract {
    protected String key;
    protected Set<String> values;

    public RuleConditionAbstract(String key) {
        this.key = key;
        this.values = new HashSet<>();
    }

    public void setValues(String text) {
        String line = KeyFromTextToValueLineParser.getValueLine(key, text);
        this.values = CommaSeparatedValuesToSetParser.getSet(line);
    }

    public boolean isTextCorrect(String text) {
        return text.indexOf(key) == text.lastIndexOf(key);
    }

    public boolean isEmptyCondition() {
        return values.size() == 0;
    }

    public abstract boolean doesConditionFitMail(Mail mail);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleConditionAbstract that = (RuleConditionAbstract) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
