<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.core.devtree.*,
	java.util.*"%><!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>等高对齐 · 3D等轴多层时序切片系统</title>
    <!-- 引入 Three.js -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #020a17;
            color: #ffffff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            overflow: hidden;
            width: 100vw;
            height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        /* 1. 顶部水平时间轴 (紧贴顶部) */
        #timeline-container {
            width: 90%;
            height: 60px;
            margin-top: 10px;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: space-between;
            z-index: 10;
        }

        .timeline-axis {
            position: absolute;
            width: 100%;
            height: 2px;
            background: linear-gradient(90deg, rgba(0,242,254,0) 0%, rgba(0,242,254,1) 15%, rgba(0,242,254,1) 85%, rgba(0,242,254,0) 100%);
            top: 50%;
            transform: translateY(-50%);
            z-index: -1;
        }

        .time-node {
            display: flex;
            flex-direction: column;
            align-items: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .node-dot {
            width: 6px;
            height: 6px;
            background-color: #00f2fe;
            border-radius: 50%;
            box-shadow: 0 0 8px #00f2fe;
            transition: all 0.3s ease;
        }

        .node-label {
            margin-top: 6px;
            font-size: 11px;
            color: #70a1ff;
            user-select: none;
            transition: all 0.3s ease;
        }

        .time-node.active .node-dot {
            width: 12px;
            height: 12px;
            background-color: #ff4757;
            box-shadow: 0 0 15px #ff4757;
        }
        .time-node.active .node-label {
            color: #ffffff;
            font-weight: bold;
            font-size: 12px;
        }

        /* 2. Three.js 容器：高度占比 85vh（>80%） */
        #canvas-container {
            width: 100%;
            height: 85vh; 
            position: relative;
            z-index: 1;
        }

        #hidden-canvases {
            display: none;
        }
    </style>
</head>
<body>

    <!-- 1. 顶部水平时间轴 -->
    <div id="timeline-container">
        <div class="timeline-axis"></div>
    </div>

    <!-- 2. Three.js 3D 渲染画布 -->
    <div id="canvas-container"></div>

    <!-- 离屏贴图 Canvas 缓存 -->
    <div id="hidden-canvases"></div>

<script>
    const timeData = [
        { id: 0, time: "00:00", title: "凌晨系统低谷运行", value: 12, color: "#9c88ff", waveFreq: 0.01 },
        { id: 1, time: "02:00", title: "夜间备份任务启动", value: 45, color: "#1e90ff", waveFreq: 0.02 },
        { id: 2, time: "04:00", title: "常规安全威胁扫描", value: 28, color: "#1e90ff", waveFreq: 0.015 },
        { id: 3, time: "06:00", title: "清晨例行指标重置", value: 18, color: "#2ed573", waveFreq: 0.012 },
        { id: 4, time: "08:00", title: "早班交接与初始化", value: 55, color: "#2ed573", waveFreq: 0.03 },
        { id: 5, time: "10:00", title: "上午业务高峰监控", value: 89, color: "#ffa502", waveFreq: 0.06 },
        { id: 6, time: "12:00", title: "午间资源过载保护", value: 62, color: "#ffa502", waveFreq: 0.04 },
        { id: 7, time: "14:00", title: "下午高频交易响应", value: 78, color: "#ff4757", waveFreq: 0.055 },
        { id: 8, time: "16:00", title: "核心算力分配监控", value: 94, color: "#ff4757", waveFreq: 0.07 },
        { id: 9, time: "18:00", title: "下班负荷逐步回落", value: 40, color: "#10ac84", waveFreq: 0.025 },
        { id: 10, time: "20:00", title: "夜间作业调度开始", value: 33, color: "#10ac84", waveFreq: 0.02 },
        { id: 11, time: "22:00", title: "系统冗余节点轮换", value: 22, color: "#9c88ff", waveFreq: 0.015 }
    ];

    const timelineContainer = document.getElementById('timeline-container');
    const container = document.getElementById('canvas-container');
    const hiddenCanvases = document.getElementById('hidden-canvases');

    let scene, camera, renderer;
    let sliceMeshes = [];      
    let lineMeshes = [];       
    let nodeElements = [];     
    let offscreenCanvases = [];
    let activeIndex = -1;

    const raycaster = new THREE.Raycaster();
    const mouse = new THREE.Vector2();

    function initThree() {
        const width = container.clientWidth;
        const height = container.clientHeight;

        scene = new THREE.Scene();

        // 1. 正交相机配置 (消除近大远小)
        const aspect = width / height;
        const viewSize = 500; // 调小视口范围，迫使切片尺寸被大幅放大撑满屏幕
        camera = new THREE.OrthographicCamera(
            -viewSize * aspect / 2, viewSize * aspect / 2,
            viewSize / 2, -viewSize / 2,
            0.1, 3000
        );
        // 相机水平居中，略微朝下看
        camera.position.set(0, 100, 1000); 
        camera.lookAt(0, -120, 0);

        renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
        renderer.setSize(width, height);
        renderer.setPixelRatio(window.devicePixelRatio);
        container.appendChild(renderer.domElement);

        const totalSlices = timeData.length;
        
        // 2. 超大尺寸切片 (使视觉高度占比 >80%)
        const sliceWidth = 280;
        const sliceHeight = 360; // 大幅增加切片的高度

        // 平行堆叠间距
        const spacingX = 85;   
        const spacingZ = -90;  

        timeData.forEach((data, index) => {
            // (1) 2D 时间轴节点挂载
            const percent = 5 + (index / (totalSlices - 1)) * 90;
            const node = document.createElement('div');
            node.className = 'time-node';
            node.style.left = `\${percent}%`;
            node.style.position = 'absolute';
            node.innerHTML = `
                <div class="node-dot"></div>
                <div class="node-label">\${data.time}</div>
            `;
            node.addEventListener('click', () => fillActiveState(index));
            node.addEventListener('mouseenter', () => fillActiveState(index));
            timelineContainer.appendChild(node);
            nodeElements.push(node);

            // (2) 贴图离屏 Canvas (512x640 高分辨率比例)
            const canvas = document.createElement('canvas');
            canvas.width = 512;
            canvas.height = 640;
            hiddenCanvases.appendChild(canvas);
            offscreenCanvases.push(canvas);

            drawCanvasContent(canvas, data, false); 

            const texture = new THREE.CanvasTexture(canvas);
            const material = new THREE.MeshBasicMaterial({
                map: texture,
                transparent: true,
                opacity: 0.25, 
                side: THREE.DoubleSide,
                depthWrite: false 
            });

            // 【关键重构】：将平面网格的旋转中心转移至顶部几何边缘！
            // 默认网格的中心在 (0,0)，我们通过 translate 将网格向下移动半个高度，
            // 使得该物体的本地 (0,0) 坐标变成其“上边缘中心”。
            const geometry = new THREE.PlaneGeometry(sliceWidth, sliceHeight);
            geometry.translate(0, -sliceHeight / 2, 0); 

            const mesh = new THREE.Mesh(geometry, material);

            // 此时 posY 就是所有切片顶部边缘所在的目标高度
            // 将所有切片的 posY 设为完全一致的 200，保证切片上方绝对等高！
            const posX = (index - (totalSlices - 1) / 2) * spacingX;
            const posZ = (index - (totalSlices - 1) / 2) * spacingZ;
            const posY = 210; // 紧贴时间轴的高度位置
            
            mesh.position.set(posX, posY, posZ);

            // 完美等轴 3D 偏转角 (左外、右内)
            mesh.rotation.set(
                THREE.MathUtils.degToRad(12),  
                THREE.MathUtils.degToRad(-35), 
                THREE.MathUtils.degToRad(-5)   
            );

            mesh.renderOrder = index; 
            mesh.userData = { index: index, data: data };
            scene.add(mesh);
            sliceMeshes.push(mesh);

            // (3) 精准对齐挂接垂直线 (直接从时间轴挂载高度 240 笔直连接至切片的顶部中心 210)
            const points = [
                new THREE.Vector3(posX, 240, posZ), 
                new THREE.Vector3(posX, posY, posZ)
            ];
            const lineGeom = new THREE.BufferGeometry().setFromPoints(points);
            const lineMat = new THREE.LineBasicMaterial({
                color: 0x00f2fe,
                transparent: true,
                opacity: 0
            });
            const line = new THREE.Line(lineGeom, lineMat);
            scene.add(line);
            lineMeshes.push(line);
        });

        window.addEventListener('mousemove', onMouseMove);
        window.addEventListener('resize', onWindowResize);

        // 默认激活中间节点
        fillActiveState(Math.floor(totalSlices / 2));
    }

    // 状态切换机
    function fillActiveState(newIndex) {
        if (newIndex === activeIndex) return;
        activeIndex = newIndex;

        sliceMeshes.forEach((mesh, idx) => {
            const canvas = offscreenCanvases[idx];
            
            if (idx === activeIndex) {
                drawCanvasContent(canvas, timeData[idx], true);
                mesh.material.map.needsUpdate = true;
                mesh.material.opacity = 1.0;
                mesh.renderOrder = 999; // 提至最前，防遮挡
                
                lineMeshes[idx].material.opacity = 0.9;
                lineMeshes[idx].material.color.setHex(0xff4757);
            } else {
                drawCanvasContent(canvas, timeData[idx], false);
                mesh.material.map.needsUpdate = true;
                mesh.material.opacity = 0.22;
                mesh.renderOrder = idx; // 恢复堆叠次序
                
                lineMeshes[idx].material.opacity = 0; 
            }
        });

        nodeElements.forEach((node, idx) => {
            if (idx === activeIndex) node.classList.add('active');
            else node.classList.remove('active');
        });
    }

    // 无抖动悬浮投射判定
    function onMouseMove(event) {
        const rect = container.getBoundingClientRect();
        mouse.x = ((event.clientX - rect.left) / container.clientWidth) * 2 - 1;
        mouse.y = -((event.clientY - rect.top) / container.clientHeight) * 2 + 1;

        raycaster.setFromCamera(mouse, camera);
        const intersects = raycaster.intersectObjects(sliceMeshes);
        
        if (intersects.length > 0) {
            intersects.sort((a, b) => b.object.renderOrder - a.object.renderOrder);
            const hitMesh = intersects[0].object;
            if (hitMesh.userData.index !== activeIndex) {
                fillActiveState(hitMesh.userData.index);
            }
        }
    }

    // 巨幕切片 Canvas 内容绘制 (isActive 区分高亮和等轴叠片)
    function drawCanvasContent(canvas, data, isActive) {
        const ctx = canvas.getContext('2d');
        const W = canvas.width;
        const H = canvas.height;

        ctx.clearRect(0, 0, W, H);

        if (isActive) {
            // 激活状态：巨幕高清科幻卡片
            ctx.fillStyle = 'rgba(6, 22, 48, 0.98)';
            ctx.fillRect(0, 0, W, H);
            
            // 科技背景网格线
            ctx.strokeStyle = 'rgba(0, 242, 254, 0.15)';
            ctx.lineWidth = 1;
            for(let x=30; x<W; x+=50) { ctx.beginPath(); ctx.moveTo(x,0); ctx.lineTo(x,H); ctx.stroke(); }
            for(let y=30; y<H; y+=50) { ctx.beginPath(); ctx.moveTo(0,y); ctx.lineTo(W,y); ctx.stroke(); }

            // 高亮发光边缘
            ctx.strokeStyle = data.color;
            ctx.lineWidth = 8;
            ctx.strokeRect(15, 15, W-30, H-30);

            // 文字信息绘制
            ctx.fillStyle = '#ffffff';
            ctx.font = 'bold 38px sans-serif';
            ctx.fillText(data.time, 35, 75);

            ctx.fillStyle = '#8899a6';
            ctx.font = '18px sans-serif';
            ctx.fillText(data.title, 170, 70);

            // 科幻正弦波动图
            ctx.beginPath();
            ctx.strokeStyle = data.color;
            ctx.lineWidth = 6;
            for (let x = 30; x < W - 30; x++) {
                let y = H / 2 + 30 + Math.sin(x * data.waveFreq) * (data.value * 0.9);
                if (x === 30) ctx.moveTo(x, y);
                else ctx.lineTo(x, y);
            }
            ctx.stroke();

            // 巨幕排版核心数字
            ctx.fillStyle = '#ffffff';
            ctx.font = 'bold 84px monospace';
            ctx.textAlign = 'right';
            ctx.fillText(data.value, W - 40, H - 50);
            
            ctx.fillStyle = data.color;
            ctx.font = '20px sans-serif';
            ctx.fillText("特征分析指标", W - 150, H - 55);
            ctx.textAlign = 'left';
        } else {
            // 未激活状态：极具通透感的半透明等轴线稿卡片
            ctx.fillStyle = 'rgba(5, 15, 35, 0.75)';
            ctx.fillRect(0, 0, W, H);

            ctx.strokeStyle = 'rgba(0, 242, 254, 0.22)';
            ctx.lineWidth = 3;
            ctx.strokeRect(15, 15, W-30, H-30);

            ctx.fillStyle = 'rgba(255,255,255,0.7)';
            ctx.font = 'bold 46px sans-serif';
            ctx.fillText(data.time, 40, 85);
        }
    }

    // 完美自适应窗口大小
    function onWindowResize() {
        const width = container.clientWidth;
        const height = container.clientHeight;

        const aspect = width / height;
        const viewSize = 500;
        
        camera.left = -viewSize * aspect / 2;
        camera.right = viewSize * aspect / 2;
        camera.top = viewSize / 2;
        camera.bottom = -viewSize / 2;
        camera.updateProjectionMatrix();

        renderer.setSize(width, height);
    }

    function animate() {
        requestAnimationFrame(animate);
        renderer.render(scene, camera);
    }

    window.onload = () => {
        initThree();
        animate();
    };
</script>
</body>
</html>