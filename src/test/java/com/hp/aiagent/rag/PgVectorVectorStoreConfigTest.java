package com.hp.aiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Auther:hp
 * @Date:2025/9/20
 * @Description:com.hp.aiagent.rag
 **/
@SpringBootTest
class PgVectorVectorStoreConfigTest {
    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("有什么故障", Map.of("meta1", "meta1")),
                new Document("如何诊断"),
                new Document("你好", Map.of("meta2", "meta2")));

        pgVectorVectorStore.add(documents);

        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("特征").topK(3).build());
        Assertions.assertNotNull(results);
    }
}