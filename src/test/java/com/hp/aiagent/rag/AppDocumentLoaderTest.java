package com.hp.aiagent.rag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Auther:hp
 * @Date:2025/9/20
 * @Description:com.hp.aiagent.rag
 **/
@SpringBootTest
class AppDocumentLoaderTest {
    @Autowired
    private AppDocumentLoader appDocumentLoader;
    @Test
    void loadMarkdowns() {
        appDocumentLoader.loadMarkdowns();
    }
}