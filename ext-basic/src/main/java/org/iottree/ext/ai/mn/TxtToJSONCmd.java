package org.iottree.ext.ai.mn;

import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TxtToJSONCmd extends MNNodeMid
{

	@Override
	public String getTP()
	{
		return "tt_jc";
	}

	@Override
	public String getTPTitle()
	{
		return g("tt_jc");
	}

	@Override
	public String getColor()
	{
		return "#a349a4";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "PK_flw";
	}
	
	@Override
	public int getOutNum()
	{
		return 1;
	}


	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		//AIAgent.test();
		return null;
	}
	
//	private static final String API_URL = "http://localhost:11434/api/generate";
//    private final ObjectMapper mapper = new ObjectMapper();
//    public String generateText(String prompt, int maxTokens) throws Exception {
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            HttpPost post = new HttpPost(API_URL);
//            // 构建请求体
//            String jsonBody = String.format(
//                "{\"model\":\"qwen:4b\",\"prompt\":\"%s\",\"max_tokens\":%d}",
//                prompt, maxTokens);
//            post.setEntity(new StringEntity(jsonBody));
//            post.setHeader("Content-Type", "application/json");
////            //
////            String response = (String)client.execute(post, httpResponse -> {
////                return EntityUtils.toString(httpResponse.getEntity());
////            });
//            //
//            Map<String, Object> result = mapper.readValue(response, Map.class);
//            return (String) ((Map<String, Object>) result.get("response")).get("content");
//        }
//    }
    
    
}
