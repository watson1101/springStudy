#!/usr/bin/env bash
# gen-secret.sh：从当前约定的默认值（项目一致）生成 k8s/02-app-secret.yaml
# 所有默认值均来自项目 memory / application.yml 实际配置，避免手填 base64 出错
# 用法：
#   cd microservice-learn/
#   bash k8s/scripts/gen-secret.sh            # 用默认值生成
#   bash k8s/scripts/gen-secret.sh --overwrite # 若已存在覆盖
set -euo pipefail
cd "$(dirname "$0")/../.."
OUT="k8s/02-app-secret.yaml"
if [ -f "$OUT" ]; then
  if [ "${1:-}" != "--overwrite" ]; then
    echo "[warn] ${OUT} 已存在，如需覆盖请执行 bash k8s/scripts/gen-secret.sh --overwrite"
    exit 0
  fi
fi

b64() { printf '%s' "$1" | base64 -w0; }

MYSQL_PW="${MYSQL_PASSWORD:-123456}"
NACOS_U="${NACOS_USERNAME:-nacos}"
NACOS_P="${NACOS_PASSWORD:-nacos}"
SSO_SRV_SECRET="${SSO_SERVER_SECRET:-dev-only-change-this-sso-server-secret}"
SSO_CLI_SECRET="${SSO_CLIENT_SECRET:-dev-only-change-this-sso-client-secret}"

cat >"$OUT" <<EOF
# 【自动生成：k8s/scripts/gen-secret.sh，禁止提交到 Git，已列入 .gitignore】
# 如需修改默认密码：用环境变量覆盖：
#   MYSQL_PASSWORD='xxx' NACOS_USERNAME='xxx' NACOS_PASSWORD='xxx' \
#     SSO_SERVER_SECRET='xxx' SSO_CLIENT_SECRET='xxx' \
#     bash k8s/scripts/gen-secret.sh --overwrite
apiVersion: v1
kind: Secret
metadata:
  name: ms-learn-secret
  namespace: ms-learn
  labels:
    app.kubernetes.io/part-of: ms-learn
type: Opaque
data:
  MYSQL_PASSWORD: "$(b64 "${MYSQL_PW}")"
  NACOS_USERNAME: "$(b64 "${NACOS_U}")"
  NACOS_PASSWORD: "$(b64 "${NACOS_P}")"
  SSO_SERVER_SECRET: "$(b64 "${SSO_SRV_SECRET}")"
  SSO_CLIENT_SECRET: "$(b64 "${SSO_CLI_SECRET}")"
EOF
chmod 600 "$OUT"
echo "[ok] 已生成 ${OUT}（600 权限）"
