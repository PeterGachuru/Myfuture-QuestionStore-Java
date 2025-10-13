package ke.co.myfuture.Myfuture;

import okhttp3.*;
import org.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ArticleGenerator {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-5-mini"; // use gpt-4.1-mini for cheaper
    private static final OkHttpClient client = new OkHttpClient();

    private static String apiKey = "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";

    public static void main(String[] args) throws IOException {
        if (apiKey == null) {
            System.err.println("Please set OPENAI_API_KEY environment variable.");
            return;
        }

        // Step 1: Define areas
        String instruction = "Suggest 10 main future-based areas of focus for blog articles in 2025. The areas should be fit for Kenyan students. The areas can touch areas including moral aspects of their lives. Respond only as json string array. No extra comments.";
        String areasResponse = askChatGPT(instruction);
        JSONArray areas = new JSONArray(areasResponse);

        for (int i = 0; i < areas.length(); i++) {
            String area = areas.getString(i);
            System.out.println("Area: " + area);

            // Step 2: Get article titles for each area
            String titlePrompt = "Suggest 10 engaging article titles for the topic: " + area+". Respond only as json array. No extra comments. ";
            String titlesResponse = askChatGPT(titlePrompt);
            JSONArray titles = new JSONArray(titlesResponse);

            // Collect file paths for cross-linking
            Map<String, String> articleFiles = new LinkedHashMap<>();
            for (int j = 0; j < titles.length(); j++) {
                String title = titles.getString(j);
                String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
                String safeArea = area.replaceAll("[^a-zA-Z0-9]", "_");
                File dir = new File("articles/" + safeArea);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, safeTitle + ".html");
                articleFiles.put(title, file.getName());
            }

            // Step 3: Generate each article
            for (Map.Entry<String, String> entry : articleFiles.entrySet()) {
                String title = entry.getKey();
                String fileName = entry.getValue();

                System.out.println("   Title: " + title);

                // Ask ChatGPT for content
                String contentPrompt = "Write a detailed blog article in HTML about: " + title +
                        ". Include headings, paragraphs, and examples. Do not include <html>, <head>, or <body> tags. Use inline css themed blue and maroon. Style the html so as its responsive to different screens";
                String articleHtml = askChatGPT(contentPrompt);

// Generate image
//                String imagePath = generateImage(title + " illustration", area, title);
//                String imgHtml = "<img src=\"" + imagePath + "\" alt=\"" + title + "\" style=\"max-width:100%; height:auto; margin-bottom:20px;\">";
//
//// Prepend image HTML to content
//                articleHtml = imgHtml + articleHtml;

                // Create footer links
                StringBuilder footerLinks = new StringBuilder();
                footerLinks.append("<h3>More in this topic:</h3>");
                footerLinks.append("<div style=\"display:flex; flex-wrap:wrap; gap:10px;\">"); // responsive container

                for (Map.Entry<String, String> other : articleFiles.entrySet()) {
                    if (!other.getKey().equals(title)) {
                        footerLinks.append("<a href=\"")
                                .append(other.getValue())
                                .append("\" style=\"flex:1 1 150px; text-decoration:none; background:#f0f0f0; color:#333; padding:8px 12px; border-radius:4px; text-align:center;\">")
                                .append(other.getKey())
                                .append("</a>");
                    }
                }
                footerLinks.append("</div>");

                // Wrap in template
                String finalHtml = wrapInTemplate(title, articleHtml, footerLinks.toString());

                // Save file
                File dir = new File("student/articles/" + area.replaceAll("[^a-zA-Z0-9]", "_"));
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(finalHtml);
                }
                System.out.println("   Saved -> " + file.getAbsolutePath());
            }
        }
    }

    private static String generateImage(String prompt, String area, String title) throws IOException {
        String payload = """
        {
          "prompt": "image_prompt_replace",
          "n": 1,
          "size": "1024x1024"
        }
        """;
        payload = payload.replace("image_prompt_replace", prompt);

        URL url = new URL("https://api.openai.com/v1/images/generations");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.getOutputStream().write(payload.getBytes("UTF-8"));

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        org.json.JSONObject json = new org.json.JSONObject(response.toString());
        String imageUrl = json.getJSONArray("data").getJSONObject(0).getString("url");

        // Download image locally
        String safeArea = area.replaceAll("[^a-zA-Z0-9]", "_");
        String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
        File dir = new File("articles/" + safeArea + "/images");
        if (!dir.exists()) dir.mkdirs();
        String filePath = dir + "/" + safeTitle + ".png";

        try (InputStream is = new URL(imageUrl).openStream();
             FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
        }

        return "images/" + safeTitle + ".png"; // relative path for HTML
    }


    private static String askChatGPT(String prompt) throws IOException {
        System.out.println(prompt);
        String payload = """
                    {
                        "model": "ai_model_replace",
                        "messages": [
                                        
                          {
                            "role": "user",
                            "content": "question_replace"
                          }
                        ]
                      }
                    """;
        payload = payload.replaceAll("question_replace", prompt);
        payload = payload.replaceAll("ai_model_replace", MODEL );
        try {
            String url = "https://api.openai.com/v1/chat/completions";
            System.out.println(url);
            System.out.println(payload);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to POST
            con.setRequestMethod("POST");

            // Set the Authorization header with Bearer token
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Set other headers if needed
            con.setRequestProperty("Content-Type", "application/json");

            // Enable input and output streams
            con.setDoOutput(true);

            // Write the request body
            con.getOutputStream().write(payload.getBytes("UTF-8"));

            // Get the response
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode >= 400) {
                System.out.println("error result");
                displayHttpError(con, responseCode);
                return null;
            }

            // Read the response body
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response");
//            System.out.println(response);
//
//            return response.toString();

            // Print the response body
//            System.out.println("Response Body: " + response.toString());

            boolean isJSONValid= isJSONValid(response.toString());
            if(isJSONValid) {
                System.out.println("Is valid json");
                org.springframework.boot.configurationprocessor.json.JSONObject jsonObject = new org.springframework.boot.configurationprocessor.json.JSONObject(response.toString());
                String responseString = ((JSONObject) jsonObject.getJSONArray("choices").get(0)).getJSONObject("message").getString("content");
                System.out.println(responseString);

//                isJSONValid= isJSONValid(response.toString());
                return responseString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private static void displayHttpError(HttpURLConnection connection, int responseCode) throws IOException {
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
                new org.springframework.boot.configurationprocessor.json.JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }
    private static String wrapInTemplate(String title, String content, String footerLinks) {
        // Insert footerLinks before the closing </div> of the main content
        int closingDivIndex = content.lastIndexOf("</div>");
        if (closingDivIndex != -1) {
            content = content.substring(0, closingDivIndex)
                    + "<div id=\"footer-links\" style=\"margin-top:40px;\">"
                    + footerLinks
                    + "</div>\n"
                    + content.substring(closingDivIndex);
        } else {
            // fallback: append at the end if </div> not found
            content += "<div id=\"footer-links\" style=\"margin-top:40px;\">" + footerLinks + "</div>\n";
        }

        return "<!DOCTYPE html>\n<html>\n<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + title + "</title>\n" +
                "    <link rel=\"stylesheet\" href=\"styles.css\">\n" +
                "</head>\n<body>\n" +
                content + "\n" +
                "    <footer style=\"text-align:center; padding:20px 0; background:#f2f2f2; margin-top:40px;\">\n" +
                "        <p>© 2025 My Blog. All rights reserved.</p>\n" +
                "    </footer>\n" +
                "</body>\n</html>";
    }
}

