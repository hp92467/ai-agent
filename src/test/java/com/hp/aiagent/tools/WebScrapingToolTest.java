package com.hp.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Auther:hp
 * @Date:2025/9/21
 * @Description:com.hp.aiagent.tools
 **/
@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "www.baidu.com";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}