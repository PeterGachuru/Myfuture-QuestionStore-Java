package ke.co.myfuture.Myfuture;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class UrlIpAnalyzer {

    private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB

    public static void main(String[] args) {
        String inputFile = "C:\\Users\\USER\\personal\\data\\url_ip_records\\url_ip_records.csv";
        String outputFile = args.length >= 2
                ? args[1]
                : "url_ip_summary.csv";

        System.out.println("==============================================");
        System.out.println("URL/IP CSV Analyzer");
        System.out.println("==============================================");
        System.out.println("Input  : " + inputFile);
        System.out.println("Output : " + outputFile);
        System.out.println("Started: " + new Date());
        System.out.println();

        /*
         * Map:
         *
         * URL -> IP -> Count
         *
         * This allows us to count how many times each
         * IP consumed each URL.
         */
        Map<String, Map<String, Long>> data = new HashMap<>();

        long lines = 0;
        long validRecords = 0;
        long invalidRecords = 0;

        long lastReport = System.currentTimeMillis();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(inputFile),
                                StandardCharsets.UTF_8
                        ),
                        BUFFER_SIZE
                )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                lines++;

                if (line.isEmpty()) {
                    continue;
                }

                /*
                 * Expected format:
                 *
                 * IP,URL
                 */
                int comma = line.indexOf(',');

                if (comma <= 0 || comma >= line.length() - 1) {
                    invalidRecords++;
                    continue;
                }

                String ip = line.substring(0, comma).trim();
                String url = line.substring(comma + 1).trim();

                if (ip.isEmpty() || url.isEmpty()) {
                    invalidRecords++;
                    continue;
                }

                /*
                 * Get the map of IPs for this URL.
                 */
                Map<String, Long> ipCounts =
                        data.computeIfAbsent(url, k -> new HashMap<>());

                /*
                 * Increment count.
                 */
                ipCounts.merge(ip, 1L, Long::sum);

                validRecords++;

                /*
                 * Progress every 60 seconds.
                 */
                long now = System.currentTimeMillis();

                if (now - lastReport >= 60_00) {

                    System.out.println(
                            "Progress: "
                                    + lines
                                    + " lines processed | "
                                    + validRecords
                                    + " valid records | "
                                    + data.size()
                                    + " URLs"
                    );

                    lastReport = now;
                }
            }

        } catch (IOException e) {

            System.err.println("Error reading input file:");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println();
        System.out.println("Input processing complete.");
        System.out.println("Total lines    : " + lines);
        System.out.println("Valid records  : " + validRecords);
        System.out.println("Invalid records: " + invalidRecords);
        System.out.println("Unique URLs    : " + data.size());
        System.out.println();

        /*
         * Sort URLs.
         */
        List<String> urls = new ArrayList<>(data.keySet());

        Collections.sort(urls);

        /*
         * Write output.
         */
        try (
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(outputFile),
                                StandardCharsets.UTF_8
                        ),
                        BUFFER_SIZE
                )
        ) {

            /*
             * CSV header.
             */
            writer.write("IP Address,Number of Times,URL");
            writer.newLine();

            long outputRows = 0;

            for (String url : urls) {

                Map<String, Long> ipCounts = data.get(url);

                /*
                 * Sort IP addresses.
                 */
                List<String> ips = new ArrayList<>(ipCounts.keySet());

                Collections.sort(ips);

                /*
                 * URL first, then IP.
                 */
                for (String ip : ips) {

                    writer.write(ip);
                    writer.write(",");
                    writer.write(Long.toString(ipCounts.get(ip)));
                    writer.write(",");
                    writer.write(escapeCsv(url));
                    writer.newLine();

                    outputRows++;
                }
            }

            System.out.println("Output rows: " + outputRows);

        } catch (IOException e) {

            System.err.println("Error writing output file:");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println();
        System.out.println("==============================================");
        System.out.println("COMPLETE");
        System.out.println("==============================================");
        System.out.println("Output: " + outputFile);
        System.out.println("Finished: " + new Date());
    }

    /*
     * Escape values for CSV.
     *
     * URLs normally won't contain commas, but this makes
     * the output valid CSV if one does.
     */
    private static String escapeCsv(String value) {

        if (value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r")) {

            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
