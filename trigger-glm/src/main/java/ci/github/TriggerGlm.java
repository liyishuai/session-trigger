package ci.github;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatError;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.ChatThinkingType;

/**
 * Trigger GLM API using the z-ai-sdk-java library.
 * Outputs JSON status information for GitHub Actions.
 */
public class TriggerGlm {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // SDK reads ZAI_API_KEY from env automatically
        ZhipuAiClient client = ZhipuAiClient.builder().build();

        // Make API call
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(Constants.ModelChatGLM4_5_AIR)
                .thinking(ChatThinking.builder().type(ChatThinkingType.DISABLED.value()).build())
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.SYSTEM.value())
                                .content("result only")
                                .build(),
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("1+1")
                                .build()))
                .build();

        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        if (response.isSuccess()) {
            // Success case - output JSON
            System.out.println(MAPPER.createObjectNode()
                    .put("success", true)
                    .set("response", MAPPER.valueToTree(response))
                    .toString());
        } else {
            // API returned an error - output ChatError code and message
            ChatError error = response.getError();
            System.out.println(MAPPER.createObjectNode()
                    .put("code", error.getCode())
                    .put("message", error.getMessage())
                    .put("success", false)
                    .toString());
        }

        client.close();
    }
}
