package ma.enset.ebank.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Agent conversationnel : RAG (guide de la banque) + outils (données réelles des comptes). */
@Service
public class ChatbotService {
    private static final String SYSTEM = """
            Tu es l'assistant virtuel de la banque E-Bank ENSET. Réponds en français, de façon concise.
            Utilise le contexte fourni (guide de la banque) pour les questions sur les produits, frais et procédures.
            Utilise les outils disponibles pour toute question sur les clients, les comptes, les soldes ou les opérations.
            Si l'information n'est ni dans le contexte ni dans les outils, dis que tu ne sais pas.
            """;

    private final ChatClient chatClient;
    private final BankTools bankTools;
    private final boolean enabled;

    public ChatbotService(ChatClient.Builder builder, SimpleVectorStore vectorStore, BankTools bankTools,
                          @Value("${spring.ai.openai.api-key:no-key}") String apiKey) {
        this.bankTools = bankTools;
        this.enabled = apiKey != null && !apiKey.isBlank() && !"no-key".equals(apiKey);
        this.chatClient = builder
                .defaultSystem(SYSTEM)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .build();
    }

    public String ask(String question) {
        if (!enabled) {
            return "Le chatbot est désactivé : définissez la variable d'environnement OPENAI_API_KEY puis redémarrez le backend.";
        }
        try {
            return chatClient.prompt()
                .user(question)
                .tools(bankTools)
                .call()
                .content();
        } catch (Exception e) {
            return "Le service d'IA est momentanément indisponible : " + e.getMessage();
        }
    }
}
