<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.service.*,
				org.iottree.core.util.web.*,
	java.io.*,org.iottree.core.util.cer.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	String prjid = request.getParameter("prjid") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;return ;
	}
	String prj_name = prj.getName() ;
%><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <title>IOT-Tree AI Chat</title>
    <jsp:include page="../head.jsp">
    	<jsp:param value="true" name="simple"/>
    </jsp:include>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', sans-serif;
            background-color:#2375cf;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 10px;
        }

        /* 主容器 - 类玻璃质感卡片 */
        .chat-container {
            width: 100%;
            max-width: 1100px;
            height: 99vh;
            background: rgba(255, 255, 255, 0.92);
            backdrop-filter: blur(2px);
            border-radius: 20px;
            box-shadow: 0 25px 45px -12px rgba(0, 0, 0, 0.25), 0 1px 2px rgba(0, 0, 0, 0.05);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.5);
        }

        /* 头部区域：连接配置 + 状态 */
        .header {
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(12px);
            padding: 12px 20px;
            border-bottom: 1px solid #e2e8f0;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: space-between;
            gap: 5px;
        }

        .ws-config {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
            flex: 2;
        }

        .ws-config label {
            font-size: 0.85rem;
            font-weight: 600;
            color: #1e293b;
            background: #f1f5f9;
            padding: 4px 10px;
            border-radius: 40px;
        }

        .ws-url-input {
            flex: 2;
            min-width: 220px;
            padding: 8px 14px;
            border-radius: 60px;
            border: 1px solid #cbd5e1;
            background: white;
            font-size: 0.85rem;
            outline: none;
            transition: 0.2s;
            font-family: monospace;
        }

        .ws-url-input:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.2);
        }

        .connect-btn {
            background: #3b82f6;
            border: none;
            color: white;
            padding: 6px 18px;
            border-radius: 40px;
            font-weight: 500;
            font-size: 0.85rem;
            cursor: pointer;
            transition: 0.2s;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
        }

        .connect-btn:hover {
            background: #2563eb;
            transform: scale(0.97);
        }

        .status-area {
            display: flex;
            align-items: center;
            gap: 12px;
            background: #f8fafc;
            padding: 5px 16px;
            border-radius: 60px;
        }

        .status-led {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #94a3b8;
            box-shadow: 0 0 0 1px white;
        }

        .status-led.connected {
            background: #22c55e;
            box-shadow: 0 0 6px #22c55e;
        }

        .status-led.disconnected {
            background: #ef4444;
        }

        .status-text {
            font-size: 0.8rem;
            font-weight: 500;
            color: #334155;
        }

        .clear-chat {
            background: transparent;
            border: 1px solid #cbd5e1;
            padding: 6px 14px;
            border-radius: 40px;
            font-size: 0.75rem;
            cursor: pointer;
            transition: 0.2s;
            color: #475569;
        }

        .clear-chat:hover {
            background: #fee2e2;
            border-color: #f87171;
            color: #b91c1c;
        }

        /* 消息区域 */
        .messages-area {
            flex: 1;
            overflow-y: auto;
            padding: 20px 24px;
            display: flex;
            flex-direction: column;
            gap: 10px;
            scroll-behavior: smooth;
            background: #ffffffcc;
        }

        /* 消息气泡 */
        .message {
            display: flex;
            flex-direction: column;
            max-width: 80%;
            animation: fadeInUp 0.2s ease;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(8px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .user-message {
            align-self: flex-end;
        }

        .assistant-message {
            align-self: flex-start;
        }

        .bubble {
            padding: 10px 16px;
            border-radius: 24px;
            line-height: 1.45;
            font-size: 0.95rem;
            word-break: break-word;
            white-space: pre-wrap;
            box-shadow: 0 1px 1px rgba(0,0,0,0.05);
        }

        .user-bubble {
            background: #3b82f6;
            color: white;
            border-bottom-right-radius: 6px;
        }

        .assistant-bubble {
            background: #f1f5f9;
            color: #0f172a;
            border-bottom-left-radius: 6px;
            border: 1px solid #e2e8f0;
        }

        .message-meta {
            font-size: 0.7rem;
            margin-top: 4px;
            margin-left: 12px;
            margin-right: 12px;
            color: #64748b;
            display: flex;
            gap: 10px;
        }

        .user-message .message-meta {
            justify-content: flex-end;
        }

        /* 输入区域 */
        .input-area {
            background: white;
            border-top: 1px solid #e2e8f0;
            padding: 16px 20px 20px 20px;
            display: flex;
            gap: 12px;
            align-items: flex-end;
        }

        .input-wrapper {
            flex: 1;
            background: #f8fafc;
            border-radius: 28px;
            border: 1px solid #e2e8f0;
            transition: 0.2s;
        }

        .input-wrapper:focus-within {
            border-color: #3b82f6;
            background: white;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        textarea {
            width: 100%;
            padding: 12px 18px;
            border: none;
            background: transparent;
            font-family: inherit;
            font-size: 0.95rem;
            line-height: 1.5;
            resize: none;
            outline: none;
            border-radius: 28px;
            max-height: 160px;
            overflow-y: auto;
        }

        .send-btn {
            background: #3b82f6;
            border: none;
            color: white;
            width: 44px;
            height: 44px;
            border-radius: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: 0.2s;
            box-shadow: 0 4px 8px rgba(59,130,246,0.3);
            flex-shrink: 0;
        }

        .send-btn:hover {
            background: #2563eb;
            transform: scale(0.96);
        }

        .send-btn:active {
            transform: scale(0.94);
        }

        .send-btn svg {
            width: 20px;
            height: 20px;
            fill: white;
        }

        .info-note {
            font-size: 0.7rem;
            text-align: center;
            margin-top: 6px;
            color: #94a3b8;
        }

        /* 滚动条美化 */
        .messages-area::-webkit-scrollbar {
            width: 5px;
        }

        .messages-area::-webkit-scrollbar-track {
            background: #eef2f5;
            border-radius: 10px;
        }

        .messages-area::-webkit-scrollbar-thumb {
            background: #cbd5e1;
            border-radius: 10px;
        }

        @media (max-width: 640px) {
            .chat-container {
                height: 95vh;
                border-radius: 24px;
            }
            .message {
                max-width: 90%;
            }
            .ws-config {
                flex-wrap: wrap;
            }
            .header {
                flex-direction: column;
                align-items: stretch;
            }
            .status-area {
                justify-content: space-between;
            }
        }
    </style>
</head>
<body>

<div class="chat-container">
   
    <div class="header">
     <%-- 
        <div class="ws-config">
            <label>🔌 WebSocket</label>
            <input type="text" id="wsUrlInput" class="ws-url-input" placeholder="ws:// 或 wss:// 地址" value="wss://echo.websocket.org">
            <button id="connectBtn" class="connect-btn">连接 / 重连</button>
        </div>
        --%>
        <div class="status-area">
            <div class="status-led" id="statusLed"></div>
            <span class="status-text" id="statusText">未连接</span>
            <button id="clearChatBtn" class="clear-chat">🗑 清空对话</button>
        </div>
    </div>

    <!-- 聊天消息展示区 -->
    <div id="messagesContainer" class="messages-area">
        <!-- 动态消息会显示在这里 -->
        <div class="assistant-message message">
            <div class="bubble assistant-bubble">
👋 你好！请先配置WebSocket服务地址并点击「连接/重连」。<br>
连接成功后，输入消息按回车即可对话（支持Ctrl+Enter换行）。
            </div>
            <div class="message-meta"><span>🤖 AI 助手</span><span>现在</span></div>
        </div>
    </div>

    <!-- 底部输入框 -->
    <div class="input-area">
        <div class="input-wrapper">
            <textarea id="message_input" rows="1" placeholder="输入消息... 按 Enter 发送，Ctrl+Enter 换行" style="overflow-y: auto;"></textarea>
        </div>
        <button id="sendButton" class="send-btn" aria-label="Send Message">
            <svg viewBox="0 0 24 24" width="20" height="20"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>
        </button>
    </div>
    <div class="info-note">
        💡 提示：连接有效WebSocket服务后，消息将发送至服务器并显示AI回复（需服务端返回文本）。回车提交，Ctrl+Enter 换行。
    </div>
</div>

<script>
var prj_name = "<%=prj_name%>" ;
        const messagesContainer = document.getElementById('messagesContainer');
        const messageInput = document.getElementById('message_input');
        const freshInput = document.getElementById('message_input');
        const sendBtn = document.getElementById('sendButton');
        const clearChatBtn = document.getElementById('clearChatBtn');
        const wsUrlInput = document.getElementById('wsUrlInput');
        const statusLed = document.getElementById('statusLed');
        const statusText = document.getElementById('statusText');

        let isConnected = false;
        

        var ws = null;
        var ws_last_chk = -1 ;
        var ws_opened = false;
        
        function log(txt)
        {
        	console.log(txt) ;
        }

        function ws_conn()
        {
        	var bhttps = location.protocol === 'https:';
            var url = (bhttps?'wss://':'ws://') + window.location.host + '/admin/_ws/chat_ai/'+prj_name;
            if ('WebSocket' in window) {
                ws = new WebSocket(url);
            } else if ('MozWebSocket' in window) {
                ws = new MozWebSocket(url);
            } else {
                log('WebSocket is not supported by this browser.');
                
                return false ;
            }
            
            ws.onopen = function () {
                //setConnected(true);
                log('Info: WebSocket connection opened.');
                ws_opened = true;
                
                console.log('WebSocket opened', event);
                updateConnectionStatus(true, '已连接');
                addSystemMessage(`✅ 连接成功 (\${url})`);
                // 可主动发送一个问候或者等待用户输入
                // 使输入框可用视觉反馈
                //messageInput.disabled = false;
                sendBtn.disabled = false;
                messageInput.placeholder = "输入消息... 按 Enter 发送";
                
            };
            ws.onmessage = function (event) {

            	let receivedData = event.data;
                if (typeof receivedData === 'string') {
                    // 可选: 如果服务端返回 JSON 格式, 自动提取内容 (更灵活)
                    let aiReplyText = receivedData;
                    try {
                        // 尝试解析JSON，提取常见字段如 content/message/text/reply
                        const parsed = JSON.parse(receivedData);
                        if (parsed.content) aiReplyText = parsed.content;
                        else if (parsed.message) aiReplyText = parsed.message;
                        else if (parsed.text) aiReplyText = parsed.text;
                        else if (parsed.reply) aiReplyText = parsed.reply;
                        // 如果都没有，保持原字符串，但可能是纯JSON展示（无所谓）
                    } catch(e) {
                        // 不是JSON，保留原始文本
                    }
                    addMessage('assistant', aiReplyText);
                } else if (event.data instanceof Blob) {
                    // 如果后端发送二进制, 尝试转文本
                    const reader = new FileReader();
                    reader.onload = function() {
                        const text = reader.result;
                        addMessage('assistant', text);
                    };
                    reader.readAsText(event.data);
                } else {
                    addMessage('assistant', '[非文本消息]');
                }
            };
            
            ws.onclose = function (event) {
            	ws_disconn();
            	console.log('WebSocket closed', event.code, event.reason);
                let closeMsg = `连接已断开 (\${event.code})`;
                if (event.reason) closeMsg += ` \${event.reason}`;
                updateConnectionStatus(false, '已断开');
                //addSystemMessage(`🔴 \${closeMsg}`, true);
                webSocket = null;
            };
            
            ws.onerror = function(error) {
                console.error('WebSocket error', error);
                updateConnectionStatus(false, '连接错误');
                //addSystemMessage(`⚠️ WebSocket 错误, 请检查网络`, true);
            };
            
            
            return true;
        }


        function ws_disconn() {
        	
            if (ws != null) {
                ws.close();
                ws = null;
            }
            ws_opened = false;
        }


        function check_ws()
        {
        	if(ws!=null&&ws_opened)
        	{
        		ws_last_chk = new Date().getTime();
        		freshInput.placeholder = "输入消息... 回车发送，Ctrl+Enter 换行";
        		return ;
        	}
        	else
        	{
        		freshInput.placeholder = "⚠️ 未连接，请先连接WebSocket服务";
        	}

        	if(ws==null)
        	{
        		ws_disconn();
        		ws_conn();
        		ws_last_chk = new Date().getTime();
        		return ;
        	}
        	
        	//ws_opened==false;
        	var dt = new Date().getTime();
        	if(dt-ws_last_chk<20000)
        		return ;
        	//time out
        	ws_disconn();
        	ws_conn();
        	ws_last_chk = new Date().getTime();
        	return ;
        }

        function reconn_ws()
        {
        	ws_disconn();
        	ws_conn();
        	ws_last_chk = new Date().getTime();
        }
        
        function send_msg(txt) {
            
            // 检查WebSocket连接状态
            if (!ws || ws.readyState !== WebSocket.OPEN)
            {
                dlg.msg('未连接');
                return;
            }
            ws.send(txt);
        }

        var chk_ws_timer = setInterval(check_ws,2000) ;
        

        // ----- 辅助函数：更新连接状态UI -----
        function updateConnectionStatus(connected, message = '') {
            isConnected = connected;
            if (connected) {
                statusLed.className = 'status-led connected';
                statusText.innerText = message || '已连接';
            } else {
                statusLed.className = 'status-led disconnected';
                statusText.innerText = message || '未连接';
            }
        }

        // ----- 添加消息到聊天区域 -----
        // role: 'user' 或 'assistant'
        // content: 文本内容
        // extraInfo: 可选 发送者名称等
        function addMessage(role, content, timeStr = null)
        {
            if (!content || content.trim() === '') return;

            // 生成时间戳
            const now = new Date();
            const timeString = timeStr || now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            
            const messageDiv = document.createElement('div');
            messageDiv.className = `message \${role === 'user' ? 'user-message' : 'assistant-message'}`;
            
            const bubbleDiv = document.createElement('div');
            bubbleDiv.className = `bubble \${role === 'user' ? 'user-bubble' : 'assistant-bubble'}`;
            // 支持纯文本换行
            bubbleDiv.innerText = content;
            
            const metaDiv = document.createElement('div');
            metaDiv.className = 'message-meta';
            const senderSpan = document.createElement('span');
            senderSpan.innerText = role === 'user' ? '👤 我' : '🤖 AI';
            const timeSpan = document.createElement('span');
            timeSpan.innerText = timeString;
            metaDiv.appendChild(senderSpan);
            metaDiv.appendChild(timeSpan);
            
            messageDiv.appendChild(bubbleDiv);
            messageDiv.appendChild(metaDiv);
            
            messagesContainer.appendChild(messageDiv);
            // 滚动到底部
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
        
        // 系统提示消息（灰色居中简短提示）
        function addSystemMessage(text, isError = false) {
            const sysDiv = document.createElement('div');
            sysDiv.style.cssText = 'text-align:center; font-size:0.75rem; color:#64748b; margin:8px 0;';
            sysDiv.innerText = text;
            if (isError) sysDiv.style.color = '#ef4444';
            messagesContainer.appendChild(sysDiv);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
            // 3秒后自动消失（可选不移除，保持信息）
            setTimeout(() => {
                if (sysDiv.parentNode) sysDiv.style.opacity = '0.6';
            }, 3000);
        }
        
        // 清空所有消息 (保留一条欢迎提示)
        function clearAllMessages() {
            while (messagesContainer.firstChild) {
                messagesContainer.removeChild(messagesContainer.firstChild);
            }
            // 添加一条默认欢迎消息（让界面不空旷）
            const welcomeDiv = document.createElement('div');
            welcomeDiv.className = 'assistant-message message';
            welcomeDiv.innerHTML = `
                <div class="bubble assistant-bubble">
                    ✨ 对话已清空。连接WebSocket后开始新的交流吧！
                </div>
                <div class="message-meta"><span>🤖 AI 助手</span><span>\${new Date().toLocaleTimeString()}</span></div>
            `;
            messagesContainer.appendChild(welcomeDiv);
        }

        // ----- 发送消息 (核心) -----
        function sendUserMessage() {
            let rawText = messageInput.value;
            console.log(rawText) ;
            if (!rawText.trim()) {
                addSystemMessage('💬 消息不能为空', false);
                return;
            }
            
            const userMsg = rawText.trim();
            // 显示用户消息
            addMessage('user', userMsg);
            
            // 通过 WebSocket 发送给服务端
            try {
               send_msg(userMsg) ;
                // 清空输入框
                messageInput.value = '';
                // 重置textarea高度
                messageInput.style.height = 'auto';
                // 保持焦点
                messageInput.focus();
            } catch (err) {
                console.error('发送失败', err);
                addSystemMessage(`发送异常: \${err.message}`, true);
                // 添加失败的系统提示，但不移除已有消息
            }
        }
        
        
        // ----- 调整textarea高度 (自动适应内容, 简约)-----
        function autoResizeTextarea() {
            messageInput.style.height = 'auto';
            let newHeight = Math.min(messageInput.scrollHeight, 160);
            messageInput.style.height = newHeight + 'px';
        }
        
        // ----- 键盘事件: 回车发送，Ctrl+Enter 换行 -----
        function onInputKeydown(e)
        {
            // 回车键 (Enter) 且 没有按下 Ctrl 键 → 发送消息
            if (e.key === 'Enter' && !e.ctrlKey) {
                //e.preventDefault();   // 阻止默认换行行为
                sendUserMessage();
                // 发送后重置高度
                messageInput.value="";
                setTimeout(() => autoResizeTextarea(), 0);
                return ;
            }
            // 如果按下 Ctrl+Enter，允许默认行为（插入换行），不做额外操作
            // 注意：Ctrl+Enter 时不做preventDefault，textarea自动换行
            //console.log(111)
        }
        
        // 监听输入事件用于自动高度和清理空余
        function onInput() {
            autoResizeTextarea();
        }
        
        // ----- 初始化UI事件绑定 -----
        function initEventListeners() {
           
            sendBtn.addEventListener('click', () => {
                sendUserMessage();
                setTimeout(() => autoResizeTextarea(), 0);
            });
            
            clearChatBtn.addEventListener('click', () => {
                clearAllMessages();
                addSystemMessage('🧹 聊天记录已清空', false);
            });
            
            messageInput.addEventListener('keydown', onInputKeydown);
            messageInput.addEventListener('input', onInput);
            
            // 初始时尝试自动调整高度
            messageInput.addEventListener('focus', autoResizeTextarea);
            window.addEventListener('resize', () => autoResizeTextarea());
            
            // 页面关闭前可选关闭WebSocket (优雅)
            window.addEventListener('beforeunload', () => {
                ws_disconn() ;
                clearInterval(chk_ws_timer) ;
            });
        }
        
        // 设置默认提示样式 + 初始状态
        function initUI() {
            updateConnectionStatus(false, '未连接');
            //messageInput.disabled = false;   // 输入框可用，但发送会校验连接
            messageInput.placeholder = "未连接服务，请先配置上方WebSocket地址并点击连接";
            // 若有默认地址显示
            if (wsUrlInput.value === 'wss://echo.websocket.org') {
                addSystemMessage('💡 当前默认使用公共 echo 测试服务器 (会原样返回消息)。如需真实AI，请修改为你的后端WebSocket地址。', false);
            }
            autoResizeTextarea();
        }
        
        // 增加额外处理：如果连接状态下发送时连接意外关闭，给出明确提示，交互友好
        // 添加手动辅助: 允许用户通过按钮重新连接
        initEventListeners();
        initUI();
        
        // 可选：预置一个方便测试的后端提示 (如果是localhost提示)
        // 另外为了防止消息重复发送的抖动，设置发送冷却简单限制（但无需严格）
        let lastSendTime = 0;
        const originalSend = sendUserMessage;
        window.sendUserMessage = sendUserMessage;
        // 重写增加节流（避免快速连击）
        sendUserMessage = function() {
            const now = Date.now();
            if (now - lastSendTime < 300) {
                addSystemMessage('请勿频繁发送', false);
                return;
            }
            lastSendTime = now;
            originalSend();
        }.bind(this);
        // 替换方法引用
        window.sendUserMessage = sendUserMessage;
        // 修改按钮和键盘事件调用的函数
        // 重新绑定确保使用新函数比较麻烦，但直接替换函数引用即可，原有监听器中调用的sendUserMessage会被更新（因为作用域提升）
        // 为确保安全，重新绑定一下关键发送调用
        const newSendBtn = sendBtn.cloneNode(true);
        sendBtn.parentNode.replaceChild(newSendBtn, sendBtn);
        // 更新全局按钮和事件
        const finalSendBtn = document.getElementById('sendButton');
        if (finalSendBtn) {
            finalSendBtn.addEventListener('click', () => {
                sendUserMessage();
                setTimeout(() => autoResizeTextarea(), 0);
            });
        }
        // 重新绑定键盘事件，避免旧闭包
        //const newTextarea = messageInput.cloneNode(true);
        //messageInput.parentNode.replaceChild(newTextarea, messageInput);
        //window.messageInput = newTextarea;
        // 更新全局引用
        /*
        if (freshInput) {
            freshInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.ctrlKey) {
                    e.preventDefault();
                    sendUserMessage();
                    setTimeout(() => autoResizeTextarea(), 0);
                }
            });
            
            freshInput.addEventListener('input', () => {
                freshInput.style.height = 'auto';
                let nh = Math.min(freshInput.scrollHeight, 160);
                freshInput.style.height = nh + 'px';
            });
        }
        // 修正全局变量指向
        window.messageInput = freshInput;
        */
        // 最后重新同步clearChat清空时不对WebSocket产生影响
        const clearBtn = document.getElementById('clearChatBtn');
        if (clearBtn) {
            // 避免重复绑定，直接替换监听
            const newClear = clearBtn.cloneNode(true);
            clearBtn.parentNode.replaceChild(newClear, clearBtn);
            newClear.addEventListener('click', () => {
                while (messagesContainer.firstChild) messagesContainer.removeChild(messagesContainer.firstChild);
                const welcomeDiv = document.createElement('div');
                welcomeDiv.className = 'assistant-message message';
                welcomeDiv.innerHTML = `<div class="bubble assistant-bubble">✨ 对话已清空。连接WebSocket后继续交流。</div><div class="message-meta"><span>🤖 AI</span><span>\${new Date().toLocaleTimeString()}</span></div>`;
                messagesContainer.appendChild(welcomeDiv);
            });
        }

    
    
    

    
</script>
</body>
</html>