package com.hp.aiagent.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Auther:hp
 * @Date:2025/9/19
 * @Description:com.hp.aiagent.app
 **/
@SpringBootTest
class MyAppTest {

    @Autowired
    private MyApp myApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，请分析故障";
        String answer = myApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "故障原因是不是温度";
        answer = myApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "故障原因是什么？请回忆";
        answer = myApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "故障诊断";
        MyApp.Report report = myApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(report);

    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "正常运行的风机，其振动频谱应具备哪些特征";
        String answer = myApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);

    }

    @Test
    void doChatWithTools() {

        testMessage("推荐几个诊断书籍");


        testMessage("最近机器故障，看看网站（.cn）怎么解决的？");


        testMessage("直接下载一张故障终端图片为文件");


        testMessage("执行 Python3 脚本来生成数据分析报告");


        testMessage("保存我的档案为文件");


        testMessage("生成一份裂纹诊断计划PDF，包含诊断流程");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = myApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();

        String message = "如何从振动频谱中识别出设备存在轻微不对中";
        String answer = myApp.doChatWithMcp(message, chatId);
    }

}