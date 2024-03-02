package bg.sofia.uni.fmi.mjt.mail.tsk.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.tsk.Mail;

public class RecipientsIncludesCondition extends AbstractRuleCondition {

    public RecipientsIncludesCondition() {
        super("recipients-includes:");
    }

    @Override
    public boolean doesConditionFitMail(Mail mail) {
        if (isEmptyCondition()) {
            return true;
        }

        for (var value : values) {
            if (mail.recipients().contains(value)) {
                return true;
            }
        }
        return false;
    }
}
