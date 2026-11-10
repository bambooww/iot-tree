package org.iottree.ext;

public class ExtManager
{

	private static final Class<?>[] GRAALVM_CNS = new Class<?>[]{
		org.iottree.ext.ai.edge.IOTTreeEdge_M.class
		,org.iottree.ext.ai.edge.IOTTE_Gesture_NM.class
		,org.iottree.ext.ai.edge.IOTTE_ImgBlkOCR_NM.class
			
		,org.iottree.ext.ai.mn.LLMOllama_M.class
		,org.iottree.ext.ai.mn.LLMOllamaChat_NM.class
		,org.iottree.ext.ai.mn.LLMOllamaChat_RES.class
		,org.iottree.ext.ai.mn.LLMOllamaEmbed_NM.class
		,org.iottree.ext.ai.mn.LLMOllamaEmbed_RES.class
			
		,org.iottree.ext.ai.mn.VLLM_M.class
		,org.iottree.ext.ai.mn.VLLMChat_NM.class
			
		,org.iottree.ext.ai.mn.LLMOnline_M.class
		,org.iottree.ext.ai.mn.LLMOnlineChat_NM.class
			
		,org.iottree.ext.ai.mn.LLMTooling_M.class
		,org.iottree.ext.ai.mn.LLMToolFunc_RES.class
			
		,org.iottree.ext.ai.mcp.MCPServer_M.class
		//,org.iottree.ext.ai.mcp.MCPTool_RES.class
			
		,org.iottree.ext.ai.dev.DevCtrl_M.class
		,org.iottree.ext.ai.dev.DevCtrlDevItem_NS.class
		,org.iottree.ext.ai.dev.DevCtrlRequest_NM.class
		,org.iottree.ext.ai.dev.DevCtrlRespHandle_NM.class
		,org.iottree.ext.ai.dev.DevCtrlTagsWriter_NM.class
			
	};
}
