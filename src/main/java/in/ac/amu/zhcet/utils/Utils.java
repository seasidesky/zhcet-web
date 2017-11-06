package in.ac.amu.zhcet.utils;

import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public class Utils {

    // Prevent instantiation of Util class
    private Utils() {}

    public static String getDefaultSessionCode(){
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();

        String session = "A";
        if (month >= 1 && month < 6)
            session ="W";

        String year = String.valueOf(localDate.getYear() % 100);
        session += year;

        return session;
    }

    public static String getDefaultSessionName() {
        return getSessionName(getDefaultSessionCode());
    }

    public static String getSessionName(String sessionCode) {
        if (sessionCode.charAt(0) == 'A')
            return "Autumn '" + sessionCode.substring(1);
        else
            return "Winter '" + sessionCode.substring(1);
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
