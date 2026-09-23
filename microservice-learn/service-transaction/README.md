# service-transaction 交易服务

## 登录状态校验

- 所有 `/api/payment/**` 接口要求登录，未登录返回 401。
- 登录态由 Sa-Token 写入 Redis（`192.168.0.27:6379`），本模块只做校验。
- OpenFeign 调用 `service-points` 时会转发当前 `Authorization`，避免内部调用被登录拦截误伤。
- 前端收到 401 后跳转登录页，登录成功后回跳原请求页面。
