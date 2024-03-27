package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import com.google.gson.Gson;
import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.Cgroup;
import ke.co.myfuture.Myfuture.QuestionStore.Cgroup.CgroupService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoiceRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import lombok.Data;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGPTQuestionsService {
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Autowired
    AIQueryRepo aiQueryRepo;

    @Autowired
    CgroupService cgroupService;

    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Autowired
    CurriNormalChoiceRepository curriNormalChoiceRepository;

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
//            if(!(curriTopic.getParent().getName().toLowerCase().contains("fraction") || curriTopic.getName().toLowerCase().contains("fraction") ))
//                continue;
            AIQuery aiQuery = new AIQuery();
            String question = """
                Create a list of questions for subject_replace students on subtopic_name_replace subtopic of topic_name_replace. Format your response as a json array of 50 objects with a question with a list of 4 choices, 3 of the choices being wrong and 1 right choice and an explanation for the right answer. Fit the content to kenyan and target age as age_replace. Specify which is the correct choice. Use only question, choices, correct_choice and explanation as the fields. Don't assign order to the choices.
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

//            while (!isJSONValid(response)) {
//                response += gpt3_5Turbo0125Query("keep going");
//            }

//            System.out.println(response);
            aiQuery.setAiResponse(response);

            aiQuery = aiQueryRepo.save(aiQuery);
            if (isJSONValid(response)) {
                aiQuery.setMigrated(true);
                saveQuestionArray(curriTopic, response);
            }
//            break;
        }
    }

    private void saveQuestionArray(CurriTopic curriTopic, String response) {
        Gson gson = new Gson();

        Questions questions = gson.fromJson(response, Questions.class);

        // Parse the JSON array string into a list of MyObject
        List<Question> myObjects = questions.questions;

        for (Question question: myObjects) {
            CurriQuestion curriQuestion = new CurriQuestion();
            curriQuestion.setString(question.getQuestion());
            curriQuestion.setSubtopic(curriTopic);
            curriQuestion.setBookModel(BookInitialModels.chatGpt3_5);
            curriQuestion.setHasImage(false);

            List<CurriNormalChoice> choices = new ArrayList<>();
            boolean foundRight = false;
            for (String choice :
                    question.getChoices()) {
                CurriNormalChoice curriNormalChoice = new CurriNormalChoice();
                curriNormalChoice.setValue(choice);
                if (choice.equalsIgnoreCase(question.correct_choice) ) {
                    curriNormalChoice.setType("right");
                    if (foundRight) {
                        foundRight = false;
                        break;
                    }
                    foundRight = true;
                }else {
                    curriNormalChoice.setType("wrong");
                }
                choices.add(curriNormalChoice);
            }
            if (!foundRight)
                continue;

            Cgroup cgroup = new Cgroup();
            cgroup.setType("Many");
            cgroup.setDescription("Question group");
            cgroup.setName("Question group");

            cgroup = cgroupService.newCgroup(cgroup);

            curriQuestion.setCgroup(cgroup.id);
            CurriQuestion savedCurriQuestion = curriQuestionRepository.save(curriQuestion);
//
            for (CurriNormalChoice choice: choices) {
                choice.setQuestion(savedCurriQuestion.getId());
            }
            curriNormalChoiceRepository.saveAll(choices);
        }

    }

    public static boolean isValidJson(String str) {
        try {
            new JSONObject(str);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private String gpt3_5Turbo0125Query(String question) {
        return normalChatGptCall(question, "gpt-3.5-turbo-0125");
    }

    private String gpt40125(String question) {
        return normalChatGptCall(question, "gpt-4-0125-preview");
    }

    private String normalChatGptCall(String question, String model) {
        String payload = """
                    {
                        "model": "ai_model_replace",
                        "response_format": { "type": "json_object" },
                        "messages": [
                                        
                          {
                            "role": "user",
                            "content": "question_replace"
                          }
                        ]
                      }
                    """;
        payload = payload.replaceAll("question_replace", question.trim());
        payload = payload.replaceAll("ai_model_replace", model.trim());
        try {
            String url = "https://api.openai.com/v1/chat/completions";
            System.out.println(url);
            System.out.println(payload);

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
            con.getOutputStream().write(payload.getBytes("UTF-8"));

            // Get the response
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode >= 400) {
                System.out.println("error result");
                displayHttpError(con, responseCode);
                return "";
            }

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
                String responseString = ((JSONObject) jsonObject.getJSONArray("choices").get(0)).getJSONObject("message").getString("content");
                System.out.println(responseString);
                return responseString;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    @Data
    class Question{
        private String question;
        private String correct_choice;
        private String explanation;
        private String[] choices;
    }

    class Questions{
        List<Question> questions;
    }
}
