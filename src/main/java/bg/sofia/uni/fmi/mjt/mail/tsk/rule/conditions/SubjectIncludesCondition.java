package bg.sofia.uni.fmi.mjt.mail.tsk.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;

public class SubjectIncludesCondition extends RuleConditionAbstract {

    public SubjectIncludesCondition() {
        super("subject-includes:");
    }

    @Override
    public boolean doesConditionFitMail(Mail mail) {
        if (isEmptyCondition()) {
            return true;
        }

        for (var value : values) {
            if (!mail.subject().contains(value)) {
                return false;
            }
        }
        return true;
    }
}
