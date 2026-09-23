# service-hotnews-collector 热搜采集服务

## 登录状态校验

- 本模块属于内部任务服务，不接入登录拦截。
- 定时采集和手动触发接口 `/api/hotnews/collect`、`/api/hotnews/config` 均免登录。
- 消息由 RocketMQ 异步投递，不经过 HTTP 登录校验。
