package ke.co.myfuture.Myfuture.IbukaGPTs.aimodels.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OpenAIChatRequest {
    private String model;
    private List<Message> messages;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role; // "user" or "system"
        private String content;
    }
}