package bg.sofia.uni.fmi.mjt.mail.tsk.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;

public class FromCondition extends AbstractRuleCondition {

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
