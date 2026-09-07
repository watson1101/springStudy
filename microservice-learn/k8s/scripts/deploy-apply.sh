#!/usr/bin/env bash
# deploy-apply.sh：在测试服务器上执行
#   1. 校验 02-app-secret.yaml 已填充真实密码（禁止使用 example）
#   2. 按顺序 kubectl apply 所有 YAML
#   3. 等待全部 Deployment 滚动升级完成 (2/2 Ready)
set -euo pipefail

cd "$(dirname "$0")/../.."
[ -d k8s ] || { echo "[err] 请在项目根目录执行"; exit 1; }

echo "=============================================="
echo " ms-learn K8s 部署"
echo "=============================================="

# ---- 1. Secret 检查 ----
SECRET_YAML="k8s/02-app-secret.yaml"
EXAMPLE_YAML="k8s/02-app-secret.example.yaml"
if [ ! -f "${SECRET_YAML}" ]; then
  echo "[err] 未找到 ${SECRET_YAML}，请从模板生成并填充真实密码："
  echo "      cp ${EXAMPLE_YAML} ${SECRET_YAML}"
  echo "      # 然后把 <REPLACE_ME_base64_of_xxx> 替换成 base64 编码的真实值"
  echo "      #   例：echo -n '123456' | base64  得到 MYSQL_PASSWORD 的 base64 值"
  echo "      #   并确保 vim 保存时没有偷偷换行"
  exit 1
fi
if grep -q 'REPLACE_ME' "${SECRET_YAML}"; then
  echo "[err] ${SECRET_YAML} 中仍存在 REPLACE_ME 占位，禁止 apply"
  exit 1
fi
echo "[1/3] Secret 检查通过"

# ---- 2. 按顺序 apply ----
echo "[2/3] kubectl apply..."
set -x
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-configmap-common.yaml
kubectl apply -f "${SECRET_YAML}"
# 业务 YAML（按文件名前缀数字顺序）
for y in k8s/1*.yaml k8s/2*.yaml; do
  kubectl apply -f "$y"
done
set +x

# ---- 3. 等待 rollout ----
echo "[3/3] 等待全部 Deployment Ready（最多 10 分钟）..."
DEPLOYS=(
  ms-gateway
  service-user
  service-order
  service-product
  service-transaction
  service-points
  service-goods
  ms-ds-system
  flowable-service
  ms-frontend
)
fail=0
for d in "${DEPLOYS[@]}"; do
  if ! kubectl -n ms-learn rollout status deploy/"${d}" --timeout=10m; then
    echo "      [!] ${d} 未在时限内就绪，查看诊断："
    echo "          kubectl -n ms-learn describe deploy/${d}"
    echo "          kubectl -n ms-learn logs deploy/${d} --tail=100"
    fail=1
  fi
done

echo "=============================================="
if [ "$fail" -eq 0 ]; then
  echo " 全部 Deployment 就绪。下一步：bash k8s/scripts/verify-deploy.sh"
else
  echo " 部署未全部就绪，请按上述诊断命令修复后重新执行：kubectl apply -f k8s/xxx.yaml"
  exit 1
fi
