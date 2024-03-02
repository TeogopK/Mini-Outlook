package bg.sofia.uni.fmi.mjt.mail.tsk.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;

public class SubjectOrBodyIncludesCondition extends AbstractRuleCondition {

    public SubjectOrBodyIncludesCondition() {
        super("subject-or-body-includes:");
    }

    @Override
    public boolean doesConditionFitMail(Mail mail) {
        if (isEmptyCondition()) {
            return true;
        }

        String text = mail.subject() + mail.body();

        for (var value : values) {
            if (!text.contains(value)) {
                return false;
            }
        }
        return true;
    }
}
