package bg.sofia.uni.fmi.mjt.mail.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

public class FromCondition extends RuleConditionAbstract {

    public FromCondition() {
        super("from:");
    }

    @Override
    public boolean doesConditionFitMail(Mail mail) {
        if (isEmptyCondition()) {
            return true;
        }

        for (var value : values) {
            if (!mail.sender().emailAddress().equals(value)) {
                return false;
            }
        }
        return true;
    }
}
