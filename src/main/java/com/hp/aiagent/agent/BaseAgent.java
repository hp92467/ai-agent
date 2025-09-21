package com.hp.aiagent.agent;

import com.hp.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {  
  
    
    private String name;  
  
    
    private String systemPrompt;  
    private String nextStepPrompt;  
  
    
    private AgentState state = AgentState.IDLE;
  
    
    private int maxSteps = 10;  
    private int currentStep = 0;  
  
    
    private ChatClient chatClient;
  
    
    private List<Message> messageList = new ArrayList<>();
  
      
    public String run(String userPrompt) {  
        if (this.state != AgentState.IDLE) {  
            throw new RuntimeException("Cannot run agent from state: " + this.state);  
        }  
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");  
        }  
        
        state = AgentState.RUNNING;  
        
        messageList.add(new UserMessage(userPrompt));
        
        List<String> results = new ArrayList<>();
        try {  
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {  
                int stepNumber = i + 1;  
                currentStep = stepNumber;  
                log.info("Executing step " + stepNumber + "/" + maxSteps);  
                
                String stepResult = step();  
                String result = "Step " + stepNumber + ": " + stepResult;  
                results.add(result);  
            }  
            
            if (currentStep >= maxSteps) {  
                state = AgentState.FINISHED;  
                results.add("Terminated: Reached max steps (" + maxSteps + ")");  
            }  
            return String.join("\n", results);  
        } catch (Exception e) {  
            state = AgentState.ERROR;  
            log.error("Error executing agent", e);  
            return "执行错误" + e.getMessage();  
        } finally {  
            
            this.cleanup();  
        }  
    }  
  
      
    public abstract String step();  
  
      
    protected void cleanup() {  
        
    }

    public SseEmitter runStream(String userPrompt) {

    SseEmitter emitter = new SseEmitter(300000L);


    CompletableFuture.runAsync(() -> {
        try {
            if (this.state != AgentState.IDLE) {
                emitter.send("错误：无法从状态运行代理: " + this.state);
                emitter.complete();
                return;
            }
            if (StringUtil.isBlank(userPrompt)) {
                emitter.send("错误：不能使用空提示词运行代理");
                emitter.complete();
                return;
            }


            state = AgentState.RUNNING;

            messageList.add(new UserMessage(userPrompt));

            try {
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step " + stepNumber + "/" + maxSteps);


                    String stepResult = step();
                    String result = "Step " + stepNumber + ": " + stepResult;


                    emitter.send(result);
                }

                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    emitter.send("执行结束: 达到最大步骤 (" + maxSteps + ")");
                }

                emitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("执行智能体失败", e);
                try {
                    emitter.send("执行错误: " + e.getMessage());
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            } finally {

                this.cleanup();
            }
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    });


    emitter.onTimeout(() -> {
        this.state = AgentState.ERROR;
        this.cleanup();
        log.warn("SSE connection timed out");
    });

    emitter.onCompletion(() -> {
        if (this.state == AgentState.RUNNING) {
            this.state = AgentState.FINISHED;
        }
        this.cleanup();
        log.info("SSE connection completed");
    });

    return emitter;
}
}