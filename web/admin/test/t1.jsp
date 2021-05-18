<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0">
    <meta name="format-detection" content="telephone=no,email=no,date=no,address=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title></title>
    <style type="text/css">
    canvas{
        position: absolute;
        border: 1px solid;
    }
    </style>
</head>
<body>
    <canvas id="canvas1" width="800" height="800"></canvas>
    <script type="text/javascript">
        var ctx1=canvas1.getContext('2d');
        var obj={
            x:100,
            y:200,
            width:200,
            height:400
        };
        /**
         * 画一个简单的长方形，让它每100毫秒旋转1度
         */
        //  浏览器中打开页面会发现，旋转是以画布的左上角为圆心的
        function rotate(){
            ctx1.clearRect(0,0,800,800);
            ctx1.fillStyle='blue';
            ctx1.rotate(Math.PI/180);
            ctx1.strokeRect(obj.x,obj.y,obj.width,obj.height);
            ctx1.fillRect(obj.x,obj.y,obj.width,obj.height);
        }
        // 在旋转之前，把画布的中心位置translate到图片的中心
        function rotate2(){
            ctx1.clearRect(0,0,800,800);
            ctx1.fillStyle='blue';
            ctx1.translate((obj.x+(obj.width/2)),(obj.y+(obj.height/2)));
            ctx1.rotate(Math.PI/180);
            ctx1.translate(-(obj.x+(obj.width/2)),-(obj.y+(obj.height/2)));
            ctx1.strokeRect(obj.x,obj.y,obj.width,obj.height);
            ctx1.fillRect(obj.x,obj.y,obj.width,obj.height);
        }
        // setInterval(rotate,100);
        setInterval(rotate2,100);
    </script>
</body>
</html>