package bg.sofia.uni.fmi.mjt.mail.parsers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommaSeparatedValuesToSetParser {
    public static Set<String> getSet(String valueLine) {
        if (valueLine.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> setWithSpaces = Set.of(valueLine.trim().split(","));

        Set<String> setToReturn = new HashSet<>();

        for (String s : setWithSpaces) {
            setToReturn.add(s.trim());
        }
        return setToReturn;
    }
}
