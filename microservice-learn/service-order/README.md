# service-order 订单服务

## 登录状态校验

- 所有 `/api/order/**` 接口要求登录，未登录返回 401。
- 登录态由 Sa-Token 写入 Redis（`192.168.0.27:6379`），本模块只做校验。
- 创建订单时通过 OpenFeign 调用 `service-user` 的 `/api/user/{id}/exists` 校验用户存在性，并转发当前 `Authorization`。
- 前端收到 401 后跳转登录页，登录成功后回跳原请求页面。
