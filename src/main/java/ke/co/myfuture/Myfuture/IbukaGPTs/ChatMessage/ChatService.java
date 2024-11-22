package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChat;
import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChatRepository;
import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChatService;
import ke.co.myfuture.Myfuture.IbukaGPTs.aimodels.openai.OpenAIChatRequest;
import ke.co.myfuture.Myfuture.IbukaGPTs.aimodels.openai.OpenAIChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ke.co.myfuture.Myfuture.Utils.Response.OnlineUtils.isJSONValid;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private GptChatRepository gptChatRepository;

    @Autowired
    private WebClient openAIClient;

    @Autowired
    private GptChatService gptChatService;

    @Value("${openai.api.key}")
    private String chatGPTKey;

//    private static final String CHATGPT_MODEL = "gpt-3.5-turbo"; // or use gpt-4 if available

    public ChatMessage addUserChatMessage(ChatMessageRequest request) {
        System.out.println("About to do an AI query");
        // Find associated GptChat
        GptChat gptChat;
        if (request.getGptChatId() == 0) {
            gptChat = gptChatService.addGptChat(request.getModel());
        } else {
            Optional<GptChat> gptChatOptional = gptChatRepository.findById(request.getGptChatId());
            if (gptChatOptional.isEmpty()) {
                throw new IllegalArgumentException("GptChat with id " + request.getGptChatId() + " not found");
            }
            gptChat = gptChatOptional.get();
        }

        // Persist user's chat message
        ChatMessage userMessage = ChatMessage.builder()
                .message(request.getMessage())
                .model(request.getModel())
                .sender(ChatMessage.Sender.USER)
                .gptChat(gptChat)
                .build();

        chatMessageRepository.save(userMessage);

        // Fetch the conversation history to provide context
        List<OpenAIChatRequest.Message> messages = getConversationHistory(gptChat);

        // Send request to ChatGPT API
        OpenAIChatRequest aiRequest = new OpenAIChatRequest(request.getModel(), messages);
        OpenAIChatResponse aiResponse = getChatGPTResponse(aiRequest);

        System.out.println("Got AI response");

        System.out.println(aiResponse);

        System.out.println(aiResponse.getChoices()[0].getMessage().getContent());
        // Persist AI's response
        ChatMessage aiMessage = ChatMessage.builder()
                .message(aiResponse.getChoices()[0].getMessage().getContent())
                .sender(ChatMessage.Sender.SYSTEM)
                .gptChat(gptChat)
                .build();

        chatMessageRepository.save(aiMessage);

        return aiMessage;
    }

    private List<OpenAIChatRequest.Message> getConversationHistory(GptChat gptChat) {
        return chatMessageRepository.findByGptChat(gptChat).stream()
                .map(msg -> new OpenAIChatRequest.Message(
                        msg.getSender().name().toLowerCase(),
                        msg.getMessage()))
                .collect(Collectors.toList());
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
            con.setRequestProperty("Authorization", "Bearer " +chatGPTKey );

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

                isJSONValid= isJSONValid(response.toString());
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


    private OpenAIChatResponse getChatGPTResponse(OpenAIChatRequest request) {
        return openAIClient.post()
                .body(Mono.just(request), OpenAIChatRequest.class)
                .retrieve()
                .bodyToMono(OpenAIChatResponse.class)
                .block();
    }

    public List<ChatMessage> allForChatId(Long chatId) {
        Optional<GptChat> gptChatOptional = gptChatRepository.findById(chatId);

        if (gptChatOptional.isEmpty()) {
            throw new IllegalArgumentException("GptChat with id " + chatId + " not found");
        }


        return allForChat(gptChatOptional.get());
    }

    public List<ChatMessage> allForChatUuid(String chatUuid) {
        Optional<GptChat> gptChatOptional = gptChatRepository.findByUuid(chatUuid);

        if (gptChatOptional.isEmpty()) {
            throw new IllegalArgumentException("GptChat with uuid " + chatUuid + " not found");
        }

        return allForChat(gptChatOptional.get());
    }
    public List<ChatMessage> allForChat(GptChat gptChat) {
        List<ChatMessage> messages = chatMessageRepository.findByGptChat(gptChat);
        for (ChatMessage message : messages) {
            message.setGptChat(gptChat);
        }
        return messages;
    }


}


