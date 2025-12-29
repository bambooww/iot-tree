package org.iottree.ext.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class AIAgent
{
	String prompt = null ;
	
	public AIAgent()
	{
		
	}
	
	public String getPromptTxt()
	{
		return this.prompt ;
	}
	
	
	
	//final ChatModel chatModel;

    private static final String MODEL_NAME = "qwen3:4b";

    private static final String MODEL_TYPE = "qwen3";



    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    public String getModelType() {
        return MODEL_TYPE;
    }
    
    public static void test()
    {
    	ChatModel chatModel = OllamaChatModel.builder().baseUrl("http://127.0.0.1:11434").modelName(MODEL_NAME).build();
    	String res = chatModel.chat("你是谁？");
    	System.out.println(res) ;
    }

    public static void main(String[] args)
    {
    	ChatModel chatModel = OllamaChatModel.builder().baseUrl("http://127.0.0.1:11434").modelName(MODEL_NAME).build();
    	String res = chatModel.chat("你是谁？");
    	System.out.println(res) ;
    }
}
