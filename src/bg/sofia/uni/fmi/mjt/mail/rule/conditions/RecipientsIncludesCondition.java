package bg.sofia.uni.fmi.mjt.mail.rule.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

public class RecipientsIncludesCondition extends RuleConditionAbstract {

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
