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
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "百度.pdf";
        String content = "www.baidu.com";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
