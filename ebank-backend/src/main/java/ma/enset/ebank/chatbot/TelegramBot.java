package ma.enset.ebank.chatbot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/** Client Telegram du chatbot : chaque message reçu est transmis à l'agent RAG. */
@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final ChatbotService chatbotService;
    private final String botUsername;
    private final String botToken;

    public TelegramBot(ChatbotService chatbotService,
                       @Value("${telegram.bot.token:}") String botToken,
                       @Value("${telegram.bot.username}") String botUsername) {
        super(botToken == null || botToken.isBlank() ? "disabled" : botToken);
        this.chatbotService = chatbotService;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @PostConstruct
    public void register() {
        if (botToken == null || botToken.isBlank()) {
            log.warn("TELEGRAM_BOT_TOKEN absent : le bot Telegram n'est pas démarré");
            return;
        }
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
            log.info("Bot Telegram @{} démarré", botUsername);
        } catch (Exception e) {
            log.error("Démarrage du bot Telegram impossible", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String chatId = update.getMessage().getChatId().toString();
        String question = update.getMessage().getText();
        try {
            execute(SendChatAction.builder().chatId(chatId).action(ActionType.TYPING.toString()).build());
            String answer = question.startsWith("/start")
                    ? "Bonjour ! Je suis l'assistant E-Bank. Posez-moi une question sur vos comptes ou nos services."
                    : chatbotService.ask(question);
            execute(SendMessage.builder().chatId(chatId).text(answer).build());
        } catch (Exception e) {
            log.error("Erreur Telegram", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
