package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Service
public class ExceptionLogger {

//    @Value("${spring.application.files.logs.exceptions}")
//    private String LOGS_DIRECTORY;

//    private static final String LOGS_DIRECTORY = "/home/jamesmn/bin/logs/";
    private static final String FILENAME_FORMAT = "dd-MM-yyyy";
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern(FILENAME_FORMAT);

    public void logError(String e) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String ipAddress = getClientIpAddress();
        String currentDateTime = generateCurrentDateAndTime();
        String exception = "";
        try {
            String filename = generateCurrentYearFilename();
            exception = currentDateTime + "    " + userEmail + "    " + ipAddress + "    " + " Exception: " + e;
            writeLogToFile(filename, exception);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String generateCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return now.format(formatter);
    }

    private String getClientIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String generateCurrentYearFilename() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = now.with(TemporalAdjusters.lastDayOfYear());
        return startOfYear.format(FILENAME_FORMATTER) + " to " + endOfYear.format(FILENAME_FORMATTER);
    }

    private void writeLogToFile(String filename, String log) throws IOException {
//        String filePath = LOGS_DIRECTORY + filename + ".txt";
//        File file = new File(filePath);
//        if (!file.exists()) {
//            file.getParentFile().mkdirs();
//            file.createNewFile();
//        }
//
//        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
//            writer.println(log);
//        }
    }
}
