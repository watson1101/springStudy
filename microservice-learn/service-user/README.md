# service-user 用户与登录中心

## 登录状态校验

- 作为登录中心，负责用户登录、注册、退出和当前用户信息。
- 登录成功后把 Sa-Token 会话写入 Redis（`192.168.0.27:6379`）。
- `/api/user/auth/login`、`/api/user/auth/register` 免登录。
- 其余 `/api/user/**` 接口要求登录；用户管理接口继续使用原有 RBAC 权限注解。
- 前端收到 401 后跳转登录页，登录成功后回跳原请求页面。
