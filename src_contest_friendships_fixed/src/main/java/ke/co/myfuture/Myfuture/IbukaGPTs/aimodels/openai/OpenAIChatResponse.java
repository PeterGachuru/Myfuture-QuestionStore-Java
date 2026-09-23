package ke.co.myfuture.Myfuture.IbukaGPTs.aimodels.openai;


import lombok.Data;

@Data
public class OpenAIChatResponse {
    private String id;
    private String object;
    private Long created;
    private Usage usage;
    private Choice[] choices;

    @Data
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }

    @Data
    public static class Choice {
        private int index;
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content; // This will contain the HTML-formatted response
        }
    }
}