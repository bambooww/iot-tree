<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%>
	<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>VibeVoice · 智能语音 (本地服务)</title>
    <!-- Font Awesome 图标库 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" />
    <style>
        /* 样式与之前一致，略微调整按钮状态 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: radial-gradient(ellipse at 50% 0%, #1a1a2e 0%, #16213e 50%, #0f0f1f 100%);
            padding: 20px;
        }

        .vibe-card {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 48px;
            padding: 48px 40px 40px;
            max-width: 700px;
            width: 100%;
            border: 1px solid rgba(255, 255, 255, 0.08);
            box-shadow: 0 30px 80px rgba(0, 0, 0, 0.6), inset 0 1px 0 rgba(255, 255, 255, 0.05);
            transition: transform 0.3s ease;
        }

        .vibe-card:hover {
            transform: translateY(-4px);
        }

        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 32px;
            flex-wrap: wrap;
            gap: 12px;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .brand-icon {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #a78bfa, #7c3aed);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: #fff;
            box-shadow: 0 8px 24px rgba(124, 58, 237, 0.35);
        }

        .brand h1 {
            font-size: 26px;
            font-weight: 700;
            color: #ffffff;
            letter-spacing: -0.5px;
        }

        .brand h1 span {
            background: linear-gradient(135deg, #a78bfa, #60a5fa);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .status-badge {
            display: flex;
            align-items: center;
            gap: 8px;
            background: rgba(167, 139, 250, 0.15);
            padding: 8px 18px;
            border-radius: 100px;
            border: 1px solid rgba(167, 139, 250, 0.2);
            color: #c4b5fd;
            font-size: 13px;
            font-weight: 500;
            letter-spacing: 0.3px;
        }

        .status-badge .dot {
            width: 8px;
            height: 8px;
            background: #34d399;
            border-radius: 50%;
            animation: pulse-dot 2s infinite;
        }

        @keyframes pulse-dot {
            0%,
            100% {
                opacity: 1;
                transform: scale(1);
            }
            50% {
                opacity: 0.4;
                transform: scale(0.8);
            }
        }

        .input-section {
            margin-bottom: 28px;
        }

        .input-section label {
            display: block;
            font-size: 14px;
            font-weight: 500;
            color: #d1d5db;
            margin-bottom: 10px;
            letter-spacing: 0.3px;
        }

        .input-wrapper {
            position: relative;
        }

        .input-wrapper textarea {
            width: 100%;
            height: 140px;
            padding: 20px 24px;
            background: rgba(255, 255, 255, 0.06);
            border: 1px solid rgba(255, 255, 255, 0.10);
            border-radius: 20px;
            color: #f3f4f6;
            font-size: 16px;
            font-family: inherit;
            resize: vertical;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
            line-height: 1.6;
            outline: none;
        }

        .input-wrapper textarea::placeholder {
            color: #6b7280;
            font-weight: 400;
        }

        .input-wrapper textarea:focus {
            border-color: #a78bfa;
            box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.15);
        }

        .char-count {
            position: absolute;
            bottom: 14px;
            right: 18px;
            font-size: 12px;
            color: #6b7280;
            background: rgba(15, 15, 31, 0.7);
            padding: 2px 12px;
            border-radius: 100px;
            backdrop-filter: blur(4px);
            pointer-events: none;
        }

        .controls {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px 24px;
            margin-bottom: 28px;
        }

        .control-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .control-group label {
            font-size: 13px;
            font-weight: 500;
            color: #9ca3af;
            letter-spacing: 0.2px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .control-group label i {
            font-size: 14px;
            color: #7c3aed;
        }

        .control-group select,
        .control-group input[type="range"] {
            width: 100%;
            background: rgba(255, 255, 255, 0.06);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 12px;
            padding: 10px 14px;
            color: #f3f4f6;
            font-size: 14px;
            font-family: inherit;
            outline: none;
            transition: border-color 0.3s ease;
            appearance: none;
            -webkit-appearance: none;
        }

        .control-group select {
            cursor: pointer;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%239ca3af' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 14px center;
            padding-right: 40px;
        }

        .control-group select option {
            background: #1a1a2e;
            color: #f3f4f6;
        }

        .control-group select:focus {
            border-color: #a78bfa;
        }

        .control-group input[type="range"] {
            padding: 0;
            height: 6px;
            border: none;
            border-radius: 100px;
            background: rgba(255, 255, 255, 0.10);
            cursor: pointer;
        }

        .control-group input[type="range"]::-webkit-slider-thumb {
            -webkit-appearance: none;
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: linear-gradient(135deg, #a78bfa, #7c3aed);
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
            transition: transform 0.2s ease;
        }

        .control-group input[type="range"]::-webkit-slider-thumb:hover {
            transform: scale(1.15);
        }

        .control-group input[type="range"]::-moz-range-thumb {
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: linear-gradient(135deg, #a78bfa, #7c3aed);
            cursor: pointer;
            border: none;
            box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
        }

        .range-labels {
            display: flex;
            justify-content: space-between;
            font-size: 11px;
            color: #6b7280;
            padding: 0 2px;
        }

        .actions {
            display: flex;
            gap: 14px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .btn {
            flex: 1;
            min-width: 140px;
            padding: 16px 28px;
            border: none;
            border-radius: 18px;
            font-size: 16px;
            font-weight: 600;
            font-family: inherit;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            cursor: pointer;
            transition: all 0.25s ease;
            outline: none;
            position: relative;
            overflow: hidden;
        }

        .btn-primary {
            background: linear-gradient(135deg, #7c3aed, #6d28d9);
            color: #fff;
            box-shadow: 0 8px 28px rgba(124, 58, 237, 0.35);
        }

        .btn-primary:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 12px 36px rgba(124, 58, 237, 0.45);
        }

        .btn-primary:active:not(:disabled) {
            transform: translateY(0);
            box-shadow: 0 4px 16px rgba(124, 58, 237, 0.3);
        }

        .btn-primary:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        .btn-secondary {
            background: rgba(255, 255, 255, 0.06);
            color: #d1d5db;
            border: 1px solid rgba(255, 255, 255, 0.08);
        }

        .btn-secondary:hover:not(:disabled) {
            background: rgba(255, 255, 255, 0.12);
            color: #ffffff;
            transform: translateY(-2px);
        }

        .btn-secondary:disabled {
            opacity: 0.3;
            cursor: not-allowed;
            transform: none;
        }

        .btn .spinner {
            display: none;
            width: 20px;
            height: 20px;
            border: 2.5px solid rgba(255, 255, 255, 0.2);
            border-top-color: #fff;
            border-radius: 50%;
            animation: spin 0.7s linear infinite;
        }

        .btn.loading .spinner {
            display: inline-block;
        }

        .btn.loading .btn-text {
            display: none;
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }

        .playback-status {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 18px;
            background: rgba(255, 255, 255, 0.03);
            border-radius: 16px;
            border: 1px solid rgba(255, 255, 255, 0.05);
            min-height: 52px;
            flex-wrap: wrap;
            gap: 10px;
        }

        .status-left {
            display: flex;
            align-items: center;
            gap: 14px;
            color: #9ca3af;
            font-size: 14px;
        }

        .status-left .wave {
            display: flex;
            align-items: center;
            gap: 3px;
            height: 24px;
        }

        .status-left .wave span {
            display: inline-block;
            width: 3px;
            height: 100%;
            background: #7c3aed;
            border-radius: 100px;
            animation: wave 1s ease-in-out infinite;
        }

        .status-left .wave span:nth-child(2) {
            animation-delay: 0.15s;
            height: 70%;
        }
        .status-left .wave span:nth-child(3) {
            animation-delay: 0.3s;
            height: 40%;
        }
        .status-left .wave span:nth-child(4) {
            animation-delay: 0.45s;
            height: 80%;
        }
        .status-left .wave span:nth-child(5) {
            animation-delay: 0.6s;
            height: 50%;
        }

        @keyframes wave {
            0%,
            100% {
                transform: scaleY(0.4);
            }
            50% {
                transform: scaleY(1);
            }
        }

        .status-left .wave.paused span {
            animation-play-state: paused;
            opacity: 0.3;
        }

        .status-text {
            font-weight: 500;
        }

        .status-text .highlight {
            color: #a78bfa;
        }

        .status-right {
            display: flex;
            align-items: center;
            gap: 12px;
            color: #6b7280;
            font-size: 13px;
        }

        .status-right .time {
            font-variant-numeric: tabular-nums;
            background: rgba(255, 255, 255, 0.05);
            padding: 2px 12px;
            border-radius: 100px;
        }

        .footer {
            margin-top: 28px;
            text-align: center;
            color: #4b5563;
            font-size: 13px;
            letter-spacing: 0.2px;
            border-top: 1px solid rgba(255, 255, 255, 0.04);
            padding-top: 20px;
        }

        .footer a {
            color: #8b5cf6;
            text-decoration: none;
            transition: color 0.2s;
        }

        .footer a:hover {
            color: #a78bfa;
            text-decoration: underline;
        }

        @media (max-width: 640px) {
            .vibe-card {
                padding: 28px 20px 24px;
                border-radius: 32px;
            }
            .brand h1 {
                font-size: 20px;
            }
            .brand-icon {
                width: 40px;
                height: 40px;
                font-size: 18px;
            }
            .controls {
                grid-template-columns: 1fr;
                gap: 14px;
            }
            .actions {
                flex-direction: column;
            }
            .btn {
                min-width: unset;
                padding: 14px 20px;
            }
            .header {
                flex-direction: column;
                align-items: flex-start;
            }
            .status-badge {
                align-self: flex-start;
            }
            .playback-status {
                flex-direction: column;
                align-items: flex-start;
            }
            .status-right {
                width: 100%;
                justify-content: flex-start;
            }
        }

        @media (max-width: 400px) {
            .vibe-card {
                padding: 20px 14px 18px;
                border-radius: 24px;
            }
            .input-wrapper textarea {
                height: 100px;
                padding: 14px 16px;
                font-size: 14px;
            }
        }
    </style>
</head>
<body>

    <div class="vibe-card">
        <!-- 头部 -->
        <div class="header">
            <div class="brand">
                <div class="brand-icon">
                    <i class="fas fa-waveform"></i>
                </div>
                <h1>Vibe<span>Voice</span></h1>
            </div>
            <div class="status-badge">
                <span class="dot"></span>
                <span id="serviceStatus">本地服务 · 就绪</span>
            </div>
        </div>

        <!-- 文本输入 -->
        <div class="input-section">
            <label for="textInput">
                <i class="fas fa-pen" style="margin-right:6px;color:#7c3aed;"></i> 输入你想朗读的文字
            </label>
            <div class="input-wrapper">
                <textarea id="textInput" placeholder="在这里输入文字，VibeVoice 将为你朗读…" maxlength="5000">你好，欢迎使用 VibeVoice 智能语音合成。在这里输入任何文字，点击播放即可听到自然流畅的语音输出。</textarea>
                <span class="char-count" id="charCount">0 / 5000</span>
            </div>
        </div>

        <!-- 控制栏 -->
        <div class="controls">
            <!-- 音色选择 -->
            <div class="control-group">
                <label for="voiceSelect"><i class="fas fa-user-voice"></i> 音色</label>
                <select id="voiceSelect">
                    <!-- 由 JavaScript 动态填充常用音色，也可根据服务端响应添加 -->
                </select>
            </div>
            <!-- 语速 -->
            <div class="control-group">
                <label for="rateRange"><i class="fas fa-tachometer-alt"></i> 语速</label>
                <input type="range" id="rateRange" min="0.5" max="2.0" step="0.05" value="1.0" />
                <div class="range-labels">
                    <span>慢</span>
                    <span id="rateValue">1.0×</span>
                    <span>快</span>
                </div>
            </div>
            <!-- 音调 -->
            <div class="control-group">
                <label for="pitchRange"><i class="fas fa-music"></i> 音调</label>
                <input type="range" id="pitchRange" min="0.5" max="2.0" step="0.05" value="1.0" />
                <div class="range-labels">
                    <span>低沉</span>
                    <span id="pitchValue">1.0×</span>
                    <span>高亢</span>
                </div>
            </div>
            <!-- 占位 -->
            <div class="control-group" style="opacity:0.6;">
                <label><i class="fas fa-volume-up"></i> 音量</label>
                <div style="display:flex;align-items:center;height:40px;color:#6b7280;font-size:13px;padding-left:4px;">
                    <i class="fas fa-check-circle" style="color:#34d399;margin-right:8px;"></i> 系统音量控制
                </div>
            </div>
        </div>

        <!-- 操作按钮 -->
        <div class="actions">
            <button class="btn btn-primary" id="speakBtn">
                <span class="btn-text"><i class="fas fa-play"></i> 播放语音</span>
                <span class="spinner"></span>
            </button>
            <button class="btn btn-secondary" id="stopBtn" disabled>
                <i class="fas fa-stop"></i> 停止
            </button>
            <button class="btn btn-secondary" id="clearBtn">
                <i class="fas fa-undo-alt"></i> 清空
            </button>
        </div>

        <!-- 播放状态 -->
        <div class="playback-status" id="playbackStatus">
            <div class="status-left">
                <div class="wave" id="waveIcon">
                    <span></span><span></span><span></span><span></span><span></span>
                </div>
                <span class="status-text" id="statusText">
                    <span class="highlight">●</span> 等待输入…
                </span>
            </div>
            <div class="status-right">
                <span class="time" id="timeDisplay">00:00</span>
                <span id="wordCount">0 字</span>
            </div>
        </div>

        <!-- 底部 -->
        <div class="footer">
            <span>语音由本地服务 <strong>localhost:3000</strong> 合成 · <span id="audioFormat">格式未知</span></span>
        </div>
    </div>

    <script>
        (function() {
            'use strict';

            // ---- DOM 引用 ----
            const textInput = document.getElementById('textInput');
            const charCount = document.getElementById('charCount');
            const voiceSelect = document.getElementById('voiceSelect');
            const rateRange = document.getElementById('rateRange');
            const pitchRange = document.getElementById('pitchRange');
            const rateValue = document.getElementById('rateValue');
            const pitchValue = document.getElementById('pitchValue');
            const speakBtn = document.getElementById('speakBtn');
            const stopBtn = document.getElementById('stopBtn');
            const clearBtn = document.getElementById('clearBtn');
            const statusText = document.getElementById('statusText');
            const timeDisplay = document.getElementById('timeDisplay');
            const wordCount = document.getElementById('wordCount');
            const waveIcon = document.getElementById('waveIcon');
            const serviceStatus = document.getElementById('serviceStatus');
            const audioFormat = document.getElementById('audioFormat');

            // ---- 本地服务端点 ----
            const API_URL = 'http://localhost:3000/v1/audio/speech';

            // ---- 音色预设 (根据常见TTS模型，可自行增删) ----
            const PRESET_VOICES = [
                { id: 'alloy', name: 'Alloy (中性)' },
                { id: 'echo', name: 'Echo (沉稳)' },
                { id: 'fable', name: 'Fable (叙事)' },
                { id: 'onyx', name: 'Onyx (深沉)' },
                { id: 'nova', name: 'Nova (明亮)' },
                { id: 'shimmer', name: 'Shimmer (柔和)' },
            ];

            // 填充音色下拉框
            function populateVoiceSelect() {
                voiceSelect.innerHTML = '';
                PRESET_VOICES.forEach(v => {
                    const opt = document.createElement('option');
                    opt.value = v.id;
                    opt.textContent = v.name;
                    voiceSelect.appendChild(opt);
                });
                // 默认选中第一个
                voiceSelect.value = PRESET_VOICES[0]?.id || '';
            }
            populateVoiceSelect();

            // ---- 音频播放状态 ----
            let audioElement = null; // 当前播放的 Audio 对象
            let isPlaying = false;
            let isPaused = false;
            let timerInterval = null;
            let elapsedSeconds = 0;
            let startTime = 0;

            // ---- 辅助函数 ----
            function updateCharCount() {
                const len = textInput.value.length;
                charCount.textContent = `\${len} / 5000`;
                wordCount.textContent = `\${len} 字`;
            }

            function formatTime(sec) {
                const m = Math.floor(sec / 60);
                const s = Math.floor(sec % 60);
                return `\${String(m).padStart(2, '0')}:\${String(s).padStart(2, '0')}`;
            }

            function updateStatusText(text, isHighlight = false) {
                if (isHighlight) {
                    statusText.innerHTML = `<span class="highlight">\${text}</span>`;
                } else {
                    statusText.textContent = text;
                }
            }

            function setWaveState(active) {
                if (active) {
                    waveIcon.classList.remove('paused');
                } else {
                    waveIcon.classList.add('paused');
                }
            }

            function stopTimer() {
                if (timerInterval) {
                    clearInterval(timerInterval);
                    timerInterval = null;
                }
            }

            function startTimer() {
                stopTimer();
                startTime = performance.now() - elapsedSeconds * 1000;
                timerInterval = setInterval(() => {
                    const now = performance.now();
                    elapsedSeconds = (now - startTime) / 1000;
                    timeDisplay.textContent = formatTime(elapsedSeconds);
                }, 200);
            }

            function resetTimer() {
                stopTimer();
                elapsedSeconds = 0;
                timeDisplay.textContent = '00:00';
            }

            // ---- 核心：调用本地服务合成语音 ----
            async function speakText() {
                const text = textInput.value.trim();
                if (!text) {
                    updateStatusText('⚠️ 请先输入文字', false);
                    textInput.style.borderColor = '#ef4444';
                    setTimeout(() => textInput.style.borderColor = '', 800);
                    return;
                }

                // 如果正在播放，先停止
                if (isPlaying) {
                    stopSpeaking();
                }

                // 禁用按钮，显示加载状态
                speakBtn.classList.add('loading');
                speakBtn.disabled = true;
                stopBtn.disabled = true; // 停止按钮在加载时不可用
                updateStatusText('⏳ 正在合成语音…', false);
                setWaveState(false);

                const voice = voiceSelect.value;
                const speed = parseFloat(rateRange.value);
                const pitch = parseFloat(pitchRange.value);

                // 构造请求体 (根据服务端需求，这里假设接受 JSON)
                const payload = {
                    input: text,
                    voice: voice,
                    speed: speed,
                    pitch: pitch, // 如果服务端不支持，可忽略
                };

                try {
                    const response = await fetch(API_URL, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(payload),
                        mode: 'cors', // 显式启用 CORS
                    });

                    if (!response.ok) {
                        let errorMsg = `服务端错误 (\${response.status})`;
                        try {
                            const errData = await response.json();
                            if (errData && errData.error) {
                                errorMsg = errData.error;
                            }
                        } catch (_) { /* ignore */ }
                        throw new Error(errorMsg);
                    }

                    // 获取音频数据 (Blob)
                    const contentType = response.headers.get('content-type') || 'audio/mpeg';
                    const blob = await response.blob();

                    // 显示音频格式
                    audioFormat.textContent = contentType;

                    // 创建 Audio 对象并播放
                    const audioUrl = URL.createObjectURL(blob);
                    if (audioElement) {
                        audioElement.pause();
                        audioElement.src = '';
                        URL.revokeObjectURL(audioElement.src);
                    }
                    audioElement = new Audio(audioUrl);
                    audioElement.volume = 1.0;

                    // ---- 事件监听 ----
                    audioElement.onplay = function() {
                        isPlaying = true;
                        isPaused = false;
                        speakBtn.classList.add('loading');
                        speakBtn.disabled = true;
                        stopBtn.disabled = false;
                        setWaveState(true);
                        updateStatusText('🔊 正在播放…', true);
                        resetTimer();
                        startTimer();
                    };

                    audioElement.onended = function() {
                        isPlaying = false;
                        isPaused = false;
                        speakBtn.classList.remove('loading');
                        speakBtn.disabled = false;
                        stopBtn.disabled = true;
                        setWaveState(false);
                        updateStatusText('✅ 播放完成', false);
                        stopTimer();
                        timeDisplay.textContent = formatTime(elapsedSeconds);
                        // 清理 URL
                        URL.revokeObjectURL(audioUrl);
                    };

                    audioElement.onerror = function(e) {
                        console.error('Audio error:', e);
                        isPlaying = false;
                        isPaused = false;
                        speakBtn.classList.remove('loading');
                        speakBtn.disabled = false;
                        stopBtn.disabled = true;
                        setWaveState(false);
                        stopTimer();
                        updateStatusText('❌ 音频播放失败', false);
                        URL.revokeObjectURL(audioUrl);
                    };

                    // 开始播放
                    await audioElement.play();

                } catch (error) {
                    console.error('请求失败:', error);
                    speakBtn.classList.remove('loading');
                    speakBtn.disabled = false;
                    stopBtn.disabled = true;
                    setWaveState(false);
                    stopTimer();
                    updateStatusText(`❌ \${error.message || '合成请求失败'}`, false);
                    // 恢复按钮文字
                    speakBtn.innerHTML = `<span class="btn-text"><i class="fas fa-play"></i> 播放语音</span><span class="spinner"></span>`;
                }
            }

            // ---- 停止播放 ----
            function stopSpeaking() {
                if (audioElement) {
                    audioElement.pause();
                    audioElement.currentTime = 0;
                    // 不撤销 URL 以免影响后续
                }
                isPlaying = false;
                isPaused = false;
                speakBtn.classList.remove('loading');
                speakBtn.disabled = false;
                stopBtn.disabled = true;
                setWaveState(false);
                stopTimer();
                updateStatusText('⏹ 已停止', false);
                speakBtn.innerHTML = `<span class="btn-text"><i class="fas fa-play"></i> 播放语音</span><span class="spinner"></span>`;
            }

            // ---- 清空 ----
            function clearText() {
                if (isPlaying) {
                    stopSpeaking();
                }
                textInput.value = '';
                updateCharCount();
                resetTimer();
                updateStatusText('✏️ 已清空', false);
                setWaveState(false);
                timeDisplay.textContent = '00:00';
                speakBtn.innerHTML = `<span class="btn-text"><i class="fas fa-play"></i> 播放语音</span><span class="spinner"></span>`;
                speakBtn.classList.remove('loading');
                speakBtn.disabled = false;
                stopBtn.disabled = true;
                textInput.focus();
            }

            // ---- 更新显示值 ----
            rateRange.addEventListener('input', function() {
                rateValue.textContent = parseFloat(this.value).toFixed(2) + '×';
            });
            pitchRange.addEventListener('input', function() {
                pitchValue.textContent = parseFloat(this.value).toFixed(2) + '×';
            });

            textInput.addEventListener('input', updateCharCount);

            // ---- 按钮绑定 ----
            speakBtn.addEventListener('click', speakText);

            stopBtn.addEventListener('click', function() {
                stopSpeaking();
                // 额外清理
                if (audioElement) {
                    audioElement.pause();
                    audioElement.currentTime = 0;
                }
                isPlaying = false;
                speakBtn.classList.remove('loading');
                speakBtn.disabled = false;
                stopBtn.disabled = true;
                setWaveState(false);
                stopTimer();
                updateStatusText('⏹ 已停止', false);
                speakBtn.innerHTML = `<span class="btn-text"><i class="fas fa-play"></i> 播放语音</span><span class="spinner"></span>`;
            });

            clearBtn.addEventListener('click', clearText);

            // ---- 键盘快捷键 ----
            textInput.addEventListener('keydown', function(e) {
                if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
                    e.preventDefault();
                    speakText();
                }
            });

            // ---- 页面关闭时清理 ----
            window.addEventListener('beforeunload', function() {
                if (audioElement) {
                    audioElement.pause();
                    audioElement.src = '';
                }
                if (timerInterval) {
                    clearInterval(timerInterval);
                }
            });

            // ---- 初始化 ----
            updateCharCount();
            updateStatusText('● 等待输入…', true);
            setWaveState(false);
            stopBtn.disabled = true;
            rateValue.textContent = '1.00×';
            pitchValue.textContent = '1.00×';
            audioFormat.textContent = '等待合成';

            // 检查服务是否可用（可选，发送一个空请求？这里不自动检测）
            serviceStatus.textContent = '本地服务 · 就绪';

            console.log('🎙️ VibeVoice 已连接本地服务:', API_URL);
        })();
    </script>

</body>
</html>