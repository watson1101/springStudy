#!/bin/bash
# =============================================
# 一键部署脚本 — 上传 JAR 到远程服务器并重启服务
# 配合 Maven deploy profile（exec-maven-plugin）使用
#
# 用法:
#   本地调试:    bash deploy.sh <JAR文件路径> [服务器IP]
#   Maven调用:  mvn deploy -P deploy -pl <模块名> -am
#
# 参数:
#   $1 - JAR文件完整路径 (由 Maven exec 插件传入)
#   $2 - 服务器 IP 地址 (可选，默认从环境变量或配置文件读取)
# =============================================

set -e

# ===== 配置区 =====
# 默认服务器地址（可通过第二个参数覆盖）
DEFAULT_SERVER="localhost"
# 远程目录
REMOTE_PATH="/home/hong/apps/msdemo"
# SSH 用户名
SSH_USER="hong"
# ==================

# 解析参数
JAR_FILE="$1"
SERVER_HOST="${2:-$DEFAULT_SERVER}"
TARGET_HOST="${SSH_USER}@${SERVER_HOST}"
DEPLOY_LOG="deploy-$(date +%Y%m%d-%H%M%S).log"

echo ""
echo "╔═══════════════════════════════════════╗"
echo "║       一键部署脚本                    ║"
echo "╚═══════════════════════════════════════╝"
echo "  JAR:      ${JAR_FILE}"
echo "  服务器:   ${TARGET_HOST}"
echo "  远程目录: ${REMOTE_PATH}"
echo ""

# 检查 JAR 是否存在
if [ ! -f "${JAR_FILE}" ]; then
    echo "[错误] JAR 文件不存在: ${JAR_FILE}"
    echo "        请先执行: mvn package -pl <模块名> -am -DskipTests"
    exit 1
fi

JAR_NAME=$(basename "${JAR_FILE}")
echo "[1/3] 创建远程目录..."
ssh "${TARGET_HOST}" "mkdir -p ${REMOTE_PATH}"

echo "[2/3] 上传 ${JAR_NAME} 到服务器..."
scp "${JAR_FILE}" "${TARGET_HOST}:${REMOTE_PATH}/"

echo "[3/3] 重启服务..."
ssh "${TARGET_HOST}" "bash -s" << REMOTE_SCRIPT
    cd ${REMOTE_PATH}

    # 关闭旧进程
    OLD_PID=\$(pgrep -f "${JAR_NAME}" 2>/dev/null || true)
    if [ -n "\$OLD_PID" ]; then
        echo "  关闭旧进程: PID=\$OLD_PID"
        kill "\$OLD_PID" 2>/dev/null || true
        sleep 2
        # 强制杀
        kill -9 "\$OLD_PID" 2>/dev/null || true
    fi

    # 启动新服务
    echo "  启动新服务: ${JAR_NAME}"
    nohup java -jar "${REMOTE_PATH}/${JAR_NAME}" > "${REMOTE_PATH}/app.log" 2>&1 &
    NEW_PID=\$!

    # 等待几秒检查是否启动成功
    sleep 3
    if kill -0 \$NEW_PID 2>/dev/null; then
        echo "  ✓ 服务启动成功, PID=\$NEW_PID"
    else
        echo "  ✗ 服务启动失败，请检查日志: tail -100 ${REMOTE_PATH}/app.log"
        exit 1
    fi
REMOTE_SCRIPT

echo ""
echo "✓ 部署完成！"
echo "  日志: ${DEPLOY_LOG}"
echo ""