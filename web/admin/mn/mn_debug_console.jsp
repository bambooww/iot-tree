<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","netid"))
			return ;
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
%><!DOCTYPE html>
<html >
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Canvas Console Output</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        #consoleCanvas {
            width: 100%;
            height: 400px;
            border: 1px solid #ddd;
            background-color: #f7f7f7;
        }
    </style>
</head>
<body>
    <h1>Canvas Console Output</h1>
    <canvas id="consoleCanvas" width="800" height="400"></canvas>
    <script>
        (function() {
            const canvas = document.getElementById('consoleCanvas');
            const ctx = canvas.getContext('2d');
            const logBuffer = [];
            const maxLines = 20; // Maximum number of lines to display
            const lineHeight = 20; // Height of each line
            const colors = {
                log: '#000000',
                warn: '#b58900',
                error: '#dc322f'
            };

            ctx.font = '16px Arial';
            ctx.fillStyle = colors.log;

            function drawLog() {
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                for (let i = 0; i < logBuffer.length; i++) {
                    const log = logBuffer[i];
                    ctx.fillStyle = colors[log.type];
                    ctx.fillText(log.message, 10, (i + 1) * lineHeight);
                }
            }

            function addLog(message, type) {
                logBuffer.push({ message, type });
                if (logBuffer.length > maxLines) {
                    logBuffer.shift();
                }
                drawLog();
            }

            function overrideConsoleMethod(method, type) {
                const originalMethod = console[method];
                console[method] = function(...args) {
                    const message = args.join(' ');
                    addLog(message, type);
                    originalMethod.apply(console, args);
                };
            }

            overrideConsoleMethod('log', 'log');
            overrideConsoleMethod('warn', 'warn');
            overrideConsoleMethod('error', 'error');

            // Example logs
            //console.log('This is a log message.');
           // console.warn('This is a warning message.');
           // console.error('This is an error message.');

            for (let i = 0; i < 30; i++) {
               // console.log('Log message ' + (i + 1));
            }
        })();
    </script>
</body>
</html>
