package com.hp.aiagent.app;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.hp.aiagent.advisor.MyLoggerAdvisor;
import com.hp.aiagent.advisor.ReReadingAdvisor;
import com.hp.aiagent.chatmemory.FileBasedChatMemory;
import com.hp.aiagent.rag.AppRagCustomAdvisorFactory;
import com.hp.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Auther:hp
 * @Date:2025/9/19
 * @Description:com.hp.aiagent.app
 **/
@Component
@Slf4j
public class MyApp {
    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = "你是一名深耕旋转机械故障诊断领域的专家，熟悉风机、泵、轴承、齿轮箱、电机等设备的运行特性及常见故障类型。" +
            "用户可以向你倾诉设备运行状态或故障困扰。" +
            "根据设备状态不同，你将引导用户提供相关信息：" +
            "正常运行状态：询问设备振动、温度、噪声、电流等参数是否异常，及其典型表现。" +
            "轻微异常（早期故障）状态：询问是否观察到轻微振动、温升、噪声变化，或运行效率略下降，是否已记录频谱或电流特征。" +
            "严重故障状态：询问设备出现的明显振动、剧烈噪声、温度异常、机械撞击或润滑异常情况，是否已经采取紧急措施。" +
            "在提问过程中，引导用户详细描述：" +
            "故障或异常的时间、发生频率、持续时间" +
            "对应的设备部位及测点" +
            "观察到的参数变化（振动幅值、频率、电流、电压、温度等）" +
            "已采取的操作或处理方法" +
            "基于用户提供的完整信息，给出针对性的诊断分析、可能故障原因及建议的处理方案，必要时提醒紧急停机或检修措施。";

    public MyApp(ChatModel dashscopeChatModel) {
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        //自定义拦截器
                        new MyLoggerAdvisor()
                        //自定义 ReReadingAdvisor 重读增强推理
                        // ,new ReReadingAdvisor()
                )
                .build();

    }

    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;

    }

    record Report(String title, List<String> suggestions) {
    }

    public Report doChatWithReport(String message, String chatId) {
        Report report = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成诊断结果，标题为{用户名}的诊断报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(Report.class);
        log.info("loveReport: {}", report);
        return report;
    }

    //rag知识库问答
    @Resource
    private VectorStore AppVectorStore;

    //pgvector向量库
    @Resource
    private VectorStore pgVectorVectorStore;

    //查询重写器使用
    @Resource
    private QueryRewriter queryRewriter;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())

                //本地知识库rag问答
                //.advisors(new QuestionAnswerAdvisor(AppVectorStore))

                //pgvector 向量存储的rag库
                //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))

                .advisors(
                        AppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                AppVectorStore, "轴承"
                        )
                )

                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /*使用工具*/
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /*mcp服务*/
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public Flux<String> doChatByStream(String message, String chatId) {
    return chatClient
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .stream()
            .content();
}


}
