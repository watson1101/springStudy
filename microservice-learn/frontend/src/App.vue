<template>
  <el-container style="min-height: 100vh">
    <el-aside width="200px" style="background:#001529">
      <div style="color:#fff;padding:16px;font-weight:600">🛠️ 微服务学习</div>
      <el-menu
        background-color="#001529"
        text-color="#ccc"
        active-text-color="#fff"
        router
        :default-active="$route.path"
      >
        <el-menu-item index="/">📋 架构总览</el-menu-item>
        <el-menu-item index="/users">👤 用户服务</el-menu-item>
        <el-menu-item index="/orders">🧾 订单服务</el-menu-item>
        <el-menu-item index="/products">🛒 商品服务</el-menu-item>
        <el-menu-item index="/goods">📦 商品管理</el-menu-item>
        <el-menu-item index="/points">🎁 积分服务</el-menu-item>
        <el-menu-item index="/payments">💳 支付服务</el-menu-item>
        <el-menu-item index="/hotnews">🔥 热点资讯</el-menu-item>
        <el-menu-item index="/system">⚙️ 系统配置</el-menu-item>
        <el-menu-item index="/flowable">🔄 工作流</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;background:#fff;box-shadow:0 1px 4px rgba(0,0,0,.1)">
        <h2 style="font-size:18px">{{ $route.meta.title || '微服务架构学习平台' }}</h2>
      </el-header>
      <el-main style="background:#f5f7fa">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
body { margin: 0; font-family: -apple-system, "PingFang SC", sans-serif; }
</style>
