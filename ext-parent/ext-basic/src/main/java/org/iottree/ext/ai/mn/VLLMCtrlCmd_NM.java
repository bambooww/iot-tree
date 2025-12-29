package org.iottree.ext.ai.mn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.ext.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import com.openai.models.completions.CompletionChoice;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionRequest;
import dev.langchain4j.model.openai.internal.chat.ChatCompletionResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VLLMCtrlCmd_NM extends MNNodeMid
{
	int maxToken = 1024;

	@Override
	public String getTP()
	{
		return "vllm_ctrl_cmd";
	}

	@Override
	public String getTPTitle()
	{
		return g("vllm_ctrl_cmd");
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
	public boolean isParamReady(StringBuilder failedr)
	{
		// if(Convert.isNullOrEmpty(this.ollamaHost) || this.ollamaPort<=0)
		// {
		// failedr.append("no valid ollama host:port set") ;
		// return false ;
		// }
		// if(Convert.isNullOrEmpty(modelName))
		// {
		// failedr.append("no model name set") ;
		// return false;
		// }
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		// jo.putOpt("ollama_host", this.ollamaHost) ;
		// jo.put("ollama_port",this.ollamaPort) ;
		// jo.putOpt("model_name",this.modelName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		// this.ollamaHost = jo.optString("ollama_host") ;
		// this.ollamaPort = jo.optInt("ollama_port", 11434) ;
		// this.modelName = jo.optString("model_name") ;
	}

	@Override
	public int getOutNum()
	{
		// Optional<T>.ofNullable(value)
		return 1;
	}
	
	protected RTOut RT_onMsgIn1(MNConn in_conn, MNMsg msg) throws Exception
	{
		String pld = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(pld))
			return null ;
		
		String SYSTEM_PROMPT = "你是一名智能家居管家，只能从下面工具列表里挑选，输出 JSON。\r\n" + 
				"工具列表：\r\n" + 
				"[\r\n" + 
				"  {\"name\":\"bedroomLight\",\"desc\":\"主卧灯\",\"state\":\"on|off\",\"args\":{\"action\":\"on|off\"}},\r\n" + 
				"  {\"name\":\"ac\",\"desc\":\"空调\",\"args\":{\"action\":\"on|off|set_temp\",\"temp\":16-30}}\r\n" + 
				"]\r\n" + 
				
				"当用户询问当前设备状态，请输出：{\"state\":{\"bedroomLight\":\"{{on|off}}\",\"temp\":{{state}}\r\n" +
				"当用需要调整设备状态，请输出："+"{\"plan\":[{\"tool\":\"bedroomLight\",\"action\":\"on\"}]}\r\n"+
				
				"当前状态：{\"bedroomLight_state\":\"off\",\"ac\":\"off\",\"temp_state\":25\"}\r\n";
		
		VLLMCtrl_M owner = (VLLMCtrl_M) this.getOwnRelatedModule();
		StringBuilder failedr = new StringBuilder();
		if (!owner.isParamReady(failedr))
		{
			RT_DEBUG_ERR.fire("owner", "Owner Module Param Not Ready:" + failedr);
			return null;
		}

		String url_base = owner.getVLLMUrlBase();
		
		OpenAiChatModel model = OpenAiChatModel.builder()
			    .baseUrl(url_base+"/v1") // 关键：指向你的vLLM服务
			    .apiKey("no-api-key-needed") // vLLM不需要key，但需填一个非空值
			    .modelName("./Qwen2.5-0.5B-Instruct/") // 必须与启动服务时指定的名称一致
			    .temperature(0.1)
			    .maxTokens(5500)
			    .logRequests(true) // 开启日志，便于调试
			    .logResponses(true)
			    .build();

		ResponseFormat rf = ResponseFormat.builder()
				.type(ResponseFormatType.JSON)
				//.jsonSchema(JsonSchema.builder().name("abc").build())
				.build();
		
		List<ChatMessage> messages = Arrays.asList(
                SystemMessage.from(SYSTEM_PROMPT), // 第一段：系统角色
                UserMessage.from("当前设备状态：\n" + getCurrentDeviceStatus()), // 第二段：状态
                UserMessage.from("用户指令：" + pld) // 第三段：本次指令
        );
		
		//ChatResponse resp = model.chat(messages);
		
//		ChatCompletionRequest req = ChatCompletionRequest.builder()
//				.addSystemMessage(sys)
//				.addUserMessage(pld).build() ;
		
		//UserMessage umsg = UserMessage.from(pld);
		//umsg.

			ChatResponse response = model.chat(messages) ;
			
			JSONObject resp_jo = new JSONObject(response.aiMessage().text()) ;
			//System.out.println() ;
			MNMsg m = new MNMsg().asPayloadJO(resp_jo) ;
					 
		return RTOut.createOutIdx().asIdxMsg(0, m);
	}
	
	private String getCurrentDeviceStatus() {
        // 返回结构化的状态，例如JSON字符串
        return "{\r\n" + 
        		"                  \"devices\": {\r\n" + 
        		"                    \"living_room_light\": { \"state\": \"off\", \"brightness\": 0 },\r\n" + 
        		"                    \"bedroom_ac\": { \"state\": \"on\", \"temperature\": 26 },\r\n" + 
        		"                    \"hallway_motion_sensor\": { \"state\": \"inactive\" }\r\n" + 
        		"                  }\r\n" + 
        		"                }";
    }
	

	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String test_jo_str0 = "{\r\n" + 
				"  \"model\" : \"./Qwen2.5-0.5B-Instruct/\",\r\n" + 
				"  \"messages\" : [ {\r\n" + 
				"    \"role\" : \"system\",\r\n" + 
				"    \"content\" : \"你是编程领域的小助手，帮助用户解答编程学习和求职面试相关的问题，并给出建议。重点关注 4 个方向：\\n1. 规划清晰的编程学习路线\\n2. 提供项目学习建议\\n3. 给出程序员求职全流程指南（比如简历优化、投递技巧）\\n4. 分享高频面试题和面试技巧\\n请用简洁易懂的语言回答，助力用户高效学习与求职。\"\r\n" + 
				"  }, {\r\n" + 
				"    \"role\" : \"user\",\r\n" + 
				"    \"content\" : \"你好，我是程序员鱼皮\\n\\nAnswer using the following information:\\n鱼皮的项目学习建议.md\\n快速学习技术知识：[编程导航知识碎片](https://yuyuanweb.feishu.cn/wiki/AqfawFUT0iD69kkiRKoci6Nqnqc)\\n\\n学会自主解决问题：[利用编程导航解决问题](https://yuyuanweb.feishu.cn/wiki/FY7DwfanEikgzuk3yJlcXRWLnZc)\\n\\n查看真实简历建议：[编程导航简历参考](https://www.code-nav.cn/job/resume)\\n\\n鱼皮的项目学习建议.md\\nSQL 和模拟数据的全栈网站，功能完备，实践 Schema 设计、模板引擎、SQL 解析、模拟数据、Excel  导入导出。（该项目暂无完整教程，可根据开源代码和文档学习）\\n\\n#### 阶段 3 - 快速补充技术栈\\n\\n这些项目小而精，侧重于带你快速入门并实战某一项技术，可以按需选择学习。\\n\\n1. [AI 超级智能体项目（25年最新）](https://www.codefather.cn/course/1915010091721236482)：强烈推荐，学习实践 AI 应用开发，掌握新时代程序员必知的 AI 概念、AI 工具、AI 编程技术。\\n2. [聚合搜索平台](https://www.codefather.cn/course/1790979621621641217)：学习实践爬虫 + **Elastic Stack** + 设计模式 + 数据同步 + JMeter 压力测试。\\n3. [智能 BI 项目](https://www.codefather.cn/course/1790980531403927553)：学习实践异步化 + 线程池 + **RabbitMQ 消息队列** + **AI 应用开发** + AIGC Prompt 优化。\\n\\n#### 阶段 4 - 技术进阶\\n\\n这些项目涉及更多架构设计相关的知识，更侧重技术提升，建议能够熟练开发业务类项目再学习，可以在简历上补充技术轮子类项目。\\n\\n鱼皮的项目学习建议.md\\n负责任的说，我写代码近 8 年，从学生时代开始，可以说 99% 的 Bug 都是自己解决的，所以大家不要有这种担心，因为你学的技术几乎都是主流的，你遇到过的 Bug，别人也一定遇到过。在遇到项目报错时，要先搜集足够多的错误信息（比如通过日志），然后查阅搜索引擎（百度应该都知道吧）、技术社区、官方文档、甚至现在还可以 [问 AI](https://yucongming.com)，大概率是有解决方案的。即使真的没办法解决，在向他人求助前，要保证自己的问题描述地足够清楚、并且清晰列举已经尝试过的解决方案，别人才能更快地帮你解决。\\n\\n推荐阅读：[利用编程导航解决问题](https://yuyuanweb.feishu.cn/wiki/FY7DwfanEikgzuk3yJlcXRWLnZc)\\n\\n#### 7、多读官方文档\\n\\n如今新技术层出不穷，不可能每个新技术都有好心人给你录制保姆级教程。而且工作后，很多公司可能会有自研技术，只有内部同事用过，我们只能通过阅读文档来解决。所以建议大家在学完一门新技术后，花 1 - 2 个小时就好，阅读一下官方文档，不仅能了解一些教程讲解之外的技术特性，还能提升自己阅读文档、学习新技术的能力。\\n\\n#### 8、多写文档\\n\\n除了记笔记外，每做完一个项目，都必须写一篇完整的项目总结文档。不要嫌麻烦，写总结文档的过程中，你会从上帝视角再回顾一遍整个项目的背景、设计、实现、亮点等，帮助你复习巩固、加深印象。也便于你更快地将项目写在简历上、或者开源和分享自己的项目。有能力的同学可以多画一些图，比如功能模块图、架构图、UML 类图等，正所谓一图胜千言，绘图能力也是优秀程序员必备的特质。\\n\\n如果你发现自己写不出总结文档，那么大概率你对这个项目还是不够熟悉，没有完全掌握，这时再对照着自己的笔记快速回顾吧。\\n\\n如果时间比较充足，最好是能够口述整个项目的背景、技术栈、核心业务流程、核心设计、项目难点、开发过程、测试过程、上线过程、解决过最复杂的问题等等，锻炼自己的表达能力，也为后续的面试做了准备。\\n\\n#### 9、自主优化\\n\\n鱼皮的求职指南.md\\n我有 X 段 [学习方向] 项目经历，比如负责 [项目名称] 的开发和上线。曾经遇到了 [具体技术难题，如系统响应速度慢、数据处理效率低等]。我凭借对 [相关技术原理，如算法优化、缓存机制等] 的深入理解，提出并实施了 [具体解决方案，如采用新算法重构代码、引入分布式缓存系统等]，使得系统性能得到显著提升，[具体数据指标，如响应时间缩短 X%、数据处理效率提高 X% 等]，保障了项目的顺利交付。\\n\\n我密切关注行业前沿技术发展，不断学习新知识和技能，提升自己的技术水平，比如最近很火的 AI 技术，也有在项目中实践。我对贵公司正在推进的 [相关业务领域或技术方向] 十分感兴趣，深信自身积累的技术经验与专业能力，能够快速融入团队，为贵公司创造更大价值。\\n\\n当然，你也可以直接把个人情况和岗位描述投喂给 AI，让 AI 帮你生成一段自我介绍，美滋滋~\\n\\n![](https://pic.yupi.icu/1/1741412519933-d2e9d9ea-399a-47b0-abab-4448be91944b-20250312104435640.png)\\n\\n#### 专业技能\\n\\n正常来说，面试官问的问题会和岗位描述匹配，会问很多工作需要的技能。对于技术岗位，要熟练掌握编程语言、技术框架、算法、数据结构、还有常用的软件工具等；对于非技术岗位，则要在沟通技巧、团队协作、问题解决等软实力方面下功夫。\\n\\n对专业技能的准备不用提前很久，我的建议是平时没事儿每天看个几道题目，面试前再突击一下就行，准备得太早了反而容易忘掉。现在有很多工具可以辅助面试准备，比如一些专门练习算法的网站，还有我带团队做的程序员面试刷题神器 [面试鸭](https://www.mianshiya.com/)，帮大家整理了企业常问的高频面试真题并原创了优质题解，不用自己花时间整理题目、不用看鱼龙混杂的答案，节省出来的时间又能准备至少几十道题目了，弯道超车，想想面试时遇到原题的感觉有多爽；还能和其他同学一起交流，提升坚持学习的动力。\\n\\n![](https://pic.yupi.icu/1/1741412641059-a16ce2bf-ca8d-44a7-a700-02a93cc586fb-20250312104435804.png)\\n\\n鱼皮的项目学习建议.md\\n1. [API 开放平台](https://www.codefather.cn/course/1790979723916521474)：学习实践前后端模板开发 + 架构设计 + SDK 开发 + API 签名认证 + Dubbo RPC + Gateway 微服务网关。\\n2. [OJ 判题系统](https://www.codefather.cn/course/1790980707917017089)：学习实践前后端模板开发 + 多种设计模式 + 单体项目微服务改造 + Linux 虚拟机远程开发 + Docker 代码沙箱 + Java 安全控制。\\n3. [手写 RPC 框架](https://www.codefather.cn/course/1768543954720022530)：从 0 到 1 开发轮子，实践网络协议设计 + 序列化 + Etcd 注册中心 + Vert.x 服务器 + 动态代理 + SPI 机制 + 负载均衡 + 服务重试容错机制 + 注解驱动启动器，大幅提升架构设计能力。\\n4. [亿级流量点赞系统（25年最新）](https://www.codefather.cn/course/1912696290659577857)：实践高并发 + 高性能 + 高可用 + 可观测的分布式点赞系统架构设计，涉及 Spring Boot 3 + Java 21 + TiDB + Redis + Pulsar + Docker + Nginx  技术，全面掌握企业级系统开发与优化经验。\\n5. [AI 自动回复工具](https://github.com/liyupi/yu-auto-reply)：基于中介者模式实现的小项目，支持灵活配置多个社交平台的数据监控和回答，并且利用 AI 自动回复。（该项目暂无完整教程，可根据开源代码和文档学习）\\n\\n#### 其他 - 前端实战\\n\\n这些项目以前端为核心，也融合了大量系统设计的方法，适合前端方向、或者后端已经熟练的全栈开发者学习。\"\r\n" + 
				"  } ],\r\n" + 
				"  \"temperature\" : 0.1,\r\n" + 
				"  \"stream\" : false,\r\n" + 
				"  \"max_tokens\" : 25500,\r\n" + 
				"  \"tools\" : [ {\r\n" + 
				"    \"type\" : \"function\",\r\n" + 
				"    \"function\" : {\r\n" + 
				"      \"name\" : \"interviewQuestionSearch\",\r\n" + 
				"      \"description\" : \"Retrieves relevant interview questions from mianshiya.com based on a keyword.\\nUse this tool when the user asks for interview questions about specific technologies,\\nprogramming concepts, or job-related topics. The input should be a clear search term.\\n\",\r\n" + 
				"      \"parameters\" : {\r\n" + 
				"        \"type\" : \"object\",\r\n" + 
				"        \"properties\" : {\r\n" + 
				"          \"keyword\" : {\r\n" + 
				"            \"type\" : \"string\",\r\n" + 
				"            \"description\" : \"the keyword to search\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        \"required\" : [ \"keyword\" ]\r\n" + 
				"      }\r\n" + 
				"    }\r\n" + 
				"  } ]\r\n" + 
				"}\r\n" + 
				"" ;
		
		String test_jo_str = "{\r\n" + 
				"  \"model\" : \"./Qwen2.5-0.5B-Instruct/\",\r\n" + 
				"  \"messages\" : [ {\r\n" + 
				"    \"role\" : \"system\",\r\n" + 
				"    \"content\" : \"你是编程领域的小助手，帮助用户解答编程学习和求职面试相关的问题，并给出建议。重点关注 4 个方向：\\n1. 规划清晰的编程学习路线\\n2. 提供项目学习建议\\n3. 给出程序员求职全流程指南（比如简历优化、投递技巧）\\n4. 分享高频面试题和面试技巧\\n请用简洁易懂的语言回答，助力用户高效学习与求职。\"\r\n" + 
				"  }, {\r\n" + 
				"    \"role\" : \"user\",\r\n" + 
				"    \"content\" : \"你好，我是程序员鱼皮\\n\\nAnswer using the following information:\\n鱼皮的项目学习建议.md\\n快速学习技术知识：[编程导航知识碎片](https://yuyuanweb.feishu.cn/wiki/AqfawFUT0iD69kkiRKoci6Nqnqc)\\n\\n学会自主解决问题：[利用编程导航解决问题](https://yuyuanweb.feishu.cn/wiki/FY7DwfanEikgzuk3yJlcXRWLnZc)\\n\\n查看真实简历建议：[编程导航简历参考](https://www.code-nav.cn/job/resume)\\n\\n鱼皮的项目学习建议.md\\nSQL 和模拟数据的全栈网站，功能完备，实践 Schema 设计、模板引擎、SQL 解析、模拟数据、Excel  导入导出。（该项目暂无完整教程，可根据开源代码和文档学习）\\n\\n#### 阶段 3 - 快速补充技术栈\\n\\n这些项目小而精，侧重于带你快速入门并实战某一项技术，可以按需选择学习。\\n\\n1. [AI 超级智能体项目（25年最新）](https://www.codefather.cn/course/1915010091721236482)：强烈推荐，学习实践 AI 应用开发，掌握新时代程序员必知的 AI 概念、AI 工具、AI 编程技术。\\n2. [聚合搜索平台](https://www.codefather.cn/course/1790979621621641217)：学习实践爬虫 + **Elastic Stack** + 设计模式 + 数据同步 + JMeter 压力测试。\\n3. [智能 BI 项目](https://www.codefather.cn/course/1790980531403927553)：学习实践异步化 + 线程池 + **RabbitMQ 消息队列** + **AI 应用开发** + AIGC Prompt 优化。\\n\\n#### 阶段 4 - 技术进阶\\n\\n这些项目涉及更多架构设计相关的知识，更侧重技术提升，建议能够熟练开发业务类项目再学习，可以在简历上补充技术轮子类项目。\\n\\n鱼皮的项目学习建议.md\\n负责任的说，我写代码近 8 年，从学生时代开始，可以说 99% 的 Bug 都是自己解决的，所以大家不要有这种担心，因为你学的技术几乎都是主流的，你遇到过的 Bug，别人也一定遇到过。在遇到项目报错时，要先搜集足够多的错误信息（比如通过日志），然后查阅搜索引擎（百度应该都知道吧）、技术社区、官方文档、甚至现在还可以 [问 AI](https://yucongming.com)，大概率是有解决方案的。即使真的没办法解决，在向他人求助前，要保证自己的问题描述地足够清楚、并且清晰列举已经尝试过的解决方案，别人才能更快地帮你解决。\\n\\n推荐阅读：[利用编程导航解决问题](https://yuyuanweb.feishu.cn/wiki/FY7DwfanEikgzuk3yJlcXRWLnZc)\\n\\n#### 7、多读官方文档\\n\\n如今新技术层出不穷，不可能每个新技术都有好心人给你录制保姆级教程。而且工作后，很多公司可能会有自研技术，只有内部同事用过，我们只能通过阅读文档来解决。所以建议大家在学完一门新技术后，花 1 - 2 个小时就好，阅读一下官方文档，不仅能了解一些教程讲解之外的技术特性，还能提升自己阅读文档、学习新技术的能力。\\n\\n#### 8、多写文档\\n\\n除了记笔记外，每做完一个项目，都必须写一篇完整的项目总结文档。不要嫌麻烦，写总结文档的过程中，你会从上帝视角再回顾一遍整个项目的背景、设计、实现、亮点等，帮助你复习巩固、加深印象。也便于你更快地将项目写在简历上、或者开源和分享自己的项目。有能力的同学可以多画一些图，比如功能模块图、架构图、UML 类图等，正所谓一图胜千言，绘图能力也是优秀程序员必备的特质。\\n\\n如果你发现自己写不出总结文档，那么大概率你对这个项目还是不够熟悉，没有完全掌握，这时再对照着自己的笔记快速回顾吧。\\n\\n如果时间比较充足，最好是能够口述整个项目的背景、技术栈、核心业务流程、核心设计、项目难点、开发过程、测试过程、上线过程、解决过最复杂的问题等等，锻炼自己的表达能力，也为后续的面试做了准备。\\n\\n#### 9、自主优化\\n\\n鱼皮的求职指南.md\\n我有 X 段 [学习方向] 项目经历，比如负责 [项目名称] 的开发和上线。曾经遇到了 [具体技术难题，如系统响应速度慢、数据处理效率低等]。我凭借对 [相关技术原理，如算法优化、缓存机制等] 的深入理解，提出并实施了 [具体解决方案，如采用新算法重构代码、引入分布式缓存系统等]，使得系统性能得到显著提升，[具体数据指标，如响应时间缩短 X%、数据处理效率提高 X% 等]，保障了项目的顺利交付。\\n\\n我密切关注行业前沿技术发展，不断学习新知识和技能，提升自己的技术水平，比如最近很火的 AI 技术，也有在项目中实践。我对贵公司正在推进的 [相关业务领域或技术方向] 十分感兴趣，深信自身积累的技术经验与专业能力，能够快速融入团队，为贵公司创造更大价值。\\n\\n当然，你也可以直接把个人情况和岗位描述投喂给 AI，让 AI 帮你生成一段自我介绍，美滋滋~\\n\\n![](https://pic.yupi.icu/1/1741412519933-d2e9d9ea-399a-47b0-abab-4448be91944b-20250312104435640.png)\\n\\n#### 专业技能\\n\\n正常来说，面试官问的问题会和岗位描述匹配，会问很多工作需要的技能。对于技术岗位，要熟练掌握编程语言、技术框架、算法、数据结构、还有常用的软件工具等；对于非技术岗位，则要在沟通技巧、团队协作、问题解决等软实力方面下功夫。\\n\\n对专业技能的准备不用提前很久，我的建议是平时没事儿每天看个几道题目，面试前再突击一下就行，准备得太早了反而容易忘掉。现在有很多工具可以辅助面试准备，比如一些专门练习算法的网站，还有我带团队做的程序员面试刷题神器 [面试鸭](https://www.mianshiya.com/)，帮大家整理了企业常问的高频面试真题并原创了优质题解，不用自己花时间整理题目、不用看鱼龙混杂的答案，节省出来的时间又能准备至少几十道题目了，弯道超车，想想面试时遇到原题的感觉有多爽；还能和其他同学一起交流，提升坚持学习的动力。\\n\\n![](https://pic.yupi.icu/1/1741412641059-a16ce2bf-ca8d-44a7-a700-02a93cc586fb-20250312104435804.png)\\n\\n鱼皮的项目学习建议.md\\n1. [API 开放平台](https://www.codefather.cn/course/1790979723916521474)：学习实践前后端模板开发 + 架构设计 + SDK 开发 + API 签名认证 + Dubbo RPC + Gateway 微服务网关。\\n2. [OJ 判题系统](https://www.codefather.cn/course/1790980707917017089)：学习实践前后端模板开发 + 多种设计模式 + 单体项目微服务改造 + Linux 虚拟机远程开发 + Docker 代码沙箱 + Java 安全控制。\\n3. [手写 RPC 框架](https://www.codefather.cn/course/1768543954720022530)：从 0 到 1 开发轮子，实践网络协议设计 + 序列化 + Etcd 注册中心 + Vert.x 服务器 + 动态代理 + SPI 机制 + 负载均衡 + 服务重试容错机制 + 注解驱动启动器，大幅提升架构设计能力。\\n4. [亿级流量点赞系统（25年最新）](https://www.codefather.cn/course/1912696290659577857)：实践高并发 + 高性能 + 高可用 + 可观测的分布式点赞系统架构设计，涉及 Spring Boot 3 + Java 21 + TiDB + Redis + Pulsar + Docker + Nginx  技术，全面掌握企业级系统开发与优化经验。\\n5. [AI 自动回复工具](https://github.com/liyupi/yu-auto-reply)：基于中介者模式实现的小项目，支持灵活配置多个社交平台的数据监控和回答，并且利用 AI 自动回复。（该项目暂无完整教程，可根据开源代码和文档学习）\\n\\n#### 其他 - 前端实战\\n\\n这些项目以前端为核心，也融合了大量系统设计的方法，适合前端方向、或者后端已经熟练的全栈开发者学习。\"\r\n" + 
				"  } ],\r\n" + 
				"  \"temperature\" : 0.1,\r\n" + 
				"  \"stream\" : false,\r\n" + 
				"  \"max_tokens\" : 25500\r\n" + 
				"}\r\n" + 
				"" ;
		
		String sys = "你是一名智能家居管家，只能从下面工具列表里挑选，输出 JSON。\r\n" + 
				"工具列表：\r\n" + 
				"[\r\n" + 
				"  {\"name\":\"bedroomLight\",\"desc\":\"主卧灯\",\"state\":\"on|off\",\"args\":{\"action\":\"on|off\"}},\r\n" + 
				"  {\"name\":\"ac\",\"desc\":\"空调\",\"args\":{\"action\":\"on|off|set_temp\",\"temp\":16-30}}\r\n" + 
				"]\r\n" + 
				
				"当用户询问当前设备状态，请输出：{\"state\":{\"bedroomLight\":\"{{on|off}}\",\"temp\":{{state}}\r\n" +
				"当用需要调整设备状态，请输出："+"{\"plan\":[{\"tool\":\"bedroomLight\",\"action\":\"on\"}]}\r\n"+
				
				"当前状态：{\"bedroomLight_state\":\"off\",\"ac\":\"off\",\"temp_state\":25\"}\r\n";
		String pld = msg.getPayloadStr();
		if (Convert.isNullOrEmpty(pld))
			return null;

		VLLMCtrl_M owner = (VLLMCtrl_M) this.getOwnRelatedModule();
		StringBuilder failedr = new StringBuilder();
		if (!owner.isParamReady(failedr))
		{
			RT_DEBUG_ERR.fire("owner", "Owner Module Param Not Ready:" + failedr);
			return null;
		}

		String url_base = owner.getVLLMUrlBase();

		OkHttpClient client = new OkHttpClient();

		JSONObject req_jo = new JSONObject();
		JSONArray msg_jarr = new JSONArray();
		msg_jarr.put(Map.of("role", "system", "content", sys));

		req_jo.put("model", owner.getModelName())
				.put("messages",
						List.of(Map.of("role", "system", "content", sys),
								Map.of("role", "user", "content", pld)))
				.put("max_tokens", 5120).put("temperature", 0.1);

		System.out.println("req jo="+req_jo.toString(4)) ;
		
		ChatCompletionRequest cc_req= ChatCompletionRequest.builder()
				.addUserMessage(pld)
				.build();
		
		String jo_str = JsonUtil.toJson(cc_req) ;
		ResponseFormat rf = ResponseFormat.builder()
				.type(ResponseFormatType.JSON)
				//.jsonSchema(JsonSchema.builder().name("abc").build())
				.build();
		
		Request request = new Request.Builder().url(url_base + "/v1/chat/completions")
				.post(RequestBody.create(
						jo_str //test_jo_str // req_jo.toString(),
						,MediaType.get("application/json"))).build();
		try (Response response = client.newCall(request).execute())
		{
			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);
			String responseBody = response.body().string();
			JSONObject tmpjo = new JSONObject(responseBody) ;
			System.out.println(" resp jo"+ tmpjo.toString(4)) ;
			
			ChatCompletionResponse resp = JsonUtil.fromJson(responseBody,ChatCompletionResponse.class);
			// System.out.println() ;
			
			//String txt = aim.text() ;
			//MNMsg m = new MNMsg().asPayloadJO(responseBody);
			if(resp.choices() .size()>0)
			{
				String txt = resp.choices().get(0).message().content() ;
				MNMsg m = new MNMsg().asPayload(txt);

				return RTOut.createOutIdx().asIdxMsg(0, m);
			}
		}
		return null ;
	}
}
