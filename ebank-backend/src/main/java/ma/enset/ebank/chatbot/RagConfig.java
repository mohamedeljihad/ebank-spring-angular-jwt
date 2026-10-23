package ma.enset.ebank.chatbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * RAG : le guide de la banque est découpé en chunks, vectorisé (embeddings OpenAI)
 * puis stocké dans un vector store en mémoire.
 */
@Configuration
@Slf4j
public class RagConfig {

    @Value("classpath:/rag/ebank-guide.md")
    private Resource guide;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    ApplicationRunner loadDocuments(SimpleVectorStore vectorStore) {
        return args -> {
            if (apiKey == null || apiKey.isBlank() || "no-key".equals(apiKey)) {
                log.warn("OPENAI_API_KEY absente : le chatbot RAG est désactivé");
                return;
            }
            List<Document> documents = new TokenTextSplitter().apply(new TextReader(guide).get());
            vectorStore.add(documents);
            log.info("RAG : {} chunks indexés", documents.size());
        };
    }
}
