package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import ke.co.myfuture.Myfuture.QuestionStore.CurriNormalChoice.CurriNormalChoice;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionService;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import ke.co.myfuture.Myfuture.Utils.Response.ResponseType;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatGPTQuestionsService {
    @Autowired
    CurriTopicRepository curriTopicRepository;
    @Autowired
    AIQueryRepo aiQueryRepo;

    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Value("${ai.api_key}")
    private String chatGPTKey;

    @Autowired
    private CurriQuestionService curriQuestionService;


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


//    @Bean
    public void queryCurriQuestionsForAllSubtopics() {
        System.out.println("IN queryCurriQuestionsForAllSubtopics");
        String purpose = "curri_question";
        List<CurriTopic> curriSubTopics = curriTopicRepository.findSubtopicsWithLessAIQuestions();
        System.out.println("Count: "+curriSubTopics.size());
        for (CurriTopic curriTopic: curriSubTopics) {
            System.out.println("Sutopic "+curriTopic.id);
//            if(!(curriTopic.getParent().getName().toLowerCase().contains("fraction") || curriTopic.getName().toLowerCase().contains("fraction") ))
//                continue;
            AIQuery aiQuery = new AIQuery();
            String question = """
                Create a list of questions for subject_replace students on subtopic_name_replace subtopic of topic_name_replace. Format your response as a json array of 25 objects with a question with a list of 4 choices, 3 of the choices being wrong and 1 right choice and an explanation for the right answer. Fit the content to kenyan and target age as age_replace. Specify which is the correct choice. Use only question, choices, correct_choice and explanation as the fields. Don't assign order to the choices.
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


    public void generateForSubtopic(String model, CurriTopic curriTopic) {
        if (curriTopic.getTotalNumberOfUnverifiedQuestions()+curriTopic.getTotalNumberOfApprovedQuestions() >= 35)
            return;

        System.out.println("IN generateForSubtopic");
        String purpose = "curri_question";
        System.out.println("Sutopic "+curriTopic.id);
        AIQuery aiQuery = new AIQuery();
        String sanitizedQuestion = getGenerationInstructions(curriTopic);
//        String sanitizedQuestion = question.replaceAll("\\r|\\n", " ");

        System.out.println("----------------------------");

        System.out.println(sanitizedQuestion);

//        if (1 == 1)
//            return;

        aiQuery.setSubtopicId(curriTopic.getId());
        aiQuery.setQueryQuestion(sanitizedQuestion);
        aiQuery.setAIModel("gpt-3.5-turbo-0125");
        aiQuery.setQueryPurpose(purpose);

        aiQuery = aiQueryRepo.save(aiQuery);

        String response = gpt3_5Turbo0125Query(sanitizedQuestion);

        aiQuery.setAiResponse(response);

        aiQuery = aiQueryRepo.save(aiQuery);
        if (isJSONValid(response)) {
            aiQuery.setMigrated(true);
            saveQuestionArray(curriTopic, response);
        }
    }

    private String buildApprovalPrompt(List<CurriQuestion> questions, CurriTopic subtopic) {
        StringBuilder prompt = new StringBuilder();

        // Include the original instructions for generating questions
        prompt.append("    You are reviewing AI-generated multiple-choice questions for approval. \n")
                .append("    Each question has four choices, exactly one of which is correct. \n")
                .append("    Approve only well-formed and accurate questions. \n")
                .append("    If rejecting a question, provide a reason in at most 3 words. \n")
                .append("    Return a json object with a json array(named questions) of objects in format with fields: id, approved (true/false), and reason (if rejected).\n\n")
                .append("    The questions were generated using the following instructions:\n")
                .append(getGenerationInstructions(subtopic)) // Append original generation instructions
                .append("\n");

        prompt.append("  <Questions>\n");

        for (CurriQuestion question : questions) {
            prompt.append("    <Question>\n")
                    .append("      <Id>").append(question.getId()).append("</Id>\n")
                    .append("      <Text>").append(escapeXml(question.getString())).append("</Text>\n")
                    .append("      <Choices>\n");

            for (String choice : getChoicesList(question)) {
                prompt.append("        <Choice>").append(escapeXml(choice)).append("</Choice>\n");
            }

            prompt.append("      </Choices>\n")
                    .append("      <CorrectChoice>").append(escapeXml(getCorrectChoice(question))).append("</CorrectChoice>\n")
                    .append("    </Question>\n");
        }

        prompt.append("  </Questions>\n");
        return prompt.toString();
    }

    // Helper method to get the generation instructions
    private String getGenerationInstructions(CurriTopic curriTopic) {
        String question = """
        Create a list of questions for subject_replace students on subtopic_name_replace subtopic of topic_name_replace.
        (InstructionsOnGenerationOfQuestions_subtopic)
        (InstructionsOnGenerationOfQuestions_topic)
        Format your response as a json array of 25 objects with a question with a list of 4 choices,
        3 of the choices being wrong and 1 right choice and an explanation for the right answer.
        Fit the content to Kenyan and target age as age_replace.
        Specify which is the correct choice.
        Use only question, choices, correct_choice, and explanation as the fields.
        Do not add A, B, C, D or any numbering to the answer choices.
        Just provide the choices as plain text.
        """;

        if (curriTopic.getInstructionsOnGenerationOfQuestions() == null)
            question = question.replaceAll("\\(InstructionsOnGenerationOfQuestions_subtopic\\)", "");
        else
            question = question.replaceAll("InstructionsOnGenerationOfQuestions_subtopic", curriTopic.getInstructionsOnGenerationOfQuestions());

        if (curriTopic.getParent().getInstructionsOnGenerationOfQuestions() == null)
            question = question.replaceAll("\\(InstructionsOnGenerationOfQuestions_topic\\)", "");
        else
            question = question.replaceAll("InstructionsOnGenerationOfQuestions_topic", curriTopic.getParent().getInstructionsOnGenerationOfQuestions());

        question = question.replaceAll("subtopic_name_replace", curriTopic.getName());
        question = question.replaceAll("topic_name_replace", curriTopic.getParent().getName());
        question = question.replaceAll("age_replace", curriTopic.getParent().getCurriLevel().getAgeEstimate().toString());
        question = question.replaceAll("subject_replace", curriTopic.getParent().getSubject().getName());

        return question.replaceAll("\\r|\\n", " ");
    }


    private List<Question> responseToQuestionArray(String response) {
        if (response == null )
            return null;
        response = response.trim();
        if (!response.startsWith("{") || !response.endsWith("}")) {
//            System.out.println("Is not json");
            return null;
        }

//        System.out.println("To convert to questions");

        Gson gson = new Gson();

        Questions questions;
        try {
            questions = gson.fromJson(response, Questions.class);
        } catch (Exception e) {
            return null;
        }
        return questions.questions;
    }

    private void saveQuestionArray(CurriTopic curriTopic, String response) {
        // Parse the JSON array string into a list of MyObject
        List<Question> myObjects = responseToQuestionArray(response);

        long questionsUpdateId;

        if (myObjects != null)
        for (Question question: myObjects) {
            CurriQuestion curriQuestion = new CurriQuestion();
            curriQuestion.setString(question.getQuestion());
            curriQuestion.setExplanation(question.getExplanation());
            curriQuestion.setSubtopic(curriTopic);
            curriQuestion.setBookModel(BookInitialModels.chatGpt3_5);
            curriQuestion.setHasImage(false);

            List<CurriNormalChoice> choices = new ArrayList<>();
            boolean foundRight = false;
            if (question.getChoices() != null)
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
            questionsUpdateId = curriQuestionService.getNewUpdateId();
            curriQuestionService.saveNewQuestion(curriQuestion, choices, questionsUpdateId);
        }
    }

    public void updateExplanationForgotten() {
        StringBuilder sql = new StringBuilder();
        Pageable paging = PageRequest.of(0, 10);
        Page<AIQuery> curriQuestions = aiQueryRepo.findAllForQuestions(paging);
        int totalPages = curriQuestions.getTotalPages();
        Set<Long> subtopics = new HashSet<>();
        for (int i = 0; i < totalPages; i++) {
            System.out.println("page: "+i+" / "+totalPages);
            paging = PageRequest.of(i, 10);
            curriQuestions = aiQueryRepo.findAllForQuestions(paging);
            for (AIQuery aiQuery: curriQuestions.getContent()) {
                long questionsUpdateId;
                List<Question> myObjects = responseToQuestionArray(aiQuery.getAiResponse());
                if (myObjects != null) {
                    for (Question question: myObjects) {
                        questionsUpdateId = curriQuestionService.getNewUpdateId();
//                        System.out.println("question: "+question.getQuestion());
//                        System.out.println("explanation: "+question.getExplanation());
//                        subtopics.add(aiQuery.getSubtopicId());
                        sql.append("UPDATE curri_question SET explanation = '").append(escapeStringForSql(question.getExplanation())).append("' WHERE subtopic = "+aiQuery.getSubtopicId()+" AND string LIKE '%").append(escapeStringForSql(question.getQuestion().trim())).append("%';\n");
//                        sql.append("SELECT * FROM curri_question WHERE explanation LIKE '%").append(escapeStringForSql(question.getExplanation())).append("%' AND subtopic = "+aiQuery.getSubtopicId()+" AND string LIKE '%").append(escapeStringForSql(question.getQuestion().trim())).append("%';\n");
//                        curriQuestionRepository.updateExplanation(question.getExplanation(), question.getQuestion().trim(), questionsUpdateId);
                    }
                }
            }
            writeToFile(sql.toString());
            sql = new StringBuilder();
//            break;
        }
        System.out.println(Arrays.deepToString(subtopics.toArray()));
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
//                System.out.println(responseString);

//                isJSONValid= isJSONValid(response.toString());
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

    public void approveQuestionsWithAIByTopic(Long topicId, SseEmitter emitter) throws IOException {
        List<CurriTopic> subtopics = curriTopicRepository.findByParent(topicId);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Processing");
        response.setMessage("To approve "+subtopics.size()+" subtopics");
        response.setStatusCode(100);
        response.setResponseType(ResponseType.MESSAGE);
        emitter.send(response);
        for (CurriTopic subtopic: subtopics) {
            approveQuestionsWithAIBySubtopic(subtopic, emitter);
        }
    }


    public void approveQuestionsWithAIBySubtopic(CurriTopic subtopic, SseEmitter emitter) throws IOException {
        String model = "gpt-3.5-turbo-0125";
        List<CurriQuestion> unapprovedQuestions = curriQuestionRepository.findUnapprovedQuestionsBySubtopic(subtopic.getId(), 15); // Fetch questions needing approval

        if (unapprovedQuestions.isEmpty()) {
            System.out.println("No questions to approve.");
            return;
        }

        // Build the AI request
        String approvalPrompt = buildApprovalPrompt(unapprovedQuestions, subtopic);

        String sanitizedPrompt = approvalPrompt.replaceAll("\\r|\\n", " ")
                .replace("\"", "\\\"");;


        // Send request to AI
        String response = gpt3_5Turbo0125Query(sanitizedPrompt);

        // Process AI response
        if (isJSONValid(response)) {
            System.out.println("It is valid json "+subtopic.getId());
            List<QuestionApproval> approvals = parseApprovalResponse(response, subtopic);
            updateQuestionApprovalStatus(subtopic, approvals, model, emitter);
        }
    }

    private List<String> getChoicesList(CurriQuestion question) {
        return question.getChoices().stream()
                .map(CurriNormalChoice::getValue)
                .collect(Collectors.toList());
    }

    private String getCorrectChoice(CurriQuestion question) {
        return question.getChoices().stream()
                .filter(CurriNormalChoice::isCorrect)
                .map(CurriNormalChoice::getValue)
                .findFirst()
                .orElse("Unknown");
    }

    // Escape XML special characters to avoid issues
    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
    private List<QuestionApproval> parseApprovalResponse(String response, CurriTopic subtopic) {
        System.out.println(subtopic.getId() + " Response: " + response);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApprovalResponse approvalResponse = objectMapper.readValue(response, ApprovalResponse.class);
            return approvalResponse.getQuestions(); // Extract the list from the object
        } catch (IOException e) {
            System.out.println("Error " + subtopic.getId());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // DTO for wrapping the response
    @Data
    static  class ApprovalResponse {
        private List<QuestionApproval> questions;
    }

    // DTO for AI Approval Response
    @Data
    static class QuestionApproval {
        private Long id;
        private boolean approved;
        private String reason;
    }

    private void updateQuestionApprovalStatus(CurriTopic subtopic, List<QuestionApproval> approvals, String model, SseEmitter emitter) throws IOException {
//        QuestionApprovalsResponse approvalsResponse = new QuestionApprovalsResponse();
        int countApproved = 0, countRejected = 0;
        for (QuestionApproval approval : approvals) {
            Optional<CurriQuestion> questionOpt = curriQuestionRepository.findById(approval.getId());

            if (questionOpt.isPresent()) {
                CurriQuestion question = questionOpt.get();

                if (approval.isApproved()) {
                    countApproved++;
                    question.aIApprove(model);
                } else {
                    countRejected++;
                    question.setReviewed(true);
                    question.setSharable(false);
                    question.setDeleted(true); // Mark rejected questions as deleted
                    question.setDeletionDate(new Date());
                    question.setDeletedBy(model);
                }

                curriQuestionRepository.save(question);
            }
        }
        UniversalResponse response = new UniversalResponse();
        String message = subtopic.getParent().getName()+" -> "+subtopic.getName()+": Total: "+approvals.size()+", Approved: "+countApproved+", Rejected: "+countRejected;
        response.setStatus("Processing");
        response.setStatusCode(100);
        response.setMessage(message);
        response.setResponseType(ResponseType.MESSAGE);
        emitter.send(response);
    }
}
