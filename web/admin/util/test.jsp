<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import = "java.io.PrintStream,java.util.* , java.io.* , java.net.*,org.iottree.core.*,
 java.security.*, javax.crypto.*,javax.crypto.spec.*

 " %><%!

%><%

%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>3D Pipeline</title>
    <style>
        canvas {
            border: 1px solid black;
        }
    </style>
</head>
<body>
    <canvas id="pipelineCanvas"></canvas>
    <script>
        const canvas = document.getElementById('pipelineCanvas');
        const ctx = canvas.getContext('2d');

        // 设置画布尺寸
        canvas.width = 600;
        canvas.height = 400;

        // 定义管道属性
        const pipelineWidth = 50; // 管道宽度

        // 绘制立体管道效果
        function draw3DPipeline(points) {
            // 清除画布
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // 创建渐变
            const gradient = ctx.createLinearGradient(0, 0, canvas.width, 0);
            gradient.addColorStop(0, 'darkgray');
            gradient.addColorStop(0.5, 'gray');
            gradient.addColorStop(1, 'darkgray');

            // 绘制管道主体
            ctx.beginPath();
            ctx.moveTo(points[0].x, points[0].y);
            for (let i = 1; i < points.length - 2; i++) {
                const xc = (points[i].x + points[i + 1].x) / 2;
                const yc = (points[i].y + points[i + 1].y) / 2;
                ctx.quadraticCurveTo(points[i].x, points[i].y, xc, yc);
            }
            // 最后两个点直接连线
            ctx.lineTo(points[points.length - 2].x, points[points.length - 2].y);
            ctx.lineTo(points[points.length - 1].x, points[points.length - 1].y);
            ctx.fillStyle = gradient;
            ctx.fill();
            ctx.closePath();

            // 绘制管道边缘
            for (let i = 0; i < points.length - 1; i++) {
                ctx.beginPath();
                ctx.moveTo(points[i].x, points[i].y);
                ctx.lineTo(points[i].x - 10, points[i].y - 10);
                ctx.lineTo(points[i + 1].x - 10, points[i + 1].y - 10);
                ctx.lineTo(points[i + 1].x, points[i + 1].y);
                ctx.fillStyle = 'black';
                ctx.fill();
                ctx.closePath();
            }
        }

        // 调用绘制函数，传入点坐标数组
        const points = [
            { x: 100, y: 400 },
            { x: 200, y: 300 },
            { x: 300, y: 200 },
            { x: 400, y: 300 },
            { x: 500, y: 400 }
        ];
        draw3DPipeline(points);
    </script>
</body>
</html>
