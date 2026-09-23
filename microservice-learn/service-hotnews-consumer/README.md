# service-hotnews-consumer 热搜消费服务

## 登录状态校验

- 查询接口 `/api/hotnews/latest`、`/api/hotnews/batch/{batchId}` 要求登录，未登录返回 401。
- 登录态由 Sa-Token 写入 Redis（`192.168.0.27:6379`），本模块只做校验。
- RocketMQ 消费监听器不走 HTTP 拦截器，因此不受登录校验影响。
- 前端收到 401 后跳转登录页，登录成功后回跳原请求页面。
