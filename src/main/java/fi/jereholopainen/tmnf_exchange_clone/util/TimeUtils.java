package fi.jereholopainen.tmnf_exchange_clone.util;

public class TimeUtils {
    public static String convertMillisToMinutesAndSeconds(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}