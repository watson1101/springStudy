#!/usr/bin/env bash
# build-push.sh：在测试服务器上执行
#   1. 校验 JDK17 / Maven / Docker / Registry 就绪
#   2. 按依赖顺序 mvn package -> docker build -> docker push 私有 Registry
#   3. 结束后输出 Registry 中 ms-learn/* 枚举
# 用法：
#   cd microservice-learn/
#   bash k8s/scripts/build-push.sh
# 注意：镜像仓库地址 localhost:5000 要求 /etc/docker/daemon.json 已配 insecure-registries
set -euo pipefail

REGISTRY_ADDR="${REGISTRY_ADDR:-localhost:5000}"
IMG_PREFIX="${REGISTRY_ADDR}/ms-learn"
VERSION="${VERSION:-1.0.0}"

echo "=============================================="
echo " ms-learn K8s 构建 & 推送 v${VERSION}"
echo " Registry: ${REGISTRY_ADDR}"
echo "=============================================="

# ---- 1. 基础依赖检查 ----
echo "[1/5] 检查基础依赖..."
need_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "[err] 缺少命令 $1，请先安装"; exit 1; }; }
need_cmd java
need_cmd mvn
need_cmd docker
need_cmd curl

JAVA_MAJOR=$(java -version 2>&1 | head -1 | sed -E 's/.*"([0-9]+).*/\1/')
[ "${JAVA_MAJOR}" = "17" ] || { echo "[err] 需要 JDK 17，当前 java version 输出: $(java -version 2>&1 | head -1)"; exit 1; }

# Docker daemon 可达
docker info >/dev/null 2>&1 || { echo "[err] 无法连接 Docker daemon，请启动 docker"; exit 1; }

# Registry 可访问（不强制有 TLS）
if ! curl -fsS "http://${REGISTRY_ADDR}/v2/_catalog" >/dev/null 2>&1; then
  echo "[err] 私有 Registry http://${REGISTRY_ADDR}/v2/ 不可达"
  echo "  若报 x509: certificate signed by unknown authority："
  echo "    sudo mkdir -p /etc/docker && echo '{\"insecure-registries\":[\"192.168.0.27:5000\",\"localhost:5000\"]}' | sudo tee /etc/docker/daemon.json"
  echo "    sudo systemctl daemon-reload && sudo systemctl restart docker"
  exit 1
fi
echo "      Registry OK"

# 确保当前目录是项目根（目录特征：pom.xml + k8s/）
cd "$(dirname "$0")/../.."
[ -f pom.xml ] || { echo "[err] 请在项目根目录执行，或脚本必须位于 k8s/scripts/ 下"; exit 1; }
PROOT=$(pwd)
echo "      项目根目录: ${PROOT}"

# ---- 2. Maven 打包（8 个 JAR 服务）----
echo "[2/5] Maven 打包 JAR 模块（-pl xxx -am 同时构建 common 依赖）..."
JAR_MODULES=(
  gateway
  service-user
  service-order
  service-product
  service-transaction
  service-points
  ms-ds-system
  flowable-service
)
PL_ARGS=""
for m in "${JAR_MODULES[@]}"; do
  PL_ARGS="${PL_ARGS},${m}"
done
PL_ARGS="${PL_ARGS#,}"  # 去掉开头逗号
# -T 1C 单线程避免内存高峰；-DskipTests 跳过集成测试
mvn -q -pl "${PL_ARGS}" -am clean package -DskipTests -Dmaven.test.skip=true -T 1C
echo "      打包完成"

# ---- 3. Docker build + push：8 个 JAR 服务 ----
echo "[3/5] 构建并推送 JAR 镜像（8）..."
build_jar() {
  local mod="$1"; shift
  local tag="${IMG_PREFIX}/${mod}:${VERSION}"
  echo "      -> ${tag}"
  # 每个 JAR 模块 Dockerfile 在模块目录下，构建上下文为项目根（COPY pom.xml / common / <module>）
  docker build -q -f "${mod}/Dockerfile" -t "${tag}" .
  docker push -q "${tag}" >/dev/null
}
for m in "${JAR_MODULES[@]}"; do build_jar "$m"; done

# ---- 4. Docker build + push：frontend ----
echo "[4/5] 构建并推送 frontend 镜像..."
FR_TAG="${IMG_PREFIX}/frontend:${VERSION}"
echo "      -> ${FR_TAG}"
# frontend Dockerfile 位于 frontend/ 下，构建上下文为项目根
docker build -q -f frontend/Dockerfile -t "${FR_TAG}" .
docker push -q "${FR_TAG}" >/dev/null

# ---- 5. 输出 Registry 枚举 ----
echo "[5/5] Registry ms-learn 镜像清单："
curl -fsS "http://${REGISTRY_ADDR}/v2/_catalog" \
  | python3 -c 'import json,sys; [print("     - " + r) for r in sorted(json.load(sys.stdin).get("repositories",[])) if r.startswith("ms-learn/")]'
echo "=============================================="
echo " 构建 + 推送 完成"
