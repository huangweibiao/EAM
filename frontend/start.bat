@echo off
REM EAM 前端启动脚本 (Windows)

echo 正在启动 EAM 前端开发服务器...

REM 检查Node.js环境
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 Node.js，请确保已安装 Node.js 18+
    exit /b 1
)

REM 检查npm环境
where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 npm
    exit /b 1
)

REM 进入前端目录
cd /d "%~dp0"

REM 检查node_modules是否存在
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
)

REM 启动开发服务器
echo 正在启动 Vite 开发服务器...
call npm run dev

pause
