#!/bin/bash
# =============================================
# WSL deploy script - run inside WSL terminal
# Usage: bash deploy-wsl.sh <module-name>
# =============================================
set -e
REMOTE_PATH="/home/hong/apps/msdemo"
PROJECT_ROOT="/mnt/c/workspace/github/springStudy/ms-demo"
if [ $# -eq 0 ]; then
    echo "Usage: bash deploy-wsl.sh <module-name>"
    echo "Example: bash deploy-wsl.sh goods-service"
    exit 1
fi
MODULE="$1"
JAR_DIR="${PROJECT_ROOT}/${MODULE}/target"
JAR_FILE=$(ls "${JAR_DIR}"/*.jar 2>/dev/null | grep -v ".jar.original" | head -1)
echo ""
echo "=== WSL Deploy ==="
echo "Module: $MODULE"
echo "JAR:    $JAR_FILE"
echo ""
if [ -z "$JAR_FILE" ]; then
    echo "[ERROR] JAR not found. Build first."
    exit 1
fi
JAR_NAME=$(basename "$JAR_FILE")
echo "[1/2] Copying JAR..."
mkdir -p "$REMOTE_PATH"
cp "$JAR_FILE" "$REMOTE_PATH/"
echo "[2/2] Restarting..."
OLD_PID=$(pgrep -f "$JAR_NAME" 2>/dev/null || true)
if [ -n "$OLD_PID" ]; then
    echo "  Stopping PID=$OLD_PID"
    kill "$OLD_PID" 2>/dev/null || true
    sleep 2
fi
cd "$REMOTE_PATH"
nohup java -jar "$JAR_NAME" > app.log 2>&1 &
NEW_PID=$!
sleep 3
if kill -0 $NEW_PID 2>/dev/null; then
    echo "  OK PID=$NEW_PID"
else
    echo "  FAILED. Check: tail -50 $REMOTE_PATH/app.log"
    exit 1
fi
echo ""
echo "Done! PID=$NEW_PID"
