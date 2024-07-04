package camel.BoostMarketer.common.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static LocalDate convertStringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d.");
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDateTime convertStringToRelativeDateTime(String relativeTime) {
        LocalDateTime now = LocalDateTime.now();

        if ("방금 전".equals(relativeTime)) {
            return now;
        } else if (relativeTime.contains("시간 전")) {
            int hours = Integer.parseInt(relativeTime.split("시간 전")[0]);
            return now.minusHours(hours);
        } else if (relativeTime.contains("분 전")) {
            int minutes = Integer.parseInt(relativeTime.split("분 전")[0]);
            return now.minusMinutes(minutes);
        }
        return now; // 기본적으로 현재 시간 반환
    }

    public static Date convertToLocalDate(String input) {
        if (input.contains("전")) {
            // 상대적인 시간 처리
            LocalDateTime localDateTime = convertStringToRelativeDateTime(input);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            // 정확한 날짜 처리
            LocalDate localDate = convertStringToDate(input);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String formatBlogDate(String originalDate) {
        int lastDotIndex = originalDate.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return originalDate.substring(0, lastDotIndex).trim();
        }
        return originalDate.trim();
    }

}
