package com.hp.aiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent {  
  
      
    public abstract boolean think();  
  
      
    public abstract String act();  
  
      
    @Override  
    public String step() {  
        try {  
            boolean shouldAct = think();  
            if (!shouldAct) {  
                return "思考完成 - 无需行动";  
            }  
            return act();  
        } catch (Exception e) {  
            
            e.printStackTrace();  
            return "步骤执行失败: " + e.getMessage();  
        }  
    }  
}