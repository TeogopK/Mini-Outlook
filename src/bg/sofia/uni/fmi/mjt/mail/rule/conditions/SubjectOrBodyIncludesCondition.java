package bg.sofia.uni.fmi.mjt.mail.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

public class SubjectOrBodyIncludesCondition extends RuleConditionAbstract {

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
