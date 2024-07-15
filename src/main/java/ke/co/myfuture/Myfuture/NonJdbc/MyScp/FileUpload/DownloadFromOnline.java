package ke.co.myfuture.Myfuture.NonJdbc.MyScp.FileUpload;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static ke.co.myfuture.Myfuture.Utils.Response.OnlineUtils.getArrayFromOnline;

@Service
public class DownloadFromOnline {

    String[] sourceFolders = new String[] {
            "C:\\Users\\USER\\personal\\data",
    };
    String saveRootFolder = "C:\\Users\\USER\\personal\\testbackup";
    String baseUrlFolders = "http://localhost:2024/files/listfolders";
    String baseUrlReadFiles = "http://localhost:2024/files/listfiles";
    String baseUrlDownloadFiles = "http://localhost:2024/files/download/base2";

//    @Bean
    public void downloadRecussively() throws JSONException, IOException {
        System.out.println("downloadRecussively");
        for (String folder: sourceFolders) {
            recurseFolder(folder);
        }
    }

    public void recurseFolder(String folderPath) throws JSONException, IOException {
        String url = makeUrl(baseUrlFolders, folderPath);
        JSONArray folders = getArrayFromOnline(url);
        int s = folders.length();
        for (int i = 0; i < s; i++) {
            String folder = folders.getString(i);
            downloadFilesFromFolder(folder);
        }
    }

    public String makeUrl(String baseUrl, String filePath) {
        String encodedSearchString = UriComponentsBuilder.fromUriString("")
                .queryParam("filePath", encodeStringToBase64(filePath))
                .build().encode().toUriString();

        System.out.println(encodeStringToBase64(filePath));

        return baseUrl+encodedSearchString;
    }

    public void downloadFilesFromFolder(String folder) throws JSONException, IOException {
        String url = makeUrl(baseUrlReadFiles, folder);

        JSONArray files = getArrayFromOnline(url);
        if (files == null)
            return;
        int s = files.length();
        for (int i = 0; i < s; i++) {

            String file = files.getString(i);

            String downloadUrl = makeUrl(baseUrlDownloadFiles, file);

            downloadFile(downloadUrl, saveRootFolder);
        }
    }

    public String encodeStringToBase64(String originalString) {
        return Base64.getEncoder().encodeToString(originalString.getBytes());
    }

    public static void downloadFile(String fileURL, String savePath) throws IOException {
        // Create a URL object from the file URL
        URL url = new URL(fileURL);
        // Open a connection to the URL
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        // Check the HTTP response code
        int responseCode = httpConn.getResponseCode();

        // If the response code is HTTP OK (200)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Open input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            // Create a buffered input stream for efficiency
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            // Open output stream to save the file
            FileOutputStream fos = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            // Read bytes from the input stream and write them to the output stream
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            // Close streams
            fos.close();
            bis.close();
            inputStream.close();

            System.out.println("File downloaded from " + fileURL + " to " + savePath);
        } else {
            System.out.println("No file to download. Server replied with HTTP code: " + responseCode);
        }
        // Disconnect the HTTP connection
        httpConn.disconnect();
    }
}
