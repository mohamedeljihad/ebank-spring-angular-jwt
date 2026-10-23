package ma.enset.ebank.chatbot;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class ChatRestController {
    private final ChatbotService chatbotService;

    @GetMapping("/chat")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Map<String, String> chat(@RequestParam String query) {
        return Map.of("question", query, "answer", chatbotService.ask(query));
    }
}
