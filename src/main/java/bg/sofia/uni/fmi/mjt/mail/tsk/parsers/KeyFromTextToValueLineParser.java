package bg.sofia.uni.fmi.mjt.mail.tsk.parsers;

public class KeyFromTextToValueLineParser {
    private final static int INDEX_NOT_FOUND = -1;

    public static String getValueLine(String key, String text) {
        int indexStart = text.indexOf(key);
        if (indexStart == INDEX_NOT_FOUND) {
            return "";
        }

        indexStart += key.length();

        String stripped = text.substring(indexStart).stripLeading();

        int indexEnd = stripped.indexOf(System.lineSeparator());

        if (indexEnd == INDEX_NOT_FOUND) {
            return stripped;
        }
        return stripped.substring(0, indexEnd);
    }
}
