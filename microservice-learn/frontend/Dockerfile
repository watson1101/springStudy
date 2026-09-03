# ms-frontend 多阶段构建：Vue3 + Vite -> nginx 静态托管
# Stage 1: 构建 Vite 产物
FROM node:20-alpine AS build
WORKDIR /app
# 先复制 package + lock，最大化 layer 缓存
COPY frontend/package*.json ./
# 锁版本、使用淘宝源加速服务器内网构建
RUN npm ci --registry=https://registry.npmmirror.com || npm install --registry=https://registry.npmmirror.com
COPY frontend/ ./
# Vite 构建产物输出到 dist/
RUN npm run build

# Stage 2: 托管静态页面 + /api 反代 ms-gateway
FROM nginx:1.27-alpine
# nginx alpine 自带 uid=101/gid=101 的 nginx 用户，与 K8s Deployment runAsUser=101 对齐
COPY --from=build /app/dist /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/nginx.conf
# 让 nginx 非 root 能绑定 80 端口 & 写 cache/tmp 目录
RUN chown -R 101:101 /usr/share/nginx/html /var/cache/nginx /var/run /etc/nginx \
 && chmod -R 755 /usr/share/nginx/html
USER 101
EXPOSE 80
STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]
