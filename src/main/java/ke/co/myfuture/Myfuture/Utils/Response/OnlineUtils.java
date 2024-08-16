package ke.co.myfuture.Myfuture.Utils.Response;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OnlineUtils {

    public static JSONArray getArrayFromOnline(String url) {
        try {
//            System.out.println(url);

            String response = getTextFromURL(url);

            boolean isJSONValid= isJSONValid(response.toString());
            if(isJSONValid) {
                JSONObject jsonObject = new JSONObject(response.toString());
                return jsonObject.getJSONArray("entity");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTextFromURL(String fileURL) throws IOException {
        // Create a URL object from the file URL
        URL url = new URL(fileURL);
        // Open a connection to the URL
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        // Check the HTTP response code
        int responseCode = httpConn.getResponseCode();

        // If the response code is HTTP OK (200)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Open input stream from the HTTP connection
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            // Read lines from the input stream and append them to the StringBuilder
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }

            // Close the input stream
            in.close();

            return content.toString();
        } else {
            throw new IOException("No content to retrieve. Server replied with HTTP code: " + responseCode);
        }
    }

    private void displayHttpError(HttpURLConnection connection, int responseCode) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuilder errorMessage = new StringBuilder();

        while ((inputLine = errorReader.readLine()) != null) {
            errorMessage.append(inputLine);
        }
        errorReader.close();

        System.out.println("Error response code: " + responseCode);
        System.out.println("Error message: " + errorMessage);
    }

    public static boolean isJSONValid(String json) {
        try {
            if (json == null)
                return false;
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }
}
