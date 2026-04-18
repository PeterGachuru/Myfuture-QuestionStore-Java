package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion.AIQuery;
import ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion.AIQueryRepo;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotes;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNotes.CurriNotesRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel.SubjectLevelRepository;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class ChatGPTNotesService {
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Autowired
    AIQueryRepo aiQueryRepo;

    @Autowired
    CurriNotesRepository curriNotesRepository;
    @Autowired
    SubjectLevelRepository subjectLevelRepository;

    @Value("${ai.api_key}")
    private String chatGPTKey;

    private final String CURRENT_MODEL = "gpt-5-mini";


    //    @Bean
//    public void testNotesGeneration() {
//        generateNotesForSubject(CURRENT_MODEL, 14, 4);
//    }


    @Async
    public UniversalResponse generateNotesForSubject(String model, long levelId, long subjectId) {
        System.out.println("Automatically generating notes for "+model+" level: "+levelId+" subject: "+subjectId);
        List<CurriTopic> topicList  =  curriTopicRepository.findBySubjectAndClass(subjectId,
                levelId);
        for (CurriTopic topic: topicList) {
            List<CurriTopic> subtopicList  =  curriTopicRepository.findByParent(topic.getId());
            for (CurriTopic subtopic: subtopicList) {
                generateNotesForSubtopic(model, subtopic);
            }
        }

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Generated Successfully");
        response.setStatusCode(200);
        return response;
    }

    public void generateNotesForSubtopic(String model, Long subtopic) {
        Optional<CurriTopic> curriTopic = curriTopicRepository.findById(subtopic);

        generateNotesForSubtopic(model, curriTopic.get());
    }


    public void generateNotesForSubtopic(String model, CurriTopic curriTopic) {
        System.out.println("generateNotesForSubtopic");
        if (curriTopic.getCurriLevel().getCurriculum() == 1) {
            System.out.println("Cant continue because curriculum is extinct");
            return;
        }
        if (subjectLevelRepository.subjectIsdeleted(curriTopic.getSubject().id, curriTopic.getCurriLevel().id) > 0) {
            System.out.println("Cant continue because subject is deleted");
            return;
        }
        if (curriTopic.getIsParent() != null && curriTopic.getIsParent()) {
            List<CurriTopic> curriTopicList  =  curriTopicRepository.findByParent(curriTopic.getId());
            for (CurriTopic subCurriTopic: curriTopicList) {
                generateNotesForSubtopic(model, subCurriTopic);
            }
            return;
        }

        Optional< CurriNotes> curriNotesList = curriNotesRepository.findBySubtopic(curriTopic);
        if (!curriNotesList.isEmpty())
            return;

        System.out.println("In generateForSubtopic");
        String purpose = "curri_question";
        System.out.println("Sutopic "+curriTopic.id);
        AIQuery aiQuery = new AIQuery();
        String sanitizedQuestion = getNotesGenerationInstructions(curriTopic);
//        String sanitizedQuestion = question.replaceAll("\\r|\\n", " ");

        System.out.println("----------------------------");

        System.out.println(sanitizedQuestion);

//        if (1 == 1)
//            return;

        aiQuery.setSubtopicId(curriTopic.getId());
        aiQuery.setQueryQuestion(sanitizedQuestion);
        aiQuery.setAIModel("gpt-5-mini");
        aiQuery.setQueryPurpose(purpose);

        aiQuery = aiQueryRepo.save(aiQuery);

        String response = gpt5MiniQuery(sanitizedQuestion);

        aiQuery.setAiResponse(response);

        aiQuery = aiQueryRepo.save(aiQuery);
        if (response != null) {
            aiQuery.setMigrated(true);
            saveNotes(curriTopic, response, model);
        }
    }

    private String buildApprovalPrompt(CurriNotes curriNotes, CurriTopic subtopic) {
        StringBuilder prompt = new StringBuilder();
        // Include the original instructions for generating questions
          prompt.append("    You are reviewing AI-generated multiple-choice questions for approval. \n")
                .append("    Each question has four choices, exactly one of which is correct. \n")
                .append("    Approve only well-formed and accurate questions. \n")
                .append("    If rejecting a question, provide a reason in at most 3 words. \n")
                .append("    Return a json object with a json array(named questions) of objects in format with fields: id, approved (true/false), and reason (if rejected).\n\n")
                .append("    The questions were generated using the following instructions:\n")
                .append(getNotesGenerationInstructions(subtopic)) // Append original generation instructions
                .append("\n");

          //to append the notes to be approved

        return prompt.toString();
    }

    private String notesPromptTemplate(CurriTopic curriTopic) {
        String question = """
            Create notes in html format(div element being parent, dont use body or html as the holding element, 
            start with div, you can use inline css) for subtopic: 'subtopic_name_replace',  
            on topic: 'topic_name_replace', subject: 'subject_replace' .
            
            Fit the content to Kenyan and target age as age_replace. You can use simple visual expressions where necessary in the html. 
              If the subject in this prompt is a language, focus only on grammatical matters in that language. 
           
           Please use below only as a guide on what should be contained in the notes, not as the notes themselves. Don't include 'Suggested Learning Experiences' whatsoever.
           
            ---Specific Learning outcomes---
            (specificLearningOutcomes)
            --------------------------------
            
            ---Suggested Learning Experiences----
            (suggestLearningExperiences)
            -------------------------------------
             
            """;

        if (curriTopic.getParent() != null && curriTopic.getParent().getParent() != null)
            question = """
            'Create notes in html format(div element being parent,  dont use body or html as the holding element, start with div, you can use inline css. Use maroon as main title color) for sub subtopic: 'sub_subtopic_name_replace', subtopic: 'subtopic_name_replace', topic: 'topic_name_replace', subject: 'subject_replace'.
            (specificLearningOutcomes) 
            (suggestLearningExperiences) 
            Fit the content to Kenyan and target age as age_replace.  You can use simple visual expressions where necessary in the html. 
            If the subject in this prompt is a language, focus only on grammatical matters in that language.        
            """;
        return question;
    }

    // Helper method to get the generation instructions
    private String getNotesGenerationInstructions(CurriTopic curriTopic) {
        String question = notesPromptTemplate(curriTopic);

        if (curriTopic.getSpecificLearningOutcomes() == null)
            question = question.replaceAll("\\(specificLearningOutcomes\\)", "");
        else
            question = question.replaceAll("specificLearningOutcomes", curriTopic.getSpecificLearningOutcomes());

        if (curriTopic.getParent() == null || curriTopic.getParent().getSuggestLearningExperiences() == null)
            question = question.replaceAll("\\(suggestLearningExperiences\\)", "");
        else
            question = question.replaceAll("suggestLearningExperiences", curriTopic.getParent().getSuggestLearningExperiences());

        if (curriTopic.getParent() != null && curriTopic.getParent().getParent() != null) {
            question = question.replaceAll("sub_subtopic_name_replace", curriTopic.getName());
            question = question.replaceAll("subtopic_name_replace", curriTopic.getParent().getName());
            question = question.replaceAll("topic_name_replace", curriTopic.getParent().getParent().getName());
        } else {
            question = question.replaceAll("subtopic_name_replace", curriTopic.getName());
            if (curriTopic.getParent() != null)
                question = question.replaceAll("topic_name_replace", curriTopic.getParent().getName());
        }
        question = question.replaceAll("subtopic_name_replace", curriTopic.getName());
        if (curriTopic.getParent() != null){
            question = question.replaceAll("topic_name_replace", curriTopic.getParent().getName());
            question = question.replaceAll("age_replace", curriTopic.getParent().getCurriLevel().getAgeEstimate().toString());
            question = question.replaceAll("subject_replace", curriTopic.getParent().getSubject().getName());
        }


        return question.replaceAll("\\r|\\n", " ");
    }


    private void saveNotes(CurriTopic curriTopic, String response, String aiModel) {
        CurriNotes curriNotes = new CurriNotes();
        curriNotes.setContent(response);
        curriNotes.setSubtopic(curriTopic);
        curriNotes.setBookModel(aiModel);

        System.out.println(curriNotes);

        curriNotesRepository.save(curriNotes);
    }

    public static String escapeStringForSql(String input) {
        if (input == null) {
            return null;
        }

        // Common escape sequences
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\'':
                    escaped.append("''"); // Escape single quote
                    break;
                case '\\':
                    escaped.append("\\\\"); // Escape backslash
                    break;
                case '\0':
                    escaped.append("\\0"); // Escape null character
                    break;
                case '\n':
                    escaped.append("\\n"); // Escape newline
                    break;
                case '\r':
                    escaped.append("\\r"); // Escape carriage return
                    break;
                case '\t':
                    escaped.append("\\t"); // Escape tab
                    break;
                case '\b':
                    escaped.append("\\b"); // Escape backspace
                    break;
                case '\032':
                    escaped.append("\\Z"); // Escape escape character
                    break;
                case '%':
                    escaped.append("\\%"); // Escape percent for LIKE queries
                    break;
                case '_':
                    escaped.append("\\_"); // Escape underscore for LIKE queries
                    break;
                default:
                    escaped.append(c);
                    break;
            }
        }

        return escaped.toString();
    }


    private void writeToFile(String content){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("updates.sql", true))) {
            writer.write(content);
            System.out.println("Content written to file successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
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
    private String gpt5MiniQuery(String question) {
        return normalChatGptCall(question, "gpt-5-mini");
    }

    private String normalChatGptCall(String question, String model) {
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
                JSONObject jsonObject = new JSONObject(response.toString());
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

    // Escape XML special characters to avoid issues
    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


    // DTO for AI Approval Response
    @Data
    static class QuestionApproval {
        private Long id;
        private boolean approved;
        private String reason;
    }
}
