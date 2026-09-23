# ms-ds-system 系统配置服务

## 登录状态校验

- `/api/system/**` 默认要求登录，未登录返回 401。
- 以下 GET 只读接口免登录，供服务间调用与探测使用：
  - `/api/system/health`
  - `/api/system/dict/type/{dictType}`
  - `/api/system/dict/tree/{dictType}`
  - `/api/system/dict/item/{itemId}`
- 字典写接口、配置管理接口和 CDC 开关接口仍要求登录。
- 前端收到 401 后跳转登录页，登录成功后回跳原请求页面。
