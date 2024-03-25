package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class ChatGPTQuestionsService {
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Autowired
    AIQueryRepo aiQueryRepo;

    @Value("${ai.api_key}")
    private String chatGPTKey;


    public void queryContentForAllSubtopics() {
        String purpose = "content";
        List<CurriTopic> curriSubTopics = curriTopicRepository.findSubtopicsWithoutAI(purpose);
        for (CurriTopic curriTopic: curriSubTopics) {
            AIQuery aiQuery = new AIQuery();
            String question = """
                    {
                        "model": "gpt-3.5-turbo-0125",
                        "response_format": { "type": "text" },
                        "messages": [
                                        
                          {
                            "role": "user",
                            "content": "Create comprehensive notes for subject_replace students on subtopic_name_replace subtopic of topic_name_replace. Format your response in html with div being the parent tag. Would prefer you use definations, ordered lists and examples if possible . Fit the content to kenyan and target age as age_replace."
                          }
                        ]
                      }
                    """;
            question = question.replaceAll("subtopic_name_replace", curriTopic.getName());
            question = question.replaceAll("topic_name_replace", curriTopic.getParent().getName());
            question = question.replaceAll("age_replace", curriTopic.getParent().getCurriLevel().getAgeEstimate().toString());
            question = question.replaceAll("subject_replace", curriTopic.getParent().getSubject().getName().toString());
            aiQuery.setSubtopicId(curriTopic.getId());
            aiQuery.setQueryQuestion(question);
            aiQuery.setAIModel("gpt-3.5-turbo-0125");
            aiQuery.setQueryPurpose(purpose);

            aiQuery = aiQueryRepo.save(aiQuery);

            String response = gpt3_5Turbo0125Query(question);
            System.out.println(response);
            aiQuery.setAiResponse(response);

            aiQueryRepo.save(aiQuery);
            break;
        }
    }



    public void queryCurriQuestionsForAllSubtopics() {
        String purpose = "curri_question";
        List<CurriTopic> curriSubTopics = curriTopicRepository.findSubtopicsWithoutAI(purpose);
        for (CurriTopic curriTopic: curriSubTopics) {
            if(!(curriTopic.getParent().getName().toLowerCase().contains("fraction") || curriTopic.getName().toLowerCase().contains("fraction") ))
                continue;
            AIQuery aiQuery = new AIQuery();
            String question = """
                    {
                        "model": "gpt-3.5-turbo-0125",
                        "response_format": { "type": "json_object" },
                        "messages": [
                                        
                          {
                            "role": "user",
                            "content": "Create a list of questions for subject_replace students on subtopic_name_replace subtopic of topic_name_replace. Format your response as a json array of 50 objects with a question with a list of 4 choices, 3 of the choices being wrong and 1 right choice and an explanation for the right answer. Fit the content to kenyan and target age as age_replace. Specify which is the correct choice. Use only question, choices, correct_choice and explanation as the fields. For any mathematical formula, use html and inline css for display."
                          }
                        ]
                      }
                    """;
            question = question.replaceAll("subtopic_name_replace", curriTopic.getName());
            question = question.replaceAll("topic_name_replace", curriTopic.getParent().getName());
            question = question.replaceAll("age_replace", curriTopic.getParent().getCurriLevel().getAgeEstimate().toString());
            question = question.replaceAll("subject_replace", curriTopic.getParent().getSubject().getName());
            aiQuery.setSubtopicId(curriTopic.getId());
            aiQuery.setQueryQuestion(question);
            aiQuery.setAIModel("gpt-3.5-turbo-0125");
            aiQuery.setQueryPurpose(purpose);

            aiQuery = aiQueryRepo.save(aiQuery);

            String response = gpt3_5Turbo0125Query(question);
            System.out.println(response);
            aiQuery.setAiResponse(response);

            aiQueryRepo.save(aiQuery);
            break;
        }
    }


    private String gpt3_5Turbo0125Query(String question) {

        try {
            String url = "https://api.openai.com/v1/chat/completions";
            System.out.println(url);
            System.out.println(question);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to POST
            con.setRequestMethod("POST");

            // Set the Authorization header with Bearer token
            con.setRequestProperty("Authorization", "Bearer " + chatGPTKey);

            // Set other headers if needed
            con.setRequestProperty("Content-Type", "application/json");

            // Enable input and output streams
            con.setDoOutput(true);

            // Write the request body
            con.getOutputStream().write(question.getBytes("UTF-8"));

            // Get the response
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response body
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response body
//            System.out.println("Response Body: " + response.toString());

            boolean isJSONValid= isJSONValid(response.toString());
            if(isJSONValid) {
                JSONObject jsonObject = new JSONObject(response.toString());
                return ((JSONObject) jsonObject.getJSONArray("choices").get(0)).getJSONObject("message").getString("content");
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean isJSONValid(String json) {
        try {
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
